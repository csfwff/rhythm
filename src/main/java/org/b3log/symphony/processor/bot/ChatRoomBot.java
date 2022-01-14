package org.b3log.symphony.processor.bot;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.Notification;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.ChatroomProcessor;
import org.b3log.symphony.processor.channel.ChatroomChannel;
import org.b3log.symphony.repository.ChatRoomRepository;
import org.b3log.symphony.service.NotificationMgmtService;
import org.b3log.symphony.util.JSONs;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

/**
 * 人工智障
 * 监督复读机、刷活跃、抢红包、无用消息
 */
public class ChatRoomBot {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ChatRoomBot.class);

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
}
