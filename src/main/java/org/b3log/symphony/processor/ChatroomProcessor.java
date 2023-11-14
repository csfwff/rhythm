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
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import pers.adlered.simplecurrentlimiter.main.SimpleCurrentLimiter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


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

    @Inject
    private AvatarQueryService avatarQueryService;

    public static int barragerCost = 5;

    public static String barragerUnit = "积分";

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
        Dispatcher.get("/chat-room/getMessage", chatroomProcessor::getContextMessage);
        Dispatcher.get("/chat-room/online-users", chatroomProcessor::getChatRoomUsers);
        Dispatcher.get("/cr/raw/{id}", chatroomProcessor::getChatRaw, anonymousViewCheckMidware::handle);
        Dispatcher.delete("/chat-room/revoke/{oId}", chatroomProcessor::revokeMessage, loginCheck::handle);
        Dispatcher.post("/chat-room/red-packet/open", chatroomProcessor::openRedPacket, loginCheck::handle);
        Dispatcher.get("/chat-room/si-guo-list", chatroomProcessor::getSiGuoList);

    }

    /**
     * 获得思过崖
     * @param context
     */
    public void getSiGuoList(final RequestContext context) {
        JSONArray list = ChatRoomBot.getSiGuoList();
        for (Object i : list) {
            JSONObject j = (JSONObject) i;
            JSONObject user = userQueryService.getUserByName(j.optString(User.USER_NAME));
            j.put(UserExt.USER_AVATAR_URL, user.optString(UserExt.USER_AVATAR_URL));
            j.put(UserExt.USER_NICKNAME, user.optString(UserExt.USER_NICKNAME));
        }
        JSONObject ret = new JSONObject();
        ret.put(Keys.CODE, StatusCodes.SUCC);
        ret.put(Keys.MSG, "");
        ret.put(Keys.DATA, list);
        context.renderJSON(ret);
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
            // 是否全员禁言中
            boolean isAll = muted < 0 && muted != -1;
            if (isAll){
                if (DataModelService.hasPermission(currentUser.optString(User.USER_ROLE), 3)){
                    // OP 豁免全员禁言
                    muted = -1;
                }else {
                    // 回正
                    muted *= -1;
                }
            }
            int muteDay = muted / (24 * 60 * 60);
            int muteHour = muted % (24 * 60 * 60) / (60 * 60);
            int muteMinute = muted % (24 * 60 * 60) % (60 * 60) / 60;
            int muteSecond = muted % (24 * 60 * 60) % (60 * 60) % 60;
            if (muted != -1) {
                if (isAll){
                    context.renderJSON(StatusCodes.ERR).renderMsg("抢红包失败，原因：正在全员禁言中，剩余时间 " + muteDay + " 天 " + muteHour + " 小时 " + muteMinute + " 分 " + muteSecond + " 秒。");
                }else {
                    context.renderJSON(StatusCodes.ERR).renderMsg("抢红包失败，原因：正在禁言中，剩余时间 " + muteDay + " 天 " + muteHour + " 小时 " + muteMinute + " 分 " + muteSecond + " 秒。");
                }
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
            avatarQueryService.fillUserAvatarURL(info);
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

            if ("heartbeat".equals(redPacket.getString("type")) || "rockPaperScissors".equals(redPacket.getString("type"))) {
                // 如果要抢石头剪刀布红包，先看账户余额是否大于平均数
                if (currentUser.optInt(UserExt.USER_POINT) < redPacket.optInt("money")) {
                    context.renderJSON(StatusCodes.ERR).renderMsg("抢红包失败！你的账户余额低于该红包金额。");
                    return;
                }
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
                    String s = new JSONObject(calcRet).toString();
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
                if (redPacketIsOpened(who, userId)) {
                    context.renderJSON(new JSONObject().put("who", who).put("info", info));
                    return;
                }

                final RockPaperScissorRedPacket packet = (RockPaperScissorRedPacket) RED_PACKET_BUCKET.get(oId);
                final int senderGesture = packet.getGesture();

                if (sender.optString("oId").equals(userId)) {
                    info.put("gesture", senderGesture);
                    context.renderJSON(new JSONObject().put("who", who).put("info", info));
                    return;
                }

                int gesture = requestJSONObject.optInt("gesture", -1);
                if (gesture < 0) {
                    context.renderJSON(StatusCodes.ERR).renderMsg("红包失效");
                    return;
                }

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
            if ("rockPaperScissors".equalsIgnoreCase(redPacket.getString("type"))) {
                final RockPaperScissorRedPacket packet = (RockPaperScissorRedPacket) RED_PACKET_BUCKET.get(oId);
                source2.put("gesture", packet.getGesture());
            }
            JSONArray source3 = source2.optJSONArray("who");
            source3.put(new JSONObject().put("userMoney", meGot).put("userId", userId).put("userName", userName).put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())).put("avatar", userQueryService.getUser(userId).optString(UserExt.USER_AVATAR_URL)));
            source2.put("who", source3);
            source.put("content", source2.toString());
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
            } else if ("heartbeat".equals(redPacket.getString("type")) && redPacketStatus.optInt("got") == redPacketStatus.optInt("count")) {
                RED_PACKET_BUCKET.remove(oId);
            } else if ("rockPaperScissors".equalsIgnoreCase(redPacket.getString("type"))) {
                RED_PACKET_BUCKET.remove(oId);
            }
            ChatroomChannel.notifyChat(redPacketStatus);
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
    final private static SimpleCurrentLimiter chatRoomLivenessLimiter = new SimpleCurrentLimiter(60, 1);
    final private static SimpleCurrentLimiter risksControlMessageLimiter = new SimpleCurrentLimiter(15 * 60, 1);
    final private static SimpleCurrentLimiter openRedPacketLimiter = new SimpleCurrentLimiter(30 * 60, 1);
    /**
     * Send chatroom message.
     *
     * @param context Everything
     */
    public void addChatRoomMsg(final RequestContext context) {
        if (ChatRoomBot.record(context)) {
            final JSONObject requestJSONObject = (JSONObject) context.attr(Keys.REQUEST);
            String content = requestJSONObject.optString(Common.CONTENT);
            String clientMark = requestJSONObject.optString("client");
            String source = "";
            try {
                String client = clientMark.split("/")[0];
                String version = clientMark.split("/")[1].replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5.\\s\\-]", "");
                if (version.length() > 32) {
                    version = version.substring(0, 31);
                }
                List<String> legalClient = new ArrayList<>();
                legalClient.add("Web");
                legalClient.add("PC");
                legalClient.add("Mobile");
                legalClient.add("Windows");
                legalClient.add("macOS");
                legalClient.add("Linux");
                legalClient.add("IceNet");
                legalClient.add("ElvesOnline");
                legalClient.add("iOS");
                legalClient.add("Android");
                legalClient.add("Extension");
                legalClient.add("IDEA");
                legalClient.add("Chrome");
                legalClient.add("Edge");
                legalClient.add("VSCode");
                legalClient.add("Python");
                legalClient.add("Golang");
                legalClient.add("Bird");
                legalClient.add("Dart");
                legalClient.add("Other");
                if (legalClient.contains(client)) {
                    source = client + "/" + version;
                }
            } catch (Exception ignored) {
            }

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
            final String userPhone = currentUser.optString("userPhone");
            if (userPhone.isEmpty()) {
                context.renderJSON(StatusCodes.ERR).renderMsg("未绑定手机号码，无法使用此功能。请至设置-账户中绑定手机号码。");
                return;
            }
            final String userName = currentUser.optString(User.USER_NAME);
            // 保存 Active 信息
            chatroomChannel.userActive.put(userName, System.currentTimeMillis());

            final long time = System.currentTimeMillis();
            JSONObject msg = new JSONObject();
            msg.put(User.USER_NAME, userName);
            msg.put(UserExt.USER_AVATAR_URL, currentUser.optString(UserExt.USER_AVATAR_URL));
            msg.put(Common.CONTENT, content);
            msg.put(Common.TIME, time);
            msg.put(UserExt.USER_NICKNAME, currentUser.optString(UserExt.USER_NICKNAME));
            msg.put("sysMetal", cloudService.getEnabledMetal(currentUser.optString(Keys.OBJECT_ID)));
            msg.put("userOId", currentUser.optLong(Keys.OBJECT_ID));
            if (!source.isEmpty()) {
                msg.put("client", source);
            }

            String userId = currentUser.optString(Keys.OBJECT_ID);

            if (content.startsWith("[redpacket]") && content.endsWith("[/redpacket]")) {
                // 是否收税
                Boolean collectTaxes = false;
                // 税率
                BigDecimal taxRate = new BigDecimal("0.05");
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
                        if (type.equals("heartbeat")) {
                            if (count < 5) {
                                context.renderJSON(StatusCodes.ERR).renderMsg("心跳红包个数至少为5个！");
                                return;
                            }
                        }
                        // 扣积分
                        if (money > 10240) {
                            context.renderJSON(StatusCodes.ERR).renderMsg("红包发送失败！红包最大限额为" + 10240 + "！");
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
                                // 征税
                                collectTaxes = true;
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
                                toatlMoney, "", System.currentTimeMillis(), collectTaxes ? "collectTaxes" : "");
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
                    // 收税的时候 直接扣减
                    redPacketJSON.put("money", collectTaxes ? BigDecimal.valueOf(money).multiply(BigDecimal.ONE.subtract(taxRate)).intValue() : money);
                    redPacketJSON.put("count", count);
                    redPacketJSON.put("msg", message);
                    redPacketJSON.put("recivers", recivers);
                    // 已经抢了这个红包的人数
                    redPacketJSON.put("got", 0);
                    // 已经抢了这个红包的人以及抢到的金额
                    redPacketJSON.put("who", new JSONArray());
                    // 红包特殊标识，堵漏洞
                    redPacketJSON.put("msgType", "redPacket");

                    // 税给admin
                    int tax = money - (BigDecimal.valueOf(money).multiply(BigDecimal.ONE.subtract(taxRate)).intValue());
                    pointtransferMgmtService.transfer(Pointtransfer.ID_C_SYS, userQueryService.getUserByName("admin").optString(Keys.OBJECT_ID),
                            Pointtransfer.TRANSFER_TYPE_C_ACCOUNT2ACCOUNT, tax, userId, System.currentTimeMillis(), "猜拳红包税收，纳税人：" + userName);

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
                            RED_PACKET_BUCKET.put(msg.optString("oId"), allocateRockRedPacket(msg.optString("oId"), gesture));
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
            } else if (content.startsWith("[barrager]") && content.endsWith("[/barrager]")) {
                try {
                    // 扣钱
                    final boolean succ = null != pointtransferMgmtService.transfer(userId, Pointtransfer.ID_C_SYS,
                            Pointtransfer.TRANSFER_TYPE_C_CHAT_ROOM_SEND_BARRAGER,
                            barragerCost, "", System.currentTimeMillis(), "");
                    if (!succ) {
                        context.renderJSON(StatusCodes.ERR).renderMsg("少年，你的积分不足！");
                        return;
                    }
                    String barragerString = content.replaceAll("^\\[barrager\\]", "").replaceAll("\\[/barrager\\]$", "");
                    JSONObject barrager = new JSONObject(barragerString);
                    String barragerContent = barrager.optString("content");
                    barragerContent = Jsoup.clean(barragerContent, Whitelist.none());
                    barragerContent = StringUtils.trim(barragerContent);
                    String barragerColor = barrager.optString("color");
                    if (barragerContent.length() > 32) {
                        barragerContent = barragerContent.substring(0, 32);
                    }
                    if (barragerContent.isEmpty()) {
                        context.renderJSON(StatusCodes.ERR).renderMsg("这样不好玩哦～");
                        return;
                    }
                    JSONObject barragerJSON = new JSONObject();
                    barragerJSON.put(Common.TYPE, "barrager");
                    barragerJSON.put("barragerContent", barragerContent);
                    barragerJSON.put("barragerColor", barragerColor);
                    barragerJSON.put(User.USER_NAME, userName);
                    barragerJSON.put(UserExt.USER_AVATAR_URL, currentUser.optString(UserExt.USER_AVATAR_URL));
                    barragerJSON.put(UserExt.USER_NICKNAME, currentUser.optString(UserExt.USER_NICKNAME));
                    ChatroomChannel.notifyChat(barragerJSON);
                    // 加活跃
                    incLiveness(userId);

                    LogsService.simpleLog(context, "发送弹幕", "用户: " + userName + " 颜色: " + barragerColor + " 内容: " + barragerContent);
                    context.renderJSON(StatusCodes.SUCC);
                } catch (Exception e) {
                    LOGGER.log(Level.INFO, "User " + userName + " failed to send a barrager.");
                }
            } else {
                // 加活跃
                incLiveness(userId);

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

    private void incLiveness(String userId) {
        int start = 1930;
        int end = 800;
        int now = Integer.parseInt(new SimpleDateFormat("HHmm").format(new Date()));
        if (now > end && now < start) {
            int risksControlled = ChatRoomBot.risksControlled(userId);
            if (risksControlled != -1) {
                if (risksControlMessageLimiter.access(userId)) {
                    try {
                        if (chatRoomLivenessLimiter.access(userId)) {
                            livenessMgmtService.incLiveness(userId, Liveness.LIVENESS_ACTIVITY);
                        }
                    } catch (Exception ignored) {
                    }
                }
            } else {
                try {
                    if (chatRoomLivenessLimiter.access(userId)) {
                        livenessMgmtService.incLiveness(userId, Liveness.LIVENESS_ACTIVITY);
                    }
                } catch (Exception ignored) {
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
    public synchronized void showChatRoom(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "chat-room.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        try {
            String oId = context.param("oId");
            if (oId == null) {
                throw new NullPointerException();
            }
            dataModel.put("contextMode", "yes");
            dataModel.put("contextOId", oId);
        } catch (Exception ignored) {
            dataModel.put("contextMode", "no");
            dataModel.put("contextOId", '0');
        }
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
        // 是否宵禁
        int start = 1930;
        int end = 800;
        int now = Integer.parseInt(new SimpleDateFormat("HHmm").format(new Date()));
        if (now > end && now < start) {
            dataModel.put("nightDisableMode", false);
        } else {
            dataModel.put("nightDisableMode", true);
        }
        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
        dataModel.put(Common.SELECTED, "cr");
        dataModel.put("barragerCost", barragerCost);
        dataModel.put("barragerUnit", barragerUnit);
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
            try {
                JSONObject jsonObject = new JSONObject(content);
                String msgType = jsonObject.optString("msgType");
                if (!msgType.isEmpty()) {
                    dataModel.put("raw", "{\"msg\":\"想看红包金额，想得美 :)\",\"recivers\":\"[]\",\"senderId\":\"1380013800000\",\"msgType\":\"redPacket\",\"money\":14250,\"count\":250,\"type\":\"random\",\"got\":0,\"who\":[]}");
                    return;
                }
            } catch (Exception ignored) {
            }
            dataModel.put("raw", content);
        } catch (RepositoryException e) {
            context.renderCodeMsg(StatusCodes.ERR, "Invalid chat id.");
        }
    }

    /**
     * 获取某个oId的聊天消息上下文
     * @param context
     */
    public void getContextMessage(final RequestContext context) {
        try {
            String oId = context.param("oId");
            int size = Integer.parseInt(context.param("size"));
            int mode = Integer.parseInt(context.param("mode"));
            String type = context.param("type");
            JSONObject currentUser = Sessions.getUser();
            try {
                currentUser = ApiProcessor.getUserByKey(context.param("apiKey"));
            } catch (NullPointerException ignored) {
            }
            if (null == currentUser) {
                context.sendError(401);
                context.abort();
                return;
            }
            JSONObject ret = new JSONObject();
            ret.put(Keys.CODE, StatusCodes.SUCC);
            ret.put(Keys.MSG, "");
            ret.put(Keys.DATA, getContext(oId, size, mode, type));
            context.renderJSON(ret);
        } catch (Exception e) {
            context.sendStatus(500);
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
            String type = context.param("type");
            List<JSONObject> jsonObject = getMessages(page,type);
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
    private static Map<String/*userName*/, String/*data-times*/> revoke = new HashMap<>();

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
            LogsService.chatroomLog(context, removeMessageId, curUser);

            if (isAdmin) {
                final Transaction transaction = chatRoomRepository.beginTransaction();
                chatRoomRepository.remove(removeMessageId);
                transaction.commit();
                context.renderJSON(StatusCodes.SUCC).renderMsg("撤回成功。");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Common.TYPE, "revoke");
                jsonObject.put("oId", removeMessageId);
                ChatroomChannel.notifyChat(jsonObject);
            } else if (msgUser.equals(curUser)) {
                final String date = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd");
                // 撤回记录
                String dateTimes = revoke.getOrDefault(curUser, date + ",0");
                // 分割
                String[] date_times = dateTimes.split(",");
                // 上次撤回日期
                String nDate = date_times[0];
                // 次数
                Integer times;
                // 兼容旧数据
                if (date_times.length > 1) {
                    // 撤回次数
                    times = Integer.valueOf(date_times[1]);
                } else {
                    // 今天撤回过了, 免费了一次? 要不要追扣一把
                    times = 1;
                }

                // 当前日期不是今天 还没撤回过, times 归 0
                if (!nDate.equals(date)) {
                    times = 0;
                }
                // 需要扣减的积分 首次免费 以后 f(x) = 32x
                Integer needDelPoint = times * 32;
                // 扣钱
                final boolean succ = null != pointtransferMgmtService.transfer(currentUser.optString(Keys.OBJECT_ID), Pointtransfer.ID_C_SYS,
                        Pointtransfer.TRANSFER_TYPE_C_CHAT_ROOM_REVOKE,
                        needDelPoint, "", System.currentTimeMillis(), "");
                if (!succ) {
                    context.renderJSON(StatusCodes.ERR).renderMsg("少年，你的积分不足！要为自己的言行负责~");
                    return;
                }
                // 开启事务
                final Transaction transaction = chatRoomRepository.beginTransaction();
                // 撤回
                chatRoomRepository.remove(removeMessageId);
                // 提交事务
                transaction.commit();
                context.renderJSON(StatusCodes.SUCC).renderMsg("撤回成功，下次发消息一定要三思哦！本次消耗积分: " + needDelPoint);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Common.TYPE, "revoke");
                jsonObject.put("oId", removeMessageId);
                ChatroomChannel.notifyChat(jsonObject);
                revoke.put(curUser, date + "," + (times + 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.renderJSON(StatusCodes.ERR).renderMsg("撤回失败，请联系 @adlered。");
        }
    }
    /**
     * Get all messages from database.
     *
     * @return
     */
    public static List<JSONObject> getMessages(int page, String type) {
        try {
            final BeanManager beanManager = BeanManager.getInstance();
            final ChatRoomRepository chatRoomRepository = beanManager.getReference(ChatRoomRepository.class);
            final AvatarQueryService avatarQueryService = beanManager.getReference(AvatarQueryService.class);
            int start = 0;
            int count = 25;
            if (page > 1) {
                start = (page - 1) * 25;
            }
            List<JSONObject> messageList = chatRoomRepository.select("" +
                    "SELECT  *  FROM `" + chatRoomRepository.getName() + "` ORDER BY oId DESC LIMIT " + start + "," + count);
            List<JSONObject> msgs = messageList.stream().map(msg -> new JSONObject(msg.optString("content")).put("oId", msg.optString(Keys.OBJECT_ID))).collect(Collectors.toList());
            msgs = msgs.stream().map(msg -> JSONs.clone(msg).put(Common.TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.optLong(Common.TIME)))).collect(Collectors.toList());
            if(!"md".equals(type)){
                msgs = msgs.stream().map(msg -> JSONs.clone(msg.put("content", processMarkdown(msg.optString("content"))))).collect(Collectors.toList());
            }
            for (JSONObject msg : msgs) {
                avatarQueryService.fillUserAvatarURL(msg);
            }
            return msgs;
        } catch (RepositoryException e) {
            return new LinkedList<>();
        }
    }

    public static List<JSONObject> getContext(String oId, int size, int mode, String type) {
        try {
            final BeanManager beanManager = BeanManager.getInstance();
            final ChatRoomRepository chatRoomRepository = beanManager.getReference(ChatRoomRepository.class);
            List<JSONObject> msgs = null;
            /*
               mode = 0; 显示本条及之前之后消息
               mode = 1; 显示本条及之前消息
               mode = 2; 显示本条及之后消息
             */
            switch (mode) {
                case 0:
                    msgs = chatRoomRepository.select(
                            "(select * from " + chatRoomRepository.getName() + " where oId > ? order by oId asc limit ?) union" +
                                    "(select * from " + chatRoomRepository.getName() + " where oId = ?) union" +
                                    "(select * from " + chatRoomRepository.getName() + " where oId < ? order by oId desc limit ?) order by oId desc;",
                            oId, size, oId, oId, size
                    );
                    break;
                case 1:
                    msgs = chatRoomRepository.select(
                            "select * from " + chatRoomRepository.getName() + " where oId <= ? order by oId desc limit ?",
                            oId, (size + 1)
                    );
                    break;
                case 2:
                    msgs = chatRoomRepository.select(
                            "(select * from " + chatRoomRepository.getName() + " where oId >= ? order by oId asc limit ?) order by oId desc",
                            oId, (size + 1)
                    );
                    break;
                default:
                    return new ArrayList<>();
            }
            msgs = msgs.stream().map(msg -> new JSONObject(msg.optString("content")).put("oId", msg.optString(Keys.OBJECT_ID))).collect(Collectors.toList());
            msgs = msgs.stream().map(msg -> JSONs.clone(msg).put(Common.TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.optLong(Common.TIME)))).collect(Collectors.toList());
            if(!"md".equals(type)){
                msgs = msgs.stream().map(msg -> JSONs.clone(msg.put("content", processMarkdown(msg.optString("content"))))).collect(Collectors.toList());
            }
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

    private static RedPacket allocateRockRedPacket(String id, int gesture) {
        return new RockPaperScissorRedPacket.Builder()
                .setId(id)
                .setGesture(gesture)
                .build();
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

    public static class RockPaperScissorRedPacket extends RedPacket {
        public int gesture;

        public int getGesture() {
            return gesture;
        }

        public void setGesture(int gesture) {
            this.gesture = gesture;
        }

        private RockPaperScissorRedPacket(Builder builder) {
            gesture = builder.gesture;
            id = builder.id;
            sendId = builder.sendId;
            time = builder.time;
            packs = builder.packs;
            count = builder.count;
            money = builder.money;
        }


        public static final class Builder {
            private int gesture;
            private String id;
            private String sendId;
            private long time;
            private LinkedList<Integer> packs;
            private int count;
            private int money;

            public Builder() {
            }

            public Builder setGesture(int val) {
                gesture = val;
                return this;
            }

            public Builder setId(String val) {
                id = val;
                return this;
            }

            public Builder setSendId(String val) {
                sendId = val;
                return this;
            }

            public Builder setTime(long val) {
                time = val;
                return this;
            }

            public Builder setPacks(LinkedList<Integer> val) {
                packs = val;
                return this;
            }

            public Builder setCount(int val) {
                count = val;
                return this;
            }

            public Builder setMoney(int val) {
                money = val;
                return this;
            }

            public RockPaperScissorRedPacket build() {
                return new RockPaperScissorRedPacket(this);
            }
        }
    }


    public static class RedPacket {
        public String id;
        public String sendId;
        public long time;
        public LinkedList<Integer> packs;
        public int count;
        public int money;

        public RedPacket() {
        }

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
