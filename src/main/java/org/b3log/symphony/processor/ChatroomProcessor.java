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
        Dispatcher.get("/cr/raw/{id}", chatroomProcessor::getChatRaw, anonymousViewCheckMidware::handle);
        Dispatcher.delete("/chat-room/revoke/{oId}", chatroomProcessor::revokeMessage, loginCheck::handle);
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

        // 加活跃
        try {
            String userId = currentUser.optString(Keys.OBJECT_ID);
            if (chatRoomLivenessLimiter.access(userId)) {
                livenessMgmtService.incLiveness(userId, Liveness.LIVENESS_COMMENT);
            }
        } catch (Exception ignored) {
        }

        if (content.startsWith("[redpacket]") && content.endsWith("[/redpacket]")) {
            LOGGER.log(Level.INFO, "User " + userName + " has sent a red packet.");
            try {
                String redpacketString = content.replaceAll("^\\[redpacket\\]", "").replaceAll("\\[/redpacket\\]$", "");
                JSONObject redpacket = new JSONObject(redpacketString);
                int money = redpacket.optInt("money");
                int count = redpacket.optInt("count");
                String message = redpacket.optString("msg");
                message = message.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5,，.。！!?？《》\\s]", "");
                if (message.length() > 20) {
                    message = message.substring(0, 20);
                }
                String userId = currentUser.optString(Keys.OBJECT_ID);
                // 扣积分
                if (money > 20000) {
                    context.renderJSON(StatusCodes.ERR).renderMsg("红包金额不得大于 20000 积分！");
                    return;
                }
                try {
                    final boolean succ = null != pointtransferMgmtService.transfer(userId, Pointtransfer.ID_C_SYS,
                            Pointtransfer.TRANSFER_TYPE_C_ACTIVITY_SEND_RED_PACKET,
                            money, "", System.currentTimeMillis(), "");
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
                redPacketJSON.put("money", money);
                redPacketJSON.put("count", count);
                redPacketJSON.put("msg", message);
                // 已经抢了这个红包的人数
                redPacketJSON.put("got", 0);
                // 已经抢了这个红包的人以及抢到的金额
                redPacketJSON.put("who", new JSONArray());

                // 写入数据库
                final Transaction transaction = chatRoomRepository.beginTransaction();
                try {
                    chatRoomRepository.add(new JSONObject().put("content", redPacketJSON));
                } catch (RepositoryException e) {
                    LOGGER.log(Level.ERROR, "Cannot save ChatRoom message to the database.", e);
                }
                transaction.commit();
                context.renderJSON(StatusCodes.SUCC);

                try {
                    final JSONObject user = userQueryService.getUser(userId);
                    user.put(UserExt.USER_LATEST_CMT_TIME, System.currentTimeMillis());
                    userMgmtService.updateUser(userId, user);
                } catch (final Exception e) {
                    LOGGER.log(Level.ERROR, "Update user latest comment time failed", e);
                }
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

            msg = msg.put(Common.CONTENT, processMarkdown(msg.optString(Common.CONTENT)));
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
                    .setPage(page, 10)
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
        final BeanManager beanManager = BeanManager.getInstance();
        final ShortLinkQueryService shortLinkQueryService = beanManager.getReference(ShortLinkQueryService.class);
        content = shortLinkQueryService.linkArticle(content);
        content = Emotions.convert(content);
        content = Markdowns.toHTML(content);
        content = Markdowns.clean(content, "");

        return content;
    }
}
