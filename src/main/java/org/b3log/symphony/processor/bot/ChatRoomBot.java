package org.b3log.symphony.processor.bot;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.*;
import org.b3log.latke.service.ServiceException;
import org.b3log.symphony.model.*;
import org.b3log.symphony.processor.ApiProcessor;
import org.b3log.symphony.processor.ChatroomProcessor;
import org.b3log.symphony.processor.channel.ChatroomChannel;
import org.b3log.symphony.repository.ChatRoomRepository;
import org.b3log.symphony.repository.CloudRepository;
import org.b3log.symphony.service.CloudService;
import org.b3log.symphony.service.NotificationMgmtService;
import org.b3log.symphony.service.PointtransferMgmtService;
import org.b3log.symphony.util.JSONs;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.StatusCodes;
import org.json.JSONObject;
import pers.adlered.simplecurrentlimiter.main.SimpleCurrentLimiter;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 人工智障
 * 监督复读机、刷活跃、抢红包、无用消息
 */
public class ChatRoomBot {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ChatRoomBot.class);

    /**
     * 警告记录池，不同的记录池有不同的次数
     */
    private static final SimpleCurrentLimiter RECORD_POOL_2_IN_24H = new SimpleCurrentLimiter(24 * 60 * 60, 1);
    private static final SimpleCurrentLimiter RECORD_POOL_3_IN_15M = new SimpleCurrentLimiter(15 * 60, 2);
    private static final SimpleCurrentLimiter RECORD_POOL_5_IN_24H = new SimpleCurrentLimiter(24 * 60 * 60, 4);

    /**
     * 对应关系池
     */
    private static final Map<String, String> RECORD_MAP = Collections.synchronizedMap(new LinkedHashMap<String, String>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 100;
        }
    });

    private static String latestMessage = "";

    // 记录并分析消息是否可疑
    public static boolean record(final RequestContext context) {
        boolean pass = true;
        String reason = "";
        final JSONObject requestJSONObject = (JSONObject) context.attr(Keys.REQUEST);
        JSONObject currentUser = Sessions.getUser();
        try {
            currentUser = ApiProcessor.getUserByKey(requestJSONObject.optString("apiKey"));
        } catch (NullPointerException ignored) {
        }

        // ==? 前置参数 ?==
        String content = requestJSONObject.optString(Common.CONTENT);
        String userName = currentUser.optString(User.USER_NAME);
        String userId = currentUser.optString(Keys.OBJECT_ID);
        // ==! 前置参数 !==

        // ==? 是否禁言中 ?==
        int muteTime = muted(userId);
        int muteMinute = muteTime % ( 24  *  60  *  60 ) % ( 60  *  60 ) /  60;
        int muteSecond = muteTime % ( 24  *  60  *  60 ) % ( 60  *  60 ) %  60;
        if (muteTime != -1) {
            if (muteMinute != 0) {
                context.renderJSON(StatusCodes.ERR).renderMsg("你的消息被机器人打回，原因：正在禁言中，剩余时间 " + muteMinute + " 分钟 " + muteSecond + " 秒");
            } else {
                context.renderJSON(StatusCodes.ERR).renderMsg("你的消息被机器人打回，原因：正在禁言中，剩余时间 " + muteSecond + " 秒");
            }
            return false;
        }
        // ==! 是否禁言中 !==

        // ==? 判定恶意发送非法红包 ?==
        try {
            JSONObject checkContent = new JSONObject(content);
            if (checkContent.optString("msgType").equals("redPacket")) {
                if (RECORD_POOL_2_IN_24H.access(userName)) {
                    sendBotMsg("监测到 @" + userName + "  伪造发送红包数据包，警告一次。");
                } else {
                    sendBotMsg("由于 @" + userName + "  第二次伪造发送红包数据包，现处以扣除积分 50 的处罚。");
                    abusePoint(userId, 50, "机器人罚单-聊天室伪造发送红包数据包");
                    RECORD_POOL_2_IN_24H.remove(userName);
                }
            }
        } catch (Exception ignored) {
        }
        // ==! 判定恶意发送非法红包 !==

        // ==? 判定复读机 ?==
        if (content.equals(latestMessage)) {
            // 与上条内容相同
            if (RECORD_POOL_3_IN_15M.access(userName)) {
                if (RECORD_POOL_3_IN_15M.get(userName).getFrequency() == 2) {
                    sendBotMsg("监测到 @" + userName + "  疑似使用自动复读机插件，请不要频繁复读。");
                }
            } else {
                sendBotMsg("由于 @" + userName + "  频繁复读，现处以撤回消息、禁言 15 分钟、扣除积分 30 的处罚。");
                pass = false;
                reason = "请注意，你已经频繁复读";
                mute(userId, 15);
                abusePoint(userId, 30, "机器人罚单-聊天室复读频率过高");
                RECORD_POOL_3_IN_15M.remove(userName);
            }
        }
        // ==! 判定复读机 !==

        latestMessage = content;
        if (!pass) {
            context.renderJSON(StatusCodes.ERR).renderMsg("你的消息被机器人打回，原因：" + reason);
            return false;
        }
        return true;
    }

    // 以人工智障的身份发送消息
    public static void sendBotMsg(String content) {
        final long time = System.currentTimeMillis();
        JSONObject msg = new JSONObject();
        msg.put(User.USER_NAME, "摸鱼派官方巡逻机器人");
        msg.put(UserExt.USER_AVATAR_URL, "https://pwl.stackoverflow.wiki/2022/01/robot3-89631199.png");
        msg.put(Common.CONTENT, content);
        msg.put(Common.TIME, time);
        msg.put(UserExt.USER_NICKNAME, "人工智障");
        msg.put("sysMetal", "");
        // 聊天室内容保存到数据库
        final BeanManager beanManager = BeanManager.getInstance();
        ChatRoomRepository chatRoomRepository = beanManager.getReference(ChatRoomRepository.class);
        final Transaction transaction = chatRoomRepository.beginTransaction();
        try {
            String oId = chatRoomRepository.add(new JSONObject().put("content", msg.toString()));
            msg.put("oId", oId);
        } catch (RepositoryException e) {
            LOGGER.log(Level.ERROR, "Cannot save ChatRoom bot message to the database.", e);
        }
        transaction.commit();
        msg = msg.put("md", msg.optString(Common.CONTENT)).put(Common.CONTENT, ChatroomProcessor.processMarkdown(msg.optString(Common.CONTENT)));
        final JSONObject pushMsg = JSONs.clone(msg);
        pushMsg.put(Common.TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(msg.optLong(Common.TIME)));
        ChatroomChannel.notifyChat(pushMsg);

        try {
            ChatroomProcessor chatroomProcessor = beanManager.getReference(ChatroomProcessor.class);
            NotificationMgmtService notificationMgmtService = beanManager.getReference(NotificationMgmtService.class);
            final List<JSONObject> atUsers = chatroomProcessor.atUsers(msg.optString(Common.CONTENT), "admin");
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
    }

    // 扣除积分
    public static void abusePoint(String userId, int point, String memo) {
        final BeanManager beanManager = BeanManager.getInstance();
        PointtransferMgmtService pointtransferMgmtService = beanManager.getReference(PointtransferMgmtService.class);
        final String transferId = pointtransferMgmtService.transfer(userId, Pointtransfer.ID_C_SYS,
                Pointtransfer.TRANSFER_TYPE_C_ABUSE_DEDUCT, point, memo, System.currentTimeMillis(), "");

        NotificationMgmtService notificationMgmtService = beanManager.getReference(NotificationMgmtService.class);
        final JSONObject notification = new JSONObject();
        notification.put(Notification.NOTIFICATION_USER_ID, userId);
        notification.put(Notification.NOTIFICATION_DATA_ID, transferId);
        try {
            notificationMgmtService.addAbusePointDeductNotification(notification);
        } catch (ServiceException e) {
            LOGGER.log(Level.ERROR, "Unable to send abuse notify", e);
        }
    }

    // 禁言
    public static void mute(String userId, int minute) {
        final BeanManager beanManager = BeanManager.getInstance();
        CloudRepository cloudRepository = beanManager.getReference(CloudRepository.class);
        int muteTime = minute * 1000 * 60;
        try {
            final Transaction transaction = cloudRepository.beginTransaction();
            Query cloudDeleteQuery = new Query()
                    .setFilter(CompositeFilterOperator.and(
                            new PropertyFilter("userId", FilterOperator.EQUAL, userId),
                            new PropertyFilter("gameId", FilterOperator.EQUAL, CloudService.SYS_MUTE)
                    ));
            cloudRepository.remove(cloudDeleteQuery);
            JSONObject cloudJSON = new JSONObject();
            cloudJSON.put("userId", userId)
                    .put("gameId", CloudService.SYS_MUTE)
                    .put("data", ("" + (System.currentTimeMillis() + muteTime)));
            cloudRepository.add(cloudJSON);
            transaction.commit();
        } catch (RepositoryException e) {
            LOGGER.log(Level.ERROR, "Unable to mute [userId={}]", userId);
        }
    }

    // 禁言并提醒
    public static void muteAndNotice(String username, String userId, int minute) {
        sendBotMsg("提醒：@" + username + "  被管理员禁言 " + minute + " 分钟。");
        mute(userId, minute);
    }

    // 检查禁言
    public static int muted(String userId) {
        final BeanManager beanManager = BeanManager.getInstance();
        CloudService cloudService = beanManager.getReference(CloudService.class);
        CloudRepository cloudRepository = beanManager.getReference(CloudRepository.class);

        String muteData = cloudService.getFromCloud(userId, CloudService.SYS_MUTE);
        if (muteData.isEmpty()) {
            return -1;
        } else {
            if (System.currentTimeMillis() > Long.parseLong(muteData)) {
                try {
                    final Transaction transaction = cloudRepository.beginTransaction();
                    Query cloudDeleteQuery = new Query()
                            .setFilter(CompositeFilterOperator.and(
                                    new PropertyFilter("userId", FilterOperator.EQUAL, userId),
                                    new PropertyFilter("gameId", FilterOperator.EQUAL, CloudService.SYS_MUTE)
                            ));
                    cloudRepository.remove(cloudDeleteQuery);
                    transaction.commit();
                } catch (RepositoryException e) {
                    LOGGER.log(Level.ERROR, "Unable to unmute", e);
                }
                return -1;
            } else {
                long remainMinute = (Long.parseLong(muteData) - System.currentTimeMillis()) / 1000;
                return (int) remainMinute;
            }
        }
    }
}
