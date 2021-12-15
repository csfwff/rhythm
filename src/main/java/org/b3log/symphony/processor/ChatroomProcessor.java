/*
 * Rhythm - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Modified version from Symphony, Thanks Symphony :)
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.b3log.symphony.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.WebSocketSession;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.*;
import org.b3log.symphony.model.*;
import org.b3log.symphony.processor.channel.ChatroomChannel;
import org.b3log.symphony.processor.middleware.AnonymousViewCheckMidware;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.processor.middleware.validate.ChatMsgAddValidationMidware;
import org.b3log.symphony.repository.ChatRoomRepository;
import org.b3log.symphony.service.*;
import org.b3log.symphony.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pers.adlered.simplecurrentlimiter.main.SimpleCurrentLimiter;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Chatroom processor.
 * <ul>
 * <li>Shows chatroom (/cr), GET</li>
 * <li>Sends chat message (/chat-room/send), POST</li>
 * </ul>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Feb 11, 2020
 * @since 1.4.0
 */
@Singleton
public class ChatroomProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ChatroomProcessor.class);

    private static final Pattern AT_USER_PATTERN = Pattern.compile("(@)([a-zA-Z0-9 ]+)");


    private static final String PARTICIPANTS = "participants";
    /**
     * Chat messages.
     */
    public static LinkedList<JSONObject> messages = new LinkedList<>();

    /**
     * Data model service.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * User management service.
     */
    @Inject
    private UserMgmtService userMgmtService;

    /**
     * Short link query service.
     */
    @Inject
    private ShortLinkQueryService shortLinkQueryService;

    /**
     * Notification query service.
     */
    @Inject
    private NotificationQueryService notificationQueryService;

    /**
     * Notification management service.
     */
    @Inject
    private NotificationMgmtService notificationMgmtService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Comment management service.
     */
    @Inject
    private CommentMgmtService commentMgmtService;

    /**
     * Comment query service.
     */
    @Inject
    private CommentQueryService commentQueryService;

    /**
     * Article query service.
     */
    @Inject
    private ArticleQueryService articleQueryService;

    /**
     * Chat Room Repository.
     */
    @Inject
    private ChatRoomRepository chatRoomRepository;

    /**
     * Liveness management service.
     */
    @Inject
    private LivenessMgmtService livenessMgmtService;

    /**
     * Pointtransfer management service.
     */
    @Inject
    private PointtransferMgmtService pointtransferMgmtService;

    /**
     * Chat Room Service.
     */
    @Inject
    private ChatRoomService chatRoomService;

    /**
     * Cloud service.
     */
    @Inject
    private CloudService cloudService;

    @Inject
    private ChatroomChannel chatroomChannel;

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);
        final AnonymousViewCheckMidware anonymousViewCheckMidware = beanManager.getReference(AnonymousViewCheckMidware.class);
        final ChatMsgAddValidationMidware chatMsgAddValidationMidware = beanManager.getReference(ChatMsgAddValidationMidware.class);

        final ChatroomProcessor chatroomProcessor = beanManager.getReference(ChatroomProcessor.class);
        Dispatcher.post("/chat-room/send", chatroomProcessor::addChatRoomMsg, loginCheck::handle, chatMsgAddValidationMidware::handle);
        Dispatcher.get("/cr", chatroomProcessor::showChatRoom, anonymousViewCheckMidware::handle);
        Dispatcher.get("/chat-room/more", chatroomProcessor::getMore);
        Dispatcher.get("/chat-room/online-users", chatroomProcessor::getChatRoomUsers);
        Dispatcher.get("/cr/raw/{id}", chatroomProcessor::getChatRaw, anonymousViewCheckMidware::handle);
        Dispatcher.delete("/chat-room/revoke/{oId}", chatroomProcessor::revokeMessage, loginCheck::handle);
        Dispatcher.post("/chat-room/red-packet/open", chatroomProcessor::openRedPacket, loginCheck::handle);
    }


    /**
     * 获取聊天室在线人数
     *
     * @param context
     */
    public void getChatRoomUsers(final RequestContext context) {
        final JSONObject online = chatroomChannel.getOnline();
        JSONObject ret = new JSONObject();
        ret.put(Keys.CODE, StatusCodes.SUCC);
        ret.put(Keys.MSG, "");
        ret.put(Keys.DATA, online);
        context.renderJSON(ret);
    }

    /**
     * 拆开红包
     *
     * @param context
     */
    public synchronized void openRedPacket(final RequestContext context) {
        try {
            JSONObject currentUser = Sessions.getUser();
            try {
                final JSONObject requestJSONObject = context.requestJSON();
                currentUser = ApiProcessor.getUserByKey(requestJSONObject.optString("apiKey"));
            } catch (NullPointerException ignored) {
            }
            String userId = currentUser.optString(Keys.OBJECT_ID);
            String userName = currentUser.optString(User.USER_NAME);
            final JSONObject requestJSONObject = context.requestJSON();
            String oId = requestJSONObject.optString("oId");
            JSONObject msg = chatRoomService.getChatMsg(oId);
            JSONObject redPacket = new JSONObject(new JSONObject(msg.optString("content")).optString("content"));
            JSONObject info = new JSONObject();
            JSONObject sender = userQueryService.getUser(redPacket.optString("senderId"));
            info.put(UserExt.USER_AVATAR_URL, sender.optString(UserExt.USER_AVATAR_URL));
            info.put(User.USER_NAME, sender.optString(User.USER_NAME));
            info.put("count", redPacket.optInt("count"));
            info.put("got", redPacket.optInt("got"));
            info.put("msg", redPacket.optString("msg"));
            JSONArray recivers;
            if (!redPacket.has("recivers") || StringUtils.isBlank(redPacket.optString("recivers"))) {
                recivers = new JSONArray();
            } else {
                recivers = new JSONArray(redPacket.optString("recivers"));
            }

            String msgType = redPacket.optString("msgType");
            if (msgType.equals("redPacket")) {
                // 红包正常，可以抢了
                int money = redPacket.optInt("money");
                int countMoney = money;
                int count = redPacket.optInt("count");
                int got = redPacket.optInt("got");
                JSONArray who = redPacket.optJSONArray("who");
                // 根据抢的人数判断是否已经抢光了
                if (got >= count) {
                    context.renderJSON(new JSONObject().put("who", who).put("info", info).put("recivers", recivers));
                    return;
                }
                // 开始领取红包
                int meGot = 0;
                if (redPacket.has("type") && "average".equals(redPacket.getString("type"))) {
                    // 普通红包逻辑
                    for (Object o : who) {
                        JSONObject currentWho = (JSONObject) o;
                        String uId = currentWho.optString("userId");
                        if (uId.equals(userId)) {
                            context.renderJSON(new JSONObject().put("who", who).put("info", info));
                            return;
                        }
                    }
                    meGot = money;
                } else if (redPacket.has("type") && "specify".equals(redPacket.getString("type"))) {
                    //专属红包逻辑
                    final boolean isReciver = recivers.toList().stream().anyMatch(x -> {
                        final String reciver = (String) x;
                        if (reciver.equals(userName)) {
                            return true;
                        } else {
                            return false;
                        }
                    });
                    if (!isReciver) {
                        context.renderJSON(new JSONObject().put("who", who).put("info", info).put("recivers", recivers));
                        return;
                    }
                    for (Object o : who) {
                        JSONObject currentWho = (JSONObject) o;
                        String uId = currentWho.optString("userId");
                        if (uId.equals(userId)) {
                            context.renderJSON(new JSONObject().put("who", who).put("info", info).put("recivers", recivers));
                            return;
                        }
                    }
                    meGot = money;
                } else {
                    boolean hasZero = false;
                    for (Object o : who) {
                        JSONObject currentWho = (JSONObject) o;
                        String uId = currentWho.optString("userId");
                        if (uId.equals(userId)) {
                            context.renderJSON(new JSONObject().put("who", who).put("info", info));
                            return;
                        }
                        int userMoney = currentWho.optInt("userMoney");
                        if (userMoney == 0) {
                            hasZero = true;
                        }
                        money -= userMoney;
                    }
                    if (RED_PACKET_BUCKET.isEmpty() || !RED_PACKET_BUCKET.containsKey(oId)) {
                        //服务器重启或者宕机导致的红包缓存失效，走原来的逻辑
                        // 随机一个红包金额 1-N
                        Random random = new Random();
                        // 如果是最后一个红包了，给他一切
                        int coefficient = 2;
                        if ((countMoney / 2) <= money) {
                            coefficient = 1;
                        }
                        if (money > 0) {
                            if (count == got + 1) {
                                meGot = money;
                            } else {
                                if (!hasZero) {
                                    meGot = random.nextInt((money / coefficient) + 1);
                                } else {
                                    meGot = random.nextInt((money / coefficient) + 1) + 1;
                                }
                            }
                        }
                    } else {
                        meGot = RED_PACKET_BUCKET.get(oId).packs.poll();
                    }
                }
                // 随机成功了
                // 修改聊天室数据库
                JSONObject source = new JSONObject(chatRoomService.getChatMsg(oId).optString("content"));
                JSONObject source2 = new JSONObject(source.optString("content"));
                source2.put("got", got + 1);
                JSONArray source3 = source2.optJSONArray("who");
                source3.put(new JSONObject().put("userMoney", meGot).put("userId", userId).put("userName", userName).put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())).put("avatar", userQueryService.getUser(userId).optString(UserExt.USER_AVATAR_URL)));
                source2.put("who", source3);
                source.put("content", source2);
                final Transaction transaction = chatRoomRepository.beginTransaction();
                chatRoomRepository.update(oId, new JSONObject().put("content", source.toString()));
                transaction.commit();
                // 把钱转给用户
                final boolean succ = null != pointtransferMgmtService.transfer(Pointtransfer.ID_C_SYS, userId,
                        Pointtransfer.TRANSFER_TYPE_C_ACTIVITY_RECEIVE_RED_PACKET,
                        meGot, "", System.currentTimeMillis(), "");
                if (!succ) {
                    context.renderJSON(StatusCodes.ERR).renderMsg("发送积分失败");
                    return;
                }
                info.put("got", redPacket.optInt("got") + 1);
                context.renderJSON(new JSONObject().put("who", source3).put("info", info).put("recivers", recivers));
                // 广播红包情况
                JSONObject redPacketStatus = new JSONObject();
                redPacketStatus.put(Common.TYPE, "redPacketStatus");
                redPacketStatus.put("whoGive", source.optString(User.USER_NAME));
                redPacketStatus.put("whoGot", userName);
                redPacketStatus.put("got", got + 1);
                redPacketStatus.put("count", count);
                redPacketStatus.put("oId", oId);

                if ("random".equals(redPacket.getString("type")) && redPacketStatus.optInt("got") == redPacketStatus.optInt("count")) {
                    RED_PACKET_BUCKET.remove(oId);
                }

                ChatroomChannel.notifyChat(redPacketStatus);
                return;
            }
        } catch (Exception e) {
            context.renderJSON(StatusCodes.ERR).renderMsg("红包非法");
            LOGGER.log(Level.ERROR, "Open Red Packet failed", e);
        }
        context.renderJSON(StatusCodes.ERR).renderMsg("红包非法");
    }


    /**
     * Adds a chat message.
     * <p>
     * The request json object (a chat message):
     * <pre>
     * {
     *     "content": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified context
     */
    final private static SimpleCurrentLimiter chatRoomLivenessLimiter = new SimpleCurrentLimiter(30, 1);

    public synchronized void addChatRoomMsg(final RequestContext context) {
        final JSONObject requestJSONObject = (JSONObject) context.attr(Keys.REQUEST);
        String content = requestJSONObject.optString(Common.CONTENT);

        try {
            JSONObject checkContent = new JSONObject(content);
            if (checkContent.optString("msgType").equals("redPacket")) {
                context.renderJSON(StatusCodes.ERR).renderMsg("你想干嘛？");
                return;
            }
        } catch (Exception ignored) {
        }

        JSONObject currentUser = Sessions.getUser();
        try {
            currentUser = ApiProcessor.getUserByKey(requestJSONObject.optString("apiKey"));
        } catch (NullPointerException ignored) {
        }
        final String userName = currentUser.optString(User.USER_NAME);

        final long time = System.currentTimeMillis();
        JSONObject msg = new JSONObject();
        msg.put(User.USER_NAME, userName);
        msg.put(UserExt.USER_AVATAR_URL, currentUser.optString(UserExt.USER_AVATAR_URL));
        msg.put(Common.CONTENT, content);
        msg.put(Common.TIME, time);
        msg.put(UserExt.USER_NICKNAME, currentUser.optString(UserExt.USER_NICKNAME));
        msg.put("sysMetal", cloudService.getEnabledMetal(currentUser.optString(Keys.OBJECT_ID)));

        // 加活跃
        try {
            String userId = currentUser.optString(Keys.OBJECT_ID);
            if (chatRoomLivenessLimiter.access(userId)) {
                livenessMgmtService.incLiveness(userId, Liveness.LIVENESS_COMMENT);
            }
        } catch (Exception ignored) {
        }

        if (content.startsWith("[redpacket]") && content.endsWith("[/redpacket]")) {
            try {
                String redpacketString = content.replaceAll("^\\[redpacket\\]", "").replaceAll("\\[/redpacket\\]$", "");
                JSONObject redpacket = new JSONObject(redpacketString);
                String type = redpacket.optString("type");
                if (StringUtils.isBlank(type)) {
                    type = "random";
                }
                int money = redpacket.optInt("money");
                int count = redpacket.optInt("count");
                String recivers = redpacket.optString("recivers");
                String message = redpacket.optString("msg");
                message = message.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5,，.。！!?？《》\\s]", "");
                if (message.length() > 20) {
                    message = message.substring(0, 20);
                }
                String userId = currentUser.optString(Keys.OBJECT_ID);
                // 扣积分
                if (money > 20000 || money < 32 || count > 1000 || count <= 0 || count > money) {
                    context.renderJSON(StatusCodes.ERR).renderMsg("数据不合法！");
                    return;
                }
                try {
                    int toatlMoney = 0;
                    switch (type) {
                        case "average":
                            toatlMoney = money * count;
                            break;
                        case "specify":
                            if (StringUtils.isNotBlank(recivers)) {
                                final JSONArray reciverArray = new JSONArray(recivers);
                                final int length = reciverArray.length();
                                if (length > 0) {
                                    toatlMoney = money * length;
                                } else {
                                    context.renderJSON(StatusCodes.ERR).renderMsg("专属红包需要指定用户！");
                                    return;
                                }
                            } else {
                                context.renderJSON(StatusCodes.ERR).renderMsg("专属红包需要指定用户！");
                                return;
                            }
                            break;
                        case "random":
                        default:
                            toatlMoney = money;
                    }
                    final boolean succ = null != pointtransferMgmtService.transfer(userId, Pointtransfer.ID_C_SYS,
                            Pointtransfer.TRANSFER_TYPE_C_ACTIVITY_SEND_RED_PACKET,
                            toatlMoney, "", System.currentTimeMillis(), "");
                    if (!succ) {
                        context.renderJSON(StatusCodes.ERR).renderMsg("少年，你的积分不足！");
                        return;
                    }
                } catch (Exception e) {
                    context.renderJSON(StatusCodes.ERR).renderMsg("少年，你的积分不足！");
                    return;
                }
                // 组合新的 JSON
                JSONObject redPacketJSON = new JSONObject();
                redPacketJSON.put("senderId", userId);
                redPacketJSON.put("type", type);
                redPacketJSON.put("money", money);
                redPacketJSON.put("count", count);
                redPacketJSON.put("msg", message);
                redPacketJSON.put("recivers", recivers);
                // 已经抢了这个红包的人数
                redPacketJSON.put("got", 0);
                // 已经抢了这个红包的人以及抢到的金额
                redPacketJSON.put("who", new JSONArray());
                // 红包特殊标识，堵漏洞
                redPacketJSON.put("msgType", "redPacket");

                // 写入数据库
                final Transaction transaction = chatRoomRepository.beginTransaction();
                try {
                    msg.put(Common.CONTENT, redPacketJSON.toString());
                    String oId = chatRoomRepository.add(new JSONObject().put("content", msg.toString()));
                    msg.put("oId", oId);
                } catch (RepositoryException e) {
                    LOGGER.log(Level.ERROR, "Cannot save ChatRoom message to the database.", e);
                }
                transaction.commit();
                RED_PACKET_BUCKET.put(msg.optString("oId"), allocateRedPacket(msg.optString("oId"), userId, money, count, 2));
                final JSONObject pushMsg = JSONs.clone(msg);
                pushMsg.put(Common.TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.optLong(Common.TIME)));
                ChatroomChannel.notifyChat(pushMsg);

                try {
                    final JSONObject user = userQueryService.getUser(userId);
                    user.put(UserExt.USER_LATEST_CMT_TIME, System.currentTimeMillis());
                    userMgmtService.updateUser(userId, user);
                } catch (final Exception e) {
                    LOGGER.log(Level.ERROR, "Update user latest comment time failed", e);
                }

                context.renderJSON(StatusCodes.SUCC);
            } catch (Exception e) {
                LOGGER.log(Level.INFO, "User " + userName + " failed to send a red packet.");
            }
        } else {
            // 聊天室内容保存到数据库
            final Transaction transaction = chatRoomRepository.beginTransaction();
            try {
                String oId = chatRoomRepository.add(new JSONObject().put("content", msg.toString()));
                msg.put("oId", oId);
            } catch (RepositoryException e) {
                LOGGER.log(Level.ERROR, "Cannot save ChatRoom message to the database.", e);
            }
            transaction.commit();
            msg = msg.put("md", msg.optString(Common.CONTENT)).put(Common.CONTENT, processMarkdown(msg.optString(Common.CONTENT)));
            final JSONObject pushMsg = JSONs.clone(msg);
            pushMsg.put(Common.TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.optLong(Common.TIME)));
            ChatroomChannel.notifyChat(pushMsg);

            context.renderJSON(StatusCodes.SUCC);


            try {
                final List<JSONObject> atUsers = atUsers(msg.optString(Common.CONTENT), userName);
                if (Objects.nonNull(atUsers) && !atUsers.isEmpty()) {
                    for (JSONObject user : atUsers) {
                        final JSONObject notification = new JSONObject();
                        notification.put(Notification.NOTIFICATION_USER_ID, user.optString("oId"));
                        notification.put(Notification.NOTIFICATION_DATA_ID, msg.optString("oId"));
                        notificationMgmtService.addChatRoomAtNotification(notification);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, "notify user failed", e);
            }

            try {
                final String userId = currentUser.optString(Keys.OBJECT_ID);
                final JSONObject user = userQueryService.getUser(userId);
                user.put(UserExt.USER_LATEST_CMT_TIME, System.currentTimeMillis());
                userMgmtService.updateUser(userId, user);
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Update user latest comment time failed", e);
            }
        }
    }

    private List<JSONObject> atUsers(String content, String currentUser) {
        final Document document = Jsoup.parse(content);
        final Elements elements = document.select("p");
        if (elements.isEmpty()) return new ArrayList<>();
        List<JSONObject> users = new ArrayList<>();
        final Set<String> userNames = new HashSet<>();
        for (Element element : elements) {
            String text = element.text();
            if (StringUtils.isBlank(text) || !text.contains("@")) {
                continue;
            }
            final Matcher matcher = AT_USER_PATTERN.matcher(text);


            while (matcher.find()) {
                String userName;
                String raw = matcher.group(2);
                //认为raw文本直到遇到空格 为用户名称
                raw = raw.trim();
                final int blank = raw.indexOf(" ");
                if (blank < 0) {
                    userName = raw;
                } else {
                    userName = raw.substring(0, blank);
                }
                if (userName.equals(PARTICIPANTS)) {
                    //需要@所有在聊天室中的成员
                    final Map<WebSocketSession, JSONObject> onlineUsers = ChatroomChannel.onlineUsers;
                    return onlineUsers.values().stream().filter(x -> !x.optString(User.USER_NAME).equals(currentUser)).collect(Collectors.toList());
                }
                userNames.add(userName);
            }
        }
        userNames.forEach(name -> {
            final JSONObject user = userQueryService.getUserByName(name);
            if (Objects.nonNull(user)) {
                users.add(user);
            }
        });
        return users;
    }

    /**
     * Shows chatroom.
     *
     * @param context the specified context
     */
    public void showChatRoom(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "chat-room.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModel.put(Common.MESSAGES, getMessages(1));
        dataModel.put(Common.ONLINE_CHAT_CNT, 0);
        final JSONObject currentUser = Sessions.getUser();
        if (null != currentUser) {
            dataModel.put(UserExt.CHAT_ROOM_PICTURE_STATUS, currentUser.optInt(UserExt.CHAT_ROOM_PICTURE_STATUS));
            dataModel.put("level3Permitted", DataModelService.hasPermission(currentUser.optString(User.USER_ROLE), 3));
            // 通知标为已读
            notificationMgmtService.makeRead(currentUser.optString(Keys.OBJECT_ID), Notification.DATA_TYPE_C_CHAT_ROOM_AT);
        } else {
            dataModel.put(UserExt.CHAT_ROOM_PICTURE_STATUS, UserExt.USER_XXX_STATUS_C_ENABLED);
            dataModel.put("level3Permitted", false);
        }
        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

    /**
     * Show chat message raw.
     *
     * @param context the specified context
     */
    public void getChatRaw(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "raw.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        String id = context.pathVar("id");
        Query query = new Query().setFilter(new PropertyFilter(Keys.OBJECT_ID, FilterOperator.EQUAL, id));
        try {
            JSONObject object = chatRoomRepository.getFirst(query);
            String content = new JSONObject(object.optString("content")).optString("content");
            dataModel.put("raw", content);
        } catch (RepositoryException e) {
            context.renderCodeMsg(StatusCodes.ERR, "Invalid chat id.");
            return;
        }
    }

    /**
     * Get more chat room histories.
     *
     * @param context
     */
    public void getMore(final RequestContext context) {
        try {
            int page = Integer.parseInt(context.param("page"));
            JSONObject currentUser = Sessions.getUser();
            try {
                currentUser = ApiProcessor.getUserByKey(context.param("apiKey"));
            } catch (NullPointerException ignored) {
            }
            if (null == currentUser) {
                if (page >= 3) {
                    context.sendError(401);
                    context.abort();
                    return;
                }
            }
            List<JSONObject> jsonObject = getMessages(page);
            JSONObject ret = new JSONObject();
            ret.put(Keys.CODE, StatusCodes.SUCC);
            ret.put(Keys.MSG, "");
            ret.put(Keys.DATA, jsonObject);
            context.renderJSON(ret);
        } catch (Exception e) {
            context.sendStatus(500);
        }
    }

    /**
     * 撤回消息（直接删除）
     *
     * @param context
     */
    private static Map<String, String> revoke = new HashMap<>();

    public void revokeMessage(final RequestContext context) {
        try {
            String removeMessageId = context.pathVar("oId");
            JSONObject message = chatRoomRepository.get(removeMessageId);
            JSONObject currentUser = Sessions.getUser();
            try {
                final JSONObject requestJSONObject = context.requestJSON();
                currentUser = ApiProcessor.getUserByKey(requestJSONObject.optString("apiKey"));
            } catch (NullPointerException ignored) {
            }
            String content = message.optString("content");
            try {
                JSONObject checkContent = new JSONObject(new JSONObject(content).optString("content"));
                if (checkContent.optString("msgType").equals("redPacket")) {
                    context.renderJSON(StatusCodes.ERR).renderMsg("你想干嘛？");
                    return;
                }
            } catch (Exception ignored) {
            }
            String msgUser = new JSONObject(message.optString("content")).optString(User.USER_NAME);
            String curUser = currentUser.optString(User.USER_NAME);
            boolean isAdmin = DataModelService.hasPermission(currentUser.optString(User.USER_ROLE), 3);

            if (isAdmin) {
                final Transaction transaction = chatRoomRepository.beginTransaction();
                chatRoomRepository.remove(removeMessageId);
                transaction.commit();
                context.renderJSON(StatusCodes.SUCC).renderMsg("撤回成功。");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Common.TYPE, "revoke");
                jsonObject.put("oId", removeMessageId);
                ChatroomChannel.notifyChat(jsonObject);
                return;
            } else if (msgUser.equals(curUser)) {
                final String date = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd");
                if (revoke.get(curUser) == null || !revoke.get(curUser).equals(date)) {
                    final Transaction transaction = chatRoomRepository.beginTransaction();
                    chatRoomRepository.remove(removeMessageId);
                    transaction.commit();
                    context.renderJSON(StatusCodes.SUCC).renderMsg("撤回成功，下次发消息一定要三思哦！");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Common.TYPE, "revoke");
                    jsonObject.put("oId", removeMessageId);
                    ChatroomChannel.notifyChat(jsonObject);
                    revoke.put(curUser, date);
                    return;
                } else {
                    context.renderJSON(StatusCodes.ERR).renderMsg("撤回失败，你每天只有一次撤回的机会！");
                }
            }
        } catch (Exception e) {
            context.renderJSON(StatusCodes.ERR).renderMsg("撤回失败，请联系 @adlered。");
        }
    }

    /**
     * Get all messages from database.
     *
     * @return
     */
    public static List<JSONObject> getMessages(int page) {
        try {
            final BeanManager beanManager = BeanManager.getInstance();
            final ChatRoomRepository chatRoomRepository = beanManager.getReference(ChatRoomRepository.class);
            List<JSONObject> messageList = chatRoomRepository.getList(new Query()
                    .setPage(page, 25)
                    .addSort(Keys.OBJECT_ID, SortDirection.DESCENDING));
            List<JSONObject> msgs = messageList.stream().map(msg -> new JSONObject(msg.optString("content")).put("oId", msg.optString(Keys.OBJECT_ID))).collect(Collectors.toList());
            msgs = msgs.stream().map(msg -> JSONs.clone(msg).put(Common.TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.optLong(Common.TIME)))).collect(Collectors.toList());
            msgs = msgs.stream().map(msg -> JSONs.clone(msg.put("content", processMarkdown(msg.optString("content"))))).collect(Collectors.toList());
            return msgs;
        } catch (RepositoryException e) {
            return new LinkedList<>();
        }
    }

    private static String processMarkdown(String content) {
        try {
            JSONObject checkContent = new JSONObject(content);
            if (checkContent.optString("msgType").equals("redPacket")) {
                return content;
            }
        } catch (Exception ignored) {
        }

        final BeanManager beanManager = BeanManager.getInstance();
        final ShortLinkQueryService shortLinkQueryService = beanManager.getReference(ShortLinkQueryService.class);
        content = shortLinkQueryService.linkArticle(content);
        content = Emotions.toAliases(content);
        content = Emotions.convert(content);
        content = Markdowns.toHTML(content);
        content = Markdowns.clean(content, "");
        content = MediaPlayers.renderAudio(content);
        content = MediaPlayers.renderVideo(content);

        return content;
    }

    /**
     * @param id        红包ID
     * @param sendId    发送者ID
     * @param money     总金额
     * @param count     红包的个数
     * @param zeroCount 允许出现0的次数
     * @return
     */
    private static RedPacket allocateRedPacket(String id, String sendId, int money, int count, int zeroCount) {
        if (zeroCount >= count) {
            zeroCount = 0;
        }
        int realZeroCount = 0;
        RedPacket redPacket = new RedPacket.Builder()
                .id(id)
                .sendId(sendId)
                .money(money)
                .time(System.currentTimeMillis())
                .count(count)
                .packs(new LinkedList<>())
                .build();
        if (count == 1) {
            redPacket.packs.push(money);
            return redPacket;
        }
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        int remain = money;
        for (int i = 0; i < count; i++) {
            if (remain == 0) {
                redPacket.packs.push(remain);
            } else {
                if (redPacket.packs.size() == count - 1) {
                    redPacket.packs.push(remain);
                    break;
                }
                int min = 0;
                int max = (remain / count) + 1;
                int get = random.nextInt(min, max);
                if (get == 0) {
                    if (zeroCount > 0 && zeroCount > realZeroCount) {
                        //还有0的名额
                        realZeroCount++;
                    } else {
                        get = 1;
                    }
                }
                redPacket.packs.push(get);
                remain -= get;
            }
        }
        Collections.shuffle(redPacket.packs);
        return redPacket;
    }

    public static class RedPacket {
        public String id;
        public String sendId;
        public long time;
        public LinkedList<Integer> packs;
        public int count;
        public int money;

        private RedPacket(Builder builder) {
            id = builder.id;
            sendId = builder.sendId;
            time = builder.time;
            packs = builder.packs;
            count = builder.count;
            money = builder.money;
        }


        public static final class Builder {
            private String id;
            private String sendId;
            private long time;
            private LinkedList<Integer> packs;
            private int count;
            private int money;

            public Builder() {
            }

            public Builder id(String val) {
                id = val;
                return this;
            }

            public Builder sendId(String val) {
                sendId = val;
                return this;
            }

            public Builder time(long val) {
                time = val;
                return this;
            }

            public Builder packs(LinkedList<Integer> val) {
                packs = val;
                return this;
            }

            public Builder count(int val) {
                count = val;
                return this;
            }

            public Builder money(int val) {
                money = val;
                return this;
            }

            public RedPacket build() {
                return new RedPacket(this);
            }
        }
    }

    public static final Map<String, RedPacket> RED_PACKET_BUCKET = new HashMap<>(32);
}
