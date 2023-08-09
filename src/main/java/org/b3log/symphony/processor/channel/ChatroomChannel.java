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
package org.b3log.symphony.processor.channel;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.WebSocketChannel;
import org.b3log.latke.http.WebSocketSession;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.jdbc.JdbcRepository;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.ApiProcessor;
import org.b3log.symphony.repository.CloudRepository;
import org.b3log.symphony.service.AvatarQueryService;
import org.b3log.symphony.service.UserQueryService;
import org.json.JSONArray;
import org.json.JSONObject;
import pers.adlered.simplecurrentlimiter.main.SimpleCurrentLimiter;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Chatroom channel.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Nov 6, 2019
 * @since 1.4.0
 */
@Singleton
public class ChatroomChannel implements WebSocketChannel {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ChatroomChannel.class);

    /**
     * Session set.
     */
    public static final Set<WebSocketSession> SESSIONS = Collections.newSetFromMap(new ConcurrentHashMap());

    /**
     * Online user information storage.
     */
    public static final Map<WebSocketSession, JSONObject> onlineUsers = Collections.synchronizedMap(new HashMap<>());

    /**
     * 当前讨论话题
     */
    public static String discussing = "暂无";

    public static final Map<String, Long> userActive = Collections.synchronizedMap(new HashMap<>());

    /**
     * Called when the socket connection with the browser is established.
     *
     * @param session session
     */
    @Override
    public void onConnect(final WebSocketSession session) {
        String userStr = session.getHttpSession().getAttribute(User.USER);
        try {
            userStr = ApiProcessor.getUserByKey(session.getParameter("apiKey")).toString();
        } catch (NullPointerException ignored) {
        }
        if (null != userStr) {
            final JSONObject user = new JSONObject(userStr);
            String userName = user.optString(User.USER_NAME);
            boolean joined = true;
            for (Map.Entry<WebSocketSession, JSONObject> entry : onlineUsers.entrySet()) {
                String name = entry.getValue().optString(User.USER_NAME);
                if (userName.equals(name)) {
                    joined = false;
                }
            }
            if (joined) {
                String msg = getCustomMessage(1, userName);
                if (!msg.isEmpty()) {
                    sendCustomMessage(msg);
                }
            }
            onlineUsers.put(session, user);
            SESSIONS.add(session);
            // 单独发送在线信息
            final String msgStr = getOnline().toString();
            session.sendText(msgStr);
            // 保存 Active 信息
            userActive.put(user.optString("userName"), System.currentTimeMillis());
        } else {
            session.close();
        }
    }

    private static SimpleCurrentLimiter customMessageCurrentLimit = new SimpleCurrentLimiter(60, 6);
    public static String getCustomMessage(int type, String userName) {
        if (customMessageCurrentLimit.access(userName)) {
            final BeanManager beanManager = BeanManager.getInstance();
            UserQueryService userQueryService = beanManager.getReference(UserQueryService.class);
            CloudRepository cloudRepository = beanManager.getReference(CloudRepository.class);
            JSONObject user = userQueryService.getUserByName(userName);
            String userId = user.optString(Keys.OBJECT_ID);
            String msg = "";

            try {
                Query cloudQuery = new Query()
                        .setFilter(CompositeFilterOperator.and(
                                new PropertyFilter("userId", FilterOperator.EQUAL, userId),
                                new PropertyFilter("gameId", FilterOperator.EQUAL, "sys-custom-message")
                        ));
                JSONObject result = cloudRepository.getFirst(cloudQuery);
                String data = result.optString("data");
                if (data.contains("&&&")) {
                    if (type == 1) {
                        msg = data.split("&&&")[0];
                    } else if (type == 0) {
                        msg = data.split("&&&")[1];
                    }
                } else {
                    msg = "";
                }
            } catch (Exception e) {
                msg = "";
            }

            if (!msg.isEmpty()) {
                msg = msg.replaceAll("\\{userName}", user.optString(User.USER_NAME));
                msg = msg.replaceAll("\\{userNickName}", user.optString(UserExt.USER_NICKNAME));
                msg = msg.replaceAll("\\{userPoint}", user.optString(UserExt.USER_POINT));
            }
            return msg;
        } else {
            return "";
        }
    }

    public static boolean removeCustomMessage(String userName) {
        final BeanManager beanManager = BeanManager.getInstance();
        UserQueryService userQueryService = beanManager.getReference(UserQueryService.class);
        CloudRepository cloudRepository = beanManager.getReference(CloudRepository.class);
        String userId = userQueryService.getUserByName(userName).optString(Keys.OBJECT_ID);

        try {
            final Transaction transaction = cloudRepository.beginTransaction();
            Query cloudDeleteQuery = new Query()
                    .setFilter(CompositeFilterOperator.and(
                            new PropertyFilter("userId", FilterOperator.EQUAL, userId),
                            new PropertyFilter("gameId", FilterOperator.EQUAL, "sys-custom-message")
                    ));
            cloudRepository.remove(cloudDeleteQuery);
            transaction.commit();

            return true;
        } catch (RepositoryException e) {
            return false;
        }
    }

    public static boolean addCustomMessage(String userName, String message) {
        final BeanManager beanManager = BeanManager.getInstance();
        UserQueryService userQueryService = beanManager.getReference(UserQueryService.class);
        CloudRepository cloudRepository = beanManager.getReference(CloudRepository.class);
        String userId = userQueryService.getUserByName(userName).optString(Keys.OBJECT_ID);

        try {
            final Transaction transaction = cloudRepository.beginTransaction();
            Query cloudDeleteQuery = new Query()
                    .setFilter(CompositeFilterOperator.and(
                            new PropertyFilter("userId", FilterOperator.EQUAL, userId),
                            new PropertyFilter("gameId", FilterOperator.EQUAL, "sys-custom-message")
                    ));
            cloudRepository.remove(cloudDeleteQuery);
            JSONObject cloudJSON = new JSONObject();
            cloudJSON.put("userId", userId)
                    .put("gameId", "sys-custom-message")
                    .put("data", message);
            cloudRepository.add(cloudJSON);
            transaction.commit();

            return true;
        } catch (RepositoryException e) {
            return false;
        }
    }

    public static void sendCustomMessage(String msg) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Common.TYPE, "customMessage");
        jsonObject.put("message", msg);
        String message = jsonObject.toString();
        new Thread(() -> {
            int i = 0;
            for (WebSocketSession s : ChatroomChannel.SESSIONS) {
                i++;
                if (i % 50 == 0) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception ignored) {
                    }
                }
                s.sendText(message);
            }
        }).start();
    }

    /**
     * Called when the connection closed.
     *
     * @param session session
     */
    @Override
    public void onClose(final WebSocketSession session) {
        removeSession(session);
    }

    /**
     * Called when a message received from the browser.
     *
     * @param message message
     */
    @Override
    public void onMessage(final Message message) {
    }

    /**
     * Called when a error received.
     *
     * @param error error
     */
    @Override
    public void onError(final Error error) {
        removeSession(error.session);
    }

    /**
     * Notifies the specified chat message to browsers.
     *
     * @param message the specified message, for example,
     *                {
     *                "userName": "",
     *                "content": ""
     *                }
     */
    public static int notQuickCheck = 50;
    public static int notQuickSleep = 100;
    public static int quickCheck = 100;
    public static int quickSleep = 100;

    //用于消息发送的线程池
    private static final ThreadPoolExecutor MESSAGE_POOL = new ThreadPoolExecutor(
            4,
            4,
            120L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>()
            );

    public static void notifyChat(final JSONObject message) {
        final BeanManager beanManager = BeanManager.getInstance();
        final AvatarQueryService avatarQueryService = beanManager.getReference(AvatarQueryService.class);
        if (!message.has(Common.TYPE)) {
            message.put(Common.TYPE, "msg");
        }
        String type = message.optString(Common.TYPE);
        String sender = message.optString(User.USER_NAME);
        boolean isRedPacket = false;
        try {
            JSONObject content = new JSONObject(message.optString("content"));
            String msgType = content.optString("msgType");
            if (msgType.equals("redPacket")) {
                isRedPacket = true;
            }
        } catch (Exception ignored) {
        }
        boolean quick = sender.isEmpty() || isRedPacket || type.equals("redPacketStatus") || type.equals("revoke") || type.equals("discussChanged");
        avatarQueryService.fillUserAvatarURL(message);
        final String msgStr = message.toString();
        // 先给发送人反馈
        if (!quick) {
            List<WebSocketSession> senderSessions = new ArrayList<>();
            for (Map.Entry<WebSocketSession, JSONObject> entry : onlineUsers.entrySet()) {
                String userName = entry.getValue().optString(User.USER_NAME);
                if (userName.equals(sender)) {
                    senderSessions.add(entry.getKey());
                }
            }
            for (WebSocketSession session : senderSessions) {
                session.sendText(msgStr);
            }
        }
        MESSAGE_POOL.submit(() -> {
            int i = 0;
            for (WebSocketSession session : SESSIONS) {
                try {
                    i++;
                    if (!quick && i % notQuickCheck == 0) {
                        try {
                            Thread.sleep(notQuickSleep);
                        } catch (Exception ignored) {
                        }
                    } else if (quick && i % quickCheck == 0) {
                        try {
                            Thread.sleep(quickSleep);
                        } catch (Exception ignored) {
                        }
                    }
                    if (!quick) {
                        String toUser = onlineUsers.get(session).optString(User.USER_NAME);
                        if (!sender.equals(toUser)) {
                            session.sendText(msgStr);
                        }
                    } else {
                        session.sendText(msgStr);
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    /**
     * Removes the specified session.
     *
     * @param session the specified session
     */
    public static synchronized void removeSession(final WebSocketSession session) {
        String userName = "";
        try {
            JSONObject user = onlineUsers.get(session);
            userName = user.optString(User.USER_NAME);
            onlineUsers.remove(session);
            boolean left = true;
            for (Map.Entry<WebSocketSession, JSONObject> entry : onlineUsers.entrySet()) {
                String name = entry.getValue().optString(User.USER_NAME);
                if (userName.equals(name)) {
                    left = false;
                }
            }
            if (left) {
                String msg = getCustomMessage(0, userName);
                if (!msg.isEmpty()) {
                    sendCustomMessage(msg);
                }
            }
        } catch (NullPointerException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        SESSIONS.remove(session);
    }

    public static Map<String, Long> check() {
        Map<String, JSONObject> filteredOnlineUsers = new HashMap<>();
        for (JSONObject object : onlineUsers.values()) {
            String name = object.optString(User.USER_NAME);
            filteredOnlineUsers.put(name, object);
        }
        Long currentTime = System.currentTimeMillis();
        int sixHours = 1000 * 60 * 60 * 6;
        Map<String, Long> needKickUsers = new HashMap<>();
        List<String> users = new ArrayList<>();
        for (String user : filteredOnlineUsers.keySet()) {
            try {
                Long activeTime = userActive.get(user);
                Long spareTime = currentTime - activeTime;
                if (spareTime >= sixHours) {
                    needKickUsers.put(user, (spareTime / (1000 * 60 * 60)));
                    users.add(user);
                }
            } catch (Exception ignored) {
            }
        }

        new Thread(() -> {
            List<WebSocketSession> senderSessions = new ArrayList<>();
            for (Map.Entry<WebSocketSession, JSONObject> entry : onlineUsers.entrySet()) {
                try {
                    String tempUserName = entry.getValue().optString(User.USER_NAME);
                    if (users.contains(tempUserName)) {
                        senderSessions.add(entry.getKey());
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.ERROR, "Failed to log user entry", e);
                }
            }
            for (WebSocketSession session : senderSessions) {
                session.sendText("{\n" +
                        "    \"md\": \"由于您超过6小时未活跃，已将您断开连接，如要继续聊天请刷新页面，谢谢 :)\",\n" +
                        "    \"userAvatarURL\": \"https://pwl.stackoverflow.wiki/2022/01/robot3-89631199.png\",\n" +
                        "    \"userAvatarURL20\": \"https://pwl.stackoverflow.wiki/2022/01/robot3-89631199.png\",\n" +
                        "    \"userNickname\": \"人工智障\",\n" +
                        "    \"sysMetal\": \"\",\n" +
                        "    \"time\": \"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()) + "\",\n" +
                        "    \"oId\": \"" + System.currentTimeMillis() + "\",\n" +
                        "    \"userName\": \"摸鱼派官方巡逻机器人\",\n" +
                        "    \"type\": \"msg\",\n" +
                        "    \"userAvatarURL210\": \"https://pwl.stackoverflow.wiki/2022/01/robot3-89631199.png\",\n" +
                        "    \"content\": \"<p>由于您超过6小时未活跃，已将您断开连接，如要继续聊天请刷新页面，谢谢 :)</p>\",\n" +
                        "    \"client\": \"Other/Robot\",\n" +
                        "    \"userAvatarURL48\": \"https://pwl.stackoverflow.wiki/2022/01/robot3-89631199.png\"\n" +
                        "}");
                removeSession(session);
            }
            JdbcRepository.dispose();
        }).start();

        return needKickUsers;
    }

    /**
     * 获得聊天室在线人数和在线成员信息
     * @return
     */
    public static JSONObject getOnline() {
        final BeanManager beanManager = BeanManager.getInstance();
        final AvatarQueryService avatarQueryService = beanManager.getReference(AvatarQueryService.class);
        try {
            // 使用 HashMap 去重
            Map<String, JSONObject> filteredOnlineUsers = new HashMap<>();
            for (JSONObject object : onlineUsers.values()) {
                String name = object.optString(User.USER_NAME);
                filteredOnlineUsers.put(name, object);
            }

            JSONArray onlineArray = new JSONArray();
            for (String user : filteredOnlineUsers.keySet()) {
                JSONObject object = filteredOnlineUsers.get(user);

                String avatar = object.optString(UserExt.USER_AVATAR_URL);
                String homePage = Latkes.getStaticServePath() + "/member/" + user;

                JSONObject generated = new JSONObject();
                generated.put(User.USER_NAME, user);
                generated.put(UserExt.USER_AVATAR_URL, avatar);
                avatarQueryService.fillUserAvatarURL(generated);
                generated.put("homePage", homePage);
                onlineArray.put(generated);
            }

            JSONObject result = new JSONObject();
            result.put(Common.ONLINE_CHAT_CNT, filteredOnlineUsers.size());
            result.put(Common.TYPE, "online");
            result.put("users", onlineArray);
            result.put("discussing", discussing);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject().put(Common.ONLINE_CHAT_CNT, 99999).put(Common.TYPE, "online").put("users", new JSONArray());
    }

    // 发送在线信息
    private static boolean onlineMsgLock = false;
    public static void sendOnlineMsg() {
        if (!onlineMsgLock) {
            onlineMsgLock = true;
            final String msgStr = getOnline().toString();
            new Thread(() -> {
                int i = 0;
                for (WebSocketSession s : SESSIONS) {
                    i++;
                    if (i % 1 == 0) {
                        try {
                            Thread.sleep(500);
                        } catch (Exception ignored) {
                        }
                    }
                    s.sendText(msgStr);
                }
                onlineMsgLock = false;
            }).start();
        }
    }
}
