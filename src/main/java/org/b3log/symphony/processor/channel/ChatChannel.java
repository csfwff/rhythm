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

import org.b3log.latke.Keys;
import org.b3log.latke.http.Session;
import org.b3log.latke.http.WebSocketChannel;
import org.b3log.latke.http.WebSocketSession;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.symphony.processor.ApiProcessor;
import org.b3log.symphony.service.UserQueryService;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User channel.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Nov 6, 2019
 * @since 1.4.0
 */
@Singleton
public class ChatChannel implements WebSocketChannel {

    @Inject
    private UserQueryService userQueryService;

    /**
     * Session set.
     */
    public static final Map<String, Set<WebSocketSession>> SESSIONS = new ConcurrentHashMap();

    /**
     * Session set.
     */
    public static final Set<String> KEYS = Collections.newSetFromMap(new ConcurrentHashMap());

    /**
     * Called when the socket connection with the browser is established.
     *
     * @param session session
     */
    @Override
    public void onConnect(final WebSocketSession session) {
        JSONObject user = null;
        try {
            user = ApiProcessor.getUserByKey(session.getParameter("apiKey"));
        } catch (NullPointerException ignored) {
        }
        if (null == user) {
            session.close();
            return;
        }
        String toUser = session.getParameter("toUser");
        JSONObject toUserJSON = userQueryService.getUserByName(toUser);
        if (toUserJSON == null) {
            session.close();
            return;
        }
        String toUserId = toUserJSON.optString(Keys.OBJECT_ID);
        String userId = user.optString(Keys.OBJECT_ID);
        if (userId.equals(toUserId)) {
            session.close();
            return;
        }
        String chatHex = userId + toUserId;

        final Set<WebSocketSession> userSessions = SESSIONS.getOrDefault(chatHex, Collections.newSetFromMap(new ConcurrentHashMap()));
        userSessions.add(session);

        final Session httpSession = session.getHttpSession();
        httpSession.setAttribute(User.USER, user.toString());

        SESSIONS.put(chatHex, userSessions);
        KEYS.add(chatHex);
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
        // 1. 查询列表
        final Set<String> pairKeys = Collections.newSetFromMap(new ConcurrentHashMap());
        for (String key : KEYS) {
            if (key.startsWith(userId)) {
                pairKeys.add(userId);
            }
        }
        // 2. 删除KEYS
        for (String key : pairKeys) {
            KEYS.remove(key);
        }
        // 3. 删除SESSIONS
        for (String key : pairKeys) {
            Set<WebSocketSession> userSessions = SESSIONS.get(key);
            userSessions.remove(session);
        }
    }
}
