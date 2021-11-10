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

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Requests;
import org.b3log.symphony.model.*;
import org.b3log.symphony.processor.middleware.CSRFMidware;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.processor.middleware.validate.Activity1A0001CollectValidationMidware;
import org.b3log.symphony.processor.middleware.validate.Activity1A0001ValidationMidware;
import org.b3log.symphony.service.*;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.StatusCodes;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 专业团队，专业的 API 接口
 */
@Singleton
public class ApiProcessor {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ApiProcessor.class);

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * 存储用户的Key
     */
    public static Map<String, JSONObject> keys = new HashMap<>();

    /**
     * Follow query service.
     */
    @Inject
    private FollowQueryService followQueryService;

    /**
     * Role query service.
     */
    @Inject
    private RoleQueryService roleQueryService;

    /**
     * System settings service.
     */
    @Inject
    private SystemSettingsService systemSettingsService;

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);
        final CSRFMidware csrfMidware = beanManager.getReference(CSRFMidware.class);

        final ApiProcessor apiProcessor = beanManager.getReference(ApiProcessor.class);
        Dispatcher.get("/api/user/exists/{user}", apiProcessor::userExists);
        Dispatcher.post("/api/getKey", apiProcessor::getKey);
        Dispatcher.get("/api/user", apiProcessor::getUser);

        final RewardQueryService rewardQueryService = beanManager.getReference(RewardQueryService.class);
        Dispatcher.get("/api/article/reward/senders/{aId}", rewardQueryService::rewardedSenders);
    }

    /**
     * 通过用户名和密码进行登录，然后发放Key通行证
     */
    public void getKey(final RequestContext context) {
        context.renderJSON(StatusCodes.ERR).renderMsg(langPropsService.get("loginFailLabel"));
        final JSONObject requestJSONObject = context.requestJSON();
        final String nameOrEmail = requestJSONObject.optString("nameOrEmail");

        try {
            JSONObject user = userQueryService.getUserByName(nameOrEmail);
            if (null == user) {
                user = userQueryService.getUserByEmail(nameOrEmail);
            }

            if (null == user) {
                context.renderMsg(langPropsService.get("notFoundUserLabel"));
                return;
            }

            if (UserExt.USER_STATUS_C_INVALID == user.optInt(UserExt.USER_STATUS)) {
                context.renderMsg(langPropsService.get("userBlockLabel"));
                return;
            }

            if (UserExt.USER_STATUS_C_NOT_VERIFIED == user.optInt(UserExt.USER_STATUS)) {
                context.renderMsg(langPropsService.get("notVerifiedLabel"));
                return;
            }

            if (UserExt.USER_STATUS_C_INVALID_LOGIN == user.optInt(UserExt.USER_STATUS)
                    || UserExt.USER_STATUS_C_DEACTIVATED == user.optInt(UserExt.USER_STATUS)) {
                context.renderMsg(langPropsService.get("invalidLoginLabel"));
                return;
            }

            final String userPassword = user.optString(User.USER_PASSWORD);
            if (userPassword.equals(requestJSONObject.optString(User.USER_PASSWORD))) {
                final String key = RandomStringUtils.randomAlphanumeric(32);
                context.renderCodeMsg(StatusCodes.SUCC, "");
                context.renderJSONValue("Key", key);
                keys.put(key, user);

                return;
            }

            context.renderMsg(langPropsService.get("wrongPwdLabel"));
        } catch (final ServiceException e) {
            context.renderMsg(langPropsService.get("loginFailLabel"));
        }
    }

    /**
     * 查询指定apiKey的用户信息
     */
    public void getUser(final RequestContext context) {
        JSONObject ret = new JSONObject();
        try {
            JSONObject user = ApiProcessor.getUserByKey(context.param("apiKey"));
            final JSONObject filteredUserProfile = new JSONObject();
            filteredUserProfile.put(User.USER_NAME, user.optString(User.USER_NAME));
            filteredUserProfile.put(UserExt.USER_ONLINE_FLAG, user.optBoolean(UserExt.USER_ONLINE_FLAG));
            filteredUserProfile.put(UserExt.ONLINE_MINUTE, user.optInt(UserExt.ONLINE_MINUTE));
            filteredUserProfile.put(User.USER_URL, user.optString(User.USER_URL));
            filteredUserProfile.put(UserExt.USER_NICKNAME, user.optString(UserExt.USER_NICKNAME));
            filteredUserProfile.put(UserExt.USER_CITY, user.optString(UserExt.USER_CITY));
            filteredUserProfile.put(UserExt.USER_AVATAR_URL, user.optString(UserExt.USER_AVATAR_URL));
            filteredUserProfile.put(UserExt.USER_POINT, user.optInt(UserExt.USER_POINT));
            filteredUserProfile.put(UserExt.USER_INTRO, user.optString(UserExt.USER_INTRO));
            filteredUserProfile.put(Keys.OBJECT_ID, user.optString(Keys.OBJECT_ID));
            filteredUserProfile.put(UserExt.USER_NO, user.optString(UserExt.USER_NO));
            filteredUserProfile.put(UserExt.USER_APP_ROLE, user.optString(UserExt.USER_APP_ROLE));
            final String userId = user.optString(Keys.OBJECT_ID);
            final long followerCnt = followQueryService.getFollowerCount(userId, Follow.FOLLOWING_TYPE_C_USER);
            filteredUserProfile.put("followerCount", followerCnt);
            final long followingUserCnt = followQueryService.getFollowingCount(userId, Follow.FOLLOWING_TYPE_C_USER);
            filteredUserProfile.put("followingUserCount", followingUserCnt);
            final String userRoleId = user.optString(User.USER_ROLE);
            final JSONObject role = roleQueryService.getRole(userRoleId);
            final String roleName = role.optString(Role.ROLE_NAME);
            filteredUserProfile.put(User.USER_ROLE, roleName);
            // 获取用户个性化设定
            final JSONObject systemSettings = systemSettingsService.getByUsrId(user.optString(Keys.OBJECT_ID));
            if (Objects.isNull(systemSettings)) {
                filteredUserProfile.put("cardBg", "");
            } else {
                final String settingsJson = systemSettings.optString(SystemSettings.SETTINGS);
                final JSONObject settings = new JSONObject(settingsJson);
                final String cardBg = settings.optString("cardBg");
                if (StringUtils.isBlank(cardBg)) {
                    filteredUserProfile.put("cardBg", "");
                } else {
                    filteredUserProfile.put("cardBg", cardBg);
                }
            }
            ret.put(Keys.CODE, StatusCodes.SUCC);
            ret.put(Keys.MSG, "");
            ret.put(Keys.DATA, filteredUserProfile);
            context.renderJSON(ret);
        } catch (Exception e) {
            ret.put(Keys.CODE, StatusCodes.ERR);
            ret.put(Keys.MSG, "Invalid Api Key.");
            context.renderJSON(ret);
        }
    }

    public static JSONObject getUserByKey(String apiKey) throws NullPointerException {
        if (apiKey != null) {
            if (ApiProcessor.keys.containsKey(apiKey)) {
                return ApiProcessor.keys.get(apiKey);
            }
        }
        throw new NullPointerException();
    }

    public void userExists(final RequestContext context) {
        String user = context.pathVar("user");
        JSONObject userJSON = userQueryService.getUserByName(user);
        if (userJSON == null) {
            context.renderJSON(StatusCodes.ERR);
            return;
        }
        context.renderJSON(StatusCodes.SUCC);
    }

}
