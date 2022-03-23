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
package org.b3log.symphony.cache;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.service.AvatarQueryService;
import org.b3log.symphony.util.JSONs;
import org.b3log.symphony.util.Sessions;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * User cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.1, Jul 30, 2018
 * @since 1.4.0
 */
@Singleton
public class UserCache {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(UserCache.class);

    /**
     * Id, User.
     */
    private static final Map<String, JSONObject> ID_CACHE = Collections.synchronizedMap(new LinkedHashMap<String, JSONObject>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 100;
        }
    });

    /**
     * Name, User.
     */
    private static final Map<String, JSONObject> NAME_CACHE = Collections.synchronizedMap(new LinkedHashMap<String, JSONObject>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 100;
        }
    });

    /**
     * Administrators cache.
     */
    private static final List<JSONObject> ADMINS_CACHE = new CopyOnWriteArrayList<>();

    /**
     * Nice users cache.
     */
    private static final List<JSONObject> NICE_USERS = new ArrayList<>();

    /**
     * Gets admins.
     *
     * @return admins
     */
    public List<JSONObject> getAdmins() {
        return ADMINS_CACHE;
    }

    /**
     * Gets nice users.
     *
     * @return nice users.
     */
    public List<JSONObject> getNiceUsers(int fetchSize) {
        if (NICE_USERS.isEmpty()) {
            return Collections.emptyList();
        }

        final List<JSONObject> ret = new ArrayList<>();
        int realLen = NICE_USERS.size();
        final List<Integer> indices = CollectionUtils.getRandomIntegers(0, realLen, fetchSize);
        for (final Integer index : indices) {
            ret.add(NICE_USERS.get(index));
        }

        return JSONs.clone(ret);
    }

    /**
     * Load nice users.
     */
    public void loadNiceUsers() {
        final BeanManager beanManager = BeanManager.getInstance();
        final UserRepository userRepository = beanManager.getReference(UserRepository.class);
        final AvatarQueryService avatarQueryService = beanManager.getReference(AvatarQueryService.class);

        final List<JSONObject> ret = new ArrayList<>();

        final int RANGE_SIZE = 64;
        int fetchSize = 64;

        try {
            final Query userQuery = new Query().
                    setPage(1, RANGE_SIZE).setPageCount(1).
                    setFilter(new PropertyFilter(UserExt.USER_STATUS, FilterOperator.EQUAL, UserExt.USER_STATUS_C_VALID)).
                    addSort(UserExt.USER_ARTICLE_COUNT, SortDirection.DESCENDING).
                    addSort(UserExt.USER_COMMENT_COUNT, SortDirection.DESCENDING);
            final List<JSONObject> rangeUsers = userRepository.getList(userQuery);
            final int realLen = rangeUsers.size();
            if (realLen < fetchSize) {
                fetchSize = realLen;
            }

            final List<Integer> indices = CollectionUtils.getRandomIntegers(0, realLen, fetchSize);
            for (final Integer index : indices) {
                ret.add(rangeUsers.get(index));
            }

            for (final JSONObject selectedUser : ret) {
                avatarQueryService.fillUserAvatarURL(selectedUser);
            }

            NICE_USERS.clear();
            NICE_USERS.addAll(ret);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get nice users failed", e);
        }
    }

    /**
     * Puts the specified admins.
     *
     * @param admins the specified admins
     */
    public void putAdmins(final List<JSONObject> admins) {
        ADMINS_CACHE.clear();
        ADMINS_CACHE.addAll(admins);
    }

    /**
     * Gets a user by the specified user id.
     *
     * @param userId the specified user id
     * @return user, returns {@code null} if not found
     */
    public JSONObject getUser(final String userId) {
        final JSONObject user = ID_CACHE.get(userId);
        if (null == user) {
            return null;
        }

        return JSONs.clone(user);
    }

    /**
     * Gets a user by the specified user name.
     *
     * @param userName the specified user name
     * @return user, returns {@code null} if not found
     */
    public JSONObject getUserByName(final String userName) {
        final JSONObject user = NAME_CACHE.get(userName);
        if (null == user) {
            return null;
        }

        return JSONs.clone(user);
    }

    /**
     * Adds or updates the specified user.
     *
     * @param user the specified user
     */
    public void putUser(final JSONObject user) {
        ID_CACHE.put(user.optString(Keys.OBJECT_ID), JSONs.clone(user));
        NAME_CACHE.put(user.optString(User.USER_NAME), JSONs.clone(user));

        Sessions.put(user.optString(Keys.OBJECT_ID), user);
    }

    /**
     * Removes the the specified user.
     *
     * @param user the specified user
     */
    public void removeUser(final JSONObject user) {
        ID_CACHE.remove(user.optString(Keys.OBJECT_ID));
        NAME_CACHE.remove(user.optString(User.USER_NAME));
    }
}
