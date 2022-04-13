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
import org.b3log.symphony.processor.bot.ChatRoomBot;
import org.b3log.symphony.processor.channel.ChatroomChannel;
import org.b3log.symphony.processor.middleware.AnonymousViewCheckMidware;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.processor.middleware.validate.ChatMsgAddValidationMidware;
import org.b3log.symphony.repository.ChatRoomRepository;
import org.b3log.symphony.repository.UserRepository;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;


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

    @Inject
    private UserRepository userRepository;

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
            // ==? 是否禁言中 ?==
            int muted = ChatRoomBot.muted(userId);
            int muteDay = muted / (24 * 60 * 60);
            int muteHour = muted % (24 * 60 * 60) / (60 * 60);
            int muteMinute = muted % (24 * 60 * 60) % (60 * 60) / 60;
            int muteSecond = muted % (24 * 60 * 60) % (60 * 60) % 60;
            if (muted != -1) {
                context.renderJSON(StatusCodes.ERR).renderMsg("抢红包失败，原因：正在禁言中，剩余时间 " + muteDay + " 天 " + muteHour + " 小时 " + muteMinute + " 分 " + muteSecond + " 秒。");
                return;
            }
            // ==! 是否禁言中 !==
            // ==? 风控判断 ?==
            int risksControlled = ChatRoomBot.risksControlled(userId);
            int risksControlDay = risksControlled / (24 * 60 * 60);
            int risksControlHour = risksControlled % (24 * 60 * 60) / (60 * 60);
            int risksControlMinute = risksControlled % (24 * 60 * 60) % (60 * 60) / 60;
            int risksControlSecond = risksControlled % (24 * 60 * 60) % (60 * 60) % 60;
            if (risksControlled != -1) {
                if (!openRedPacketLimiter.access(userId)) {
                    context.renderJSON(StatusCodes.ERR).renderMsg("抢红包失败，原因：你处于风控名单，每30分钟只能抢一个红包。剩余风控时间为：" + risksControlDay + " 天 " + risksControlHour + " 小时 " + risksControlMinute + " 分 " + risksControlSecond + " 秒。");
                    return;
                }
            }
            // ==! 风控判断 !==
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
            if (redPacket.has("gesture") && StringUtils.isNotBlank(redPacket.optString("gesture"))) {
                info.put("gesture", redPacket.optInt("gesture"));
            }

            String msgType = redPacket.optString("msgType");
            if (!msgType.equals("redPacket") || !redPacket.has("type")) {
                context.renderJSON(StatusCodes.ERR).renderMsg("红包非法");
                return;
            }
            // 红包正常，可以抢了
            int money = redPacket.optInt("money");
            int countMoney = money;
            int count = redPacket.optInt("count");
            int got = redPacket.optInt("got");
            JSONArray who = redPacket.optJSONArray("who");
            JSONObject dice = requestJSONObject.optJSONObject("dice");
            // 根据抢的人数判断是否已经抢光了
            if (got >= count) {
                JSONObject ret = new JSONObject().put("who", who).put("info", info).put("recivers", recivers);
                if ("dice".equals(redPacket.getString("type"))) {
                    ret.put("diceRet", redPacket.optJSONObject("diceRet"));
                    context.renderJSON(ret);
                    return;
                }
                context.renderJSON(ret);
                return;
            }

            if ("dice".equals(redPacket.getString("type"))) {
                context.renderJSON(StatusCodes.ERR).renderMsg("暂不支持摇骰子红包！");
                return;

                /*if (redPacketIsOpened(who, userId) || userId.equals(redPacket.optString("senderId"))) {
                    context.renderJSON(new JSONObject().put("who", who).put("info", info));
                    return;
                }

                if (Objects.isNull(dice) || dice.isEmpty()) {
                    context.renderJSON(StatusCodes.ERR).renderMsg("投注失败，参数非法！");
                    return;
                }
                String bet = dice.optString("bet");
                String chips = dice.optString("chips");
                if (org.apache.commons.lang3.StringUtils.isBlank(bet) || (!org.apache.commons.lang3.StringUtils.equals("big", bet)
                        && !org.apache.commons.lang3.StringUtils.equals("small", bet)
                        && !org.apache.commons.lang3.StringUtils.equals("leopard", bet))) {
                    context.renderJSON(StatusCodes.ERR).renderMsg("投注失败，参数非法！");
                    return;
                }
                if (org.apache.commons.lang3.StringUtils.isBlank(bet) || !org.apache.commons.lang3.StringUtils.isNumeric(chips)) {
                    int chipsI = Integer.parseInt(chips);
                    if (chipsI < 32 || chipsI > 100) {
                        context.renderJSON(StatusCodes.ERR).renderMsg("投注失败，参数非法！");
                        return;
                    }
                }*/
            }

            // 开始领取红包
            int meGot = 0;
            if ("average".equals(redPacket.getString("type"))) {
                // 普通红包逻辑
                if (redPacketIsOpened(who, userId)) {
                    context.renderJSON(new JSONObject().put("who", who).put("info", info));
                    return;
                }
                meGot = money;
            } else if ("dice".equals(redPacket.getString("type"))) {
                boolean closed = false;
                //记录投注人信息
                JSONObject source = new JSONObject(chatRoomService.getChatMsg(oId).optString("content"));
                JSONObject source2 = new JSONObject(source.optString("content"));
                source2.put("got", got + 1);
                JSONArray source3 = source2.optJSONArray("who");
                source3.put(new JSONObject().put("dice", dice).put("userId", userId).put("userName", userName).put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())).put("avatar", userQueryService.getUser(userId).optString(UserExt.USER_AVATAR_URL)));
                source2.put("who", source3);
                //需要封盘
                if (got == count - 1) {
                    Dice calcRet = allocateDice();
                    String s = com.alibaba.fastjson.JSONObject.toJSONString(calcRet);
                    source2.put("diceRet", new JSONObject(s));
                    closed = true;
                }
                source.put("content", source2);

                final Transaction transaction = chatRoomRepository.beginTransaction();
                chatRoomRepository.update(oId, new JSONObject().put("content", source.toString()));
                transaction.commit();

                if (closed) {
                    //结算
                    allocateMoney(source2);
                }

                // 广播红包情况
                JSONObject redPacketStatus = new JSONObject();
                redPacketStatus.put(Common.TYPE, "redPacketStatus");
                redPacketStatus.put("whoGive", source.optString(User.USER_NAME));
                redPacketStatus.put("whoGot", userName);
                redPacketStatus.put("got", got + 1);
                redPacketStatus.put("count", count);
                redPacketStatus.put("oId", oId);
                redPacketStatus.put("dice", dice);
                ChatroomChannel.notifyChat(redPacketStatus);
                info.put("got", redPacket.optInt("got") + 1);
                context.renderJSON(new JSONObject().put("who", source3).put("diceRet", source2.optJSONObject("diceRet")).put("info", info).put("recivers", recivers).put("dice", true));
                return;
            } else if ("specify".equals(redPacket.getString("type"))) {
                //专属红包逻辑
                final boolean isReciver = recivers.toList().stream().anyMatch(x -> {
                    final String reciver = (String) x;
                    return reciver.equals(userName);
                });
                if (!isReciver) {
                    context.renderJSON(new JSONObject().put("who", who).put("info", info).put("recivers", recivers));
                    return;
                }
                if (redPacketIsOpened(who, userId)) {
                    context.renderJSON(new JSONObject().put("who", who).put("info", info).put("recivers", recivers));
                    return;
                }
                if (redPacketIsOpened(who, userId)) {
                    context.renderJSON(new JSONObject().put("who", who).put("info", info).put("recivers", recivers));
                    return;
                }
                meGot = money;
            } else if ("random".equals(redPacket.getString("type"))) {
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
            } else if ("rockPaperScissors".equals(redPacket.getString("type"))) {
                if (sender.optString("oId").equals(userId)) {
                    context.renderJSON(new JSONObject().put("who", who).put("info", info));
                    return;
                }
                if (redPacketIsOpened(who, userId)) {
                    context.renderJSON(new JSONObject().put("who", who).put("info", info));
                    return;
                }
                int gesture = requestJSONObject.optInt("gesture", -1);
                if (gesture < 0) {
                    context.renderJSON(StatusCodes.ERR).renderMsg("红包失效");
                    return;
                }
                int senderGesture = redPacket.optInt("gesture");
                if (senderGesture - gesture == 1 || senderGesture - gesture == -2) {
                    meGot = money;
                } else if (senderGesture != gesture) {
                    meGot = -money;
                }
            } else {
                if (redPacketIsOpened(who, userId)) {
                    context.renderJSON(new JSONObject().put("who", who).put("info", info));
                    return;
                }
                if (!RED_PACKET_BUCKET.containsKey(oId)) {
                    context.renderJSON(StatusCodes.ERR).renderMsg("红包失效");
                    return;
                }
                meGot = RED_PACKET_BUCKET.get(oId).packs.poll();
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
            if ("rockPaperScissors".equals(redPacket.getString("type")) && meGot <= 0) {
                pointtransferMgmtService.transfer(Pointtransfer.ID_C_SYS, redPacket.optString("senderId"),
                        Pointtransfer.TRANSFER_TYPE_C_ACTIVITY_RECEIVE_RED_PACKET,
                        meGot < 0 ? money * 2 : money, "", System.currentTimeMillis(), "");
            }
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
            } else if ("specify".equals(redPacket.getString("type"))) {
                // 通知标为已读
                notificationMgmtService.makeRead(currentUser.optString(Keys.OBJECT_ID), Notification.DATA_TYPE_C_RED_PACKET);
            } else if ("random".equals(redPacket.getString("type")) && redPacketStatus.optInt("got") == redPacketStatus.optInt("count")) {
                RED_PACKET_BUCKET.remove(oId);
            }

            if (count == got + 1) {
                ChatroomChannel.notifyChat(redPacketStatus);
            }
        } catch (Exception e) {
            context.renderJSON(StatusCodes.ERR).renderMsg("红包非法");
            LOGGER.log(Level.ERROR, "Open Red Packet failed on ChatRoomProcessor.");
        }
    }

    private void allocateMoney(final JSONObject jsonObject) {
        final JSONObject diceRet = jsonObject.optJSONObject("diceRet");
        final String bookmaker = jsonObject.optString("senderId");
        final String winnerResult = diceRet.optString("winnerResult");
        final JSONArray who = jsonObject.optJSONArray("who");
        for (Object o : who) {
            final JSONObject userInfo = (JSONObject) o;
            final JSONObject diceInfo = userInfo.optJSONObject("dice");
            final String userId = userInfo.optString("userId");
            int point = Integer.parseInt(diceInfo.optString("chips"));
            final String bet = diceInfo.optString("bet");
            //转账
            if (org.apache.commons.lang3.StringUtils.equals(winnerResult, bet)) {
                if ("leopard".equals(winnerResult)) {
                    point *= 6;
                }
                pointtransferMgmtService.transfer(bookmaker, userId,
                        Pointtransfer.TRANSFER_TYPE_C_ACTIVITY_SEND_RED_PACKET,
                        point, "", System.currentTimeMillis(), "");
            } else {
                //通杀
                pointtransferMgmtService.transfer(userId, bookmaker,
                        Pointtransfer.TRANSFER_TYPE_C_ACTIVITY_RECEIVE_RED_PACKET,
                        point, "", System.currentTimeMillis(), "");
            }
        }

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
    final private static SimpleCurrentLimiter risksControlMessageLimiter = new SimpleCurrentLimiter(15 * 60, 1);
    final private static SimpleCurrentLimiter openRedPacketLimiter = new SimpleCurrentLimiter(30 * 60, 1);

    public synchronized void addChatRoomMsg(final RequestContext context) {
        if (ChatRoomBot.record(context)) {
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

            String userId = currentUser.optString(Keys.OBJECT_ID);

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
                    int gesture = redpacket.optInt("gesture", -1);
                    if (!userName.equals("admin")) {
                        message = message.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5,，.。！!?？《》\\s]", "");
                        if (message.length() > 20) {
                            message = message.substring(0, 20);
                        }
                        // 扣积分
                        if (money > calcRedpacketMax()) {
                            context.renderJSON(StatusCodes.ERR).renderMsg("红包发送失败！根据社区成员积分储蓄平均数，当前红包最大限额为" + calcRedpacketMax() + "！");
                            return;
                        }
                        if (money < 32) {
                            context.renderJSON(StatusCodes.ERR).renderMsg("红包最小金额为32！");
                            return;
                        }
                        if (count < 1) {
                            context.renderJSON(StatusCodes.ERR).renderMsg("红包最少发1份！");
                            return;
                        }
                        if (count > 100) {
                            context.renderJSON(StatusCodes.ERR).renderMsg("红包最多发100份！");
                            return;
                        }
                        if (count > money) {
                            context.renderJSON(StatusCodes.ERR).renderMsg("红包个数不能大于总金额！");
                            return;
                        }
                    }
                    try {
                        int toatlMoney = 0;
                        switch (type) {
                            case "dice":
                                context.renderJSON(StatusCodes.ERR).renderMsg("暂不支持摇骰子红包！");
                                return;
                                /*toatlMoney = 250;
                                int userPoint = currentUser.optInt("userPoint");
                                if (userPoint < 1500 * 6) {
                                    context.renderJSON(StatusCodes.ERR).renderMsg("少年,资产不足以开盘!");
                                    return;
                                }
                                if (count > 15) {
                                    context.renderJSON(StatusCodes.ERR).renderMsg("开盘人数不成超过15人,小赌怡情！");
                                    return;
                                }
                                break;*/
                            case "rockPaperScissors":
                                if (gesture < 0 || gesture > 2) {
                                    context.renderJSON(StatusCodes.ERR).renderMsg("数据不合法！");
                                    return;
                                }
                                count = 1;
                                toatlMoney = money;
                                break;
                            case "average":
                                toatlMoney = money * count;
                                break;
                            case "specify":
                                if (StringUtils.isNotBlank(recivers)) {
                                    final JSONArray reciverArray = new JSONArray(recivers);
                                    final int length = reciverArray.length();
                                    if (length == 1 && userName.equals(reciverArray.optString(0))) {
                                        context.renderJSON(StatusCodes.ERR).renderMsg("不允许自己给自己单独发专属红包! ");
                                        return;
                                    }
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
                            case "heartbeat":
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
                    if (gesture >= 0 && gesture <= 2) {
                        redPacketJSON.put("gesture", gesture);
                    }
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
                    switch (type) {
                        case "rockPaperScissors":
                            redPacketJSON.remove("gesture");
                            msg.put(Common.CONTENT, redPacketJSON.toString());
                            break;
                        case "heartbeat":
                            // 预分配红包
                            RED_PACKET_BUCKET.put(msg.optString("oId"), allocateHeartbeatRedPacket(msg.optString("oId"), userId, money, count, 3));
                            break;
                        case "random":
                            // 预分配红包
                            RED_PACKET_BUCKET.put(msg.optString("oId"), allocateRedPacket(msg.optString("oId"), userId, money, count, 2));
                            break;
                        case "specify":
                            // 发通知
                            final JSONArray jsonArray = new JSONArray(recivers);
                            for (Object o : jsonArray) {
                                final String reciver = (String) o;
                                final JSONObject user = userQueryService.getUserByName(reciver);
                                if (Objects.isNull(user) || user.optString("oId").equals(currentUser.optString("oId"))) {
                                    continue;
                                }
                                final JSONObject notification = new JSONObject();
                                notification.put(Notification.NOTIFICATION_USER_ID, user.optString("oId"));
                                notification.put(Notification.NOTIFICATION_DATA_ID, msg.optString("oId"));
                                notificationMgmtService.addRedPacketNotification(notification);
                            }
                            break;
                        default:
                            // ignore
                    }
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
            } else if (content.startsWith("[setdiscuss]") && content.endsWith("[/setdiscuss]")) {
                // 扣钱
                final boolean succ = null != pointtransferMgmtService.transfer(userId, Pointtransfer.ID_C_SYS,
                        Pointtransfer.TRANSFER_TYPE_C_ACTIVITY_SET_DISCUSS,
                        16, "", System.currentTimeMillis(), "");
                if (!succ) {
                    context.renderJSON(StatusCodes.ERR).renderMsg("少年，你的积分不足！");
                    return;
                }
                String setdiscussString = content.replaceAll("^\\[setdiscuss\\]", "").replaceAll("\\[/setdiscuss\\]$", "");
                setdiscussString = setdiscussString.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5,，.。！!?？《》]", "");
                if (setdiscussString.length() > 16) {
                    setdiscussString = setdiscussString.substring(0, 16);
                }
                if (setdiscussString.isEmpty()) {
                    context.renderJSON(StatusCodes.ERR).renderMsg("这样不好玩哦～");
                    return;
                }
                ChatroomChannel.discussing = setdiscussString;
                // 广播话题情况
                JSONObject discussStatus = new JSONObject();
                discussStatus.put(Common.TYPE, "discussChanged");
                discussStatus.put("whoChanged", userName);
                discussStatus.put("newDiscuss", setdiscussString);
                ChatroomChannel.notifyChat(discussStatus);

                context.renderJSON(StatusCodes.SUCC);
            } else {
                // 加活跃
                int risksControlled = ChatRoomBot.risksControlled(userId);
                if (risksControlled != -1) {
                    if (risksControlMessageLimiter.access(userId)) {
                        try {
                            if (chatRoomLivenessLimiter.access(userId)) {
                                livenessMgmtService.incLiveness(userId, Liveness.LIVENESS_COMMENT);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                } else {
                    try {
                        if (chatRoomLivenessLimiter.access(userId)) {
                            livenessMgmtService.incLiveness(userId, Liveness.LIVENESS_COMMENT);
                        }
                    } catch (Exception ignored) {
                    }
                }

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
                    final JSONObject user = userQueryService.getUser(userId);
                    user.put(UserExt.USER_LATEST_CMT_TIME, System.currentTimeMillis());
                    userMgmtService.updateUser(userId, user);
                } catch (final Exception e) {
                    LOGGER.log(Level.ERROR, "Update user latest comment time failed", e);
                }
            }
        }
    }

    /**
     * 计算红包限额
     *
     * @return int
     */
    private static long CRM_CACHE_TIME = System.currentTimeMillis();
    private static int CRM_CACHE_POINT = 500;

    public static int calcRedpacketMax() {
        final BeanManager beanManager = BeanManager.getInstance();
        final UserRepository userRepository = beanManager.getReference(UserRepository.class);

        if (((System.currentTimeMillis() - CRM_CACHE_TIME) / 1000) > 5 * 60) {
            // 刷新缓存
            try {
                // 计算红包限额
                List<JSONObject> userPointAvg = userRepository.select("select round(avg(userPoint),0) from symphony_user where userRole!='adminRole' and userPoint!='0';");
                int userPoint = userPointAvg.get(0).optInt("round(avg(userPoint),0)");

                if (userPoint > 5000) {
                    userPoint = 5000;
                }
                if (userPoint < 500) {
                    userPoint = 500;
                }
                CRM_CACHE_POINT = userPoint;
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, "Calc red packet failed", e);
                CRM_CACHE_POINT = 501;
            }
            CRM_CACHE_TIME = System.currentTimeMillis();
        }
        return CRM_CACHE_POINT;
    }

    public List<JSONObject> atUsers(String content, String currentUser) {
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

    public static String processMarkdown(String content) {
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

    private boolean redPacketIsOpened(JSONArray who, String userId) {
        for (Object o : who) {
            JSONObject currentWho = (JSONObject) o;
            String uId = currentWho.optString("userId");
            if (uId.equals(userId)) {
                return true;
            }
        }
        return false;
    }

    private static Dice allocateDice() {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final int[] randoms = new int[3];
        final HashSet<Integer> hash = new HashSet<>(4);
        for (int i = 0; i < 3; i++) {
            int rand = random.nextInt(1, 7);
            randoms[i] = rand;
            hash.add(rand);
        }
        Dice dice = new Dice(Boolean.TRUE, null, randoms);
        if (hash.size() == 1) {
            dice.winnerResult = "leopard";
            return dice;
        }
        int sum = IntStream.of(randoms[0], randoms[1], randoms[2]).sum();
        if (sum <= 10) {
            dice.winnerResult = "small";
        } else {
            dice.winnerResult = "big";
        }
        return dice;
    }

    private static RedPacket allocateHeartbeatRedPacket(String id, String sendId, int money, int count, int zeroCount) {
        if (zeroCount >= count) {
            zeroCount = 1;
        }
        int extraMoney = (money / count) + 10;
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
        redPacket.packs.push(money + extraMoney);
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        int remain = extraMoney;
        int cnt = --count;
        for (int i = 0; i < cnt; i++) {
            if (remain == 0) {
                redPacket.packs.push(-remain);
            } else {
                if (count == 1) {
                    redPacket.packs.push(-remain);
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
                redPacket.packs.push(get == 0 ? 0 : -get);
                count--;
                remain -= get;
            }
        }
        Collections.shuffle(redPacket.packs);
        return redPacket;
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
        int cnt = count;
        for (int i = 0; i < cnt; i++) {
            if (remain == 0) {
                redPacket.packs.push(remain);
            } else {
                if (count == 1) {
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
                count--;
                remain -= get;
            }
        }
        Collections.shuffle(redPacket.packs);
        return redPacket;
    }

    public static class Dice {
        public boolean closed;
        public int[] diceParticles;
        public String winnerResult;

        public Dice() {
        }

        ;

        public Dice(boolean closed, String winnerResult, int... diceParticles) {
            this.closed = closed;
            if (diceParticles.length != 3) {
                throw new IllegalArgumentException("骰子个数非法");
            }
            this.diceParticles = diceParticles;
            this.winnerResult = winnerResult;
        }
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
