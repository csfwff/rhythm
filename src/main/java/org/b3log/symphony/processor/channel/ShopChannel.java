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

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.http.Session;
import org.b3log.latke.http.WebSocketChannel;
import org.b3log.latke.http.WebSocketSession;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.ApiProcessor;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shop channel.
 *
 * @author <a href="https://github.com/adlered">adlered</a>
 */
@Singleton
public class ShopChannel implements WebSocketChannel {

    /**
     * Session set.
     */
    public static final Map<String, Set<WebSocketSession>> SESSIONS = new ConcurrentHashMap();

    /**
     * Called when the socket connection with the browser is established.
     *
     * @param session session
     */
    @Override
    public void onConnect(final WebSocketSession session) {
        JSONObject user = new JSONObject(session.getHttpSession().getAttribute(User.USER));
        try {
            user = ApiProcessor.getUserByKey(session.getParameter("apiKey"));
        } catch (NullPointerException ignored) {
        }

        if (null == user) {
            return;
        }

        final String userId = user.optString(Keys.OBJECT_ID);
        final Set<WebSocketSession> userSessions = SESSIONS.getOrDefault(userId, Collections.newSetFromMap(new ConcurrentHashMap()));
        userSessions.add(session);

        SESSIONS.put(userId, userSessions);

        final JSONObject cmd = new JSONObject();
        cmd.put(UserExt.USER_T_ID, userId);
        cmd.put("message", "<br>" +
                ".------..------..------..------.<br>" +
                "|S.--. ||H.--. ||O.--. ||P.--. |<br>" +
                "| :/\\: || :/\\: || :/\\: || :/\\: |<br>" +
                "| :\\/: || (__) || :\\/: || (__) |<br>" +
                "| '--'S|| '--'H|| '--'O|| '--'P|<br>" +
                "`------'`------'`------'`------'<br>" +
                "连接成功！欢迎来到系统商店，" + user.optString(User.USER_NAME) + "！");
        sendMsg(cmd);
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
     * Sends command to browsers.
     *
     * @param message the specified message, for example,
     *                "userId": "",
     *                "cmd": ""
     */
    public static void sendMsg(final JSONObject message) {
        final String recvUserId = message.optString(UserExt.USER_T_ID);
        if (StringUtils.isBlank(recvUserId)) {
            return;
        }

        final String msgStr = message.toString();

        for (final String userId : SESSIONS.keySet()) {
            if (userId.equals(recvUserId)) {
                final Set<WebSocketSession> sessions = SESSIONS.get(userId);
                for (final WebSocketSession session : sessions) {
                    session.sendText(msgStr);
                }
            }
        }
    }

    /**
     * Removes the specified session.
     *
     * @param session the specified session
     */
    private void removeSession(final WebSocketSession session) {
        final Session httpSession = session.getHttpSession();
        final String userStr = httpSession.getAttribute(User.USER);
        if (null == userStr) {
            return;
        }
        final JSONObject user = new JSONObject(userStr);
        final String userId = user.optString(Keys.OBJECT_ID);
        Set<WebSocketSession> userSessions = SESSIONS.get(userId);
        userSessions.remove(session);
    }
}
