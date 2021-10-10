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

import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.symphony.processor.middleware.CSRFMidware;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.repository.CloudRepository;
import org.b3log.symphony.service.CloudService;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.StatusCodes;
import org.json.JSONObject;

@Singleton
public class CloudProcessor {

    @Inject
    private CloudService cloudService;

    @Inject
    private CloudRepository cloudRepository;

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);
        final CSRFMidware csrfMidware = beanManager.getReference(CSRFMidware.class);

        final CloudProcessor cloudProcessor = beanManager.getReference(CloudProcessor.class);
        Dispatcher.post("/api/cloud/sync", cloudProcessor::sync, loginCheck::handle);
        Dispatcher.post("/api/cloud/get", cloudProcessor::get, loginCheck::handle);
    }

    /**
     * 同步数据
     *
     * @param context
     */
    public void sync(final RequestContext context) {
        try {
            JSONObject user = Sessions.getUser();
            String userName = user.optString(User.USER_NAME);
            String userId = user.optString(Keys.OBJECT_ID);

            JSONObject requestJSONObject = context.requestJSON();
            String gameId = requestJSONObject.optString("gameId");
            String data = requestJSONObject.optString("data");
            cloudService.sync(userId, gameId, data);
            context.renderJSON(StatusCodes.SUCC).renderMsg("摸鱼派账号 " + userName + " - 游戏存档已同步至摸鱼派云服务");
        } catch (Exception e) {
            context.renderJSON(StatusCodes.ERR).renderMsg("将游戏存档同步至摸鱼派失败");
        }
    }

    /**
     * 获取存档
     *
     * @param context
     */
    public void get(final RequestContext context) {
        JSONObject user = Sessions.getUser();
        String userId = user.optString(Keys.OBJECT_ID);
        JSONObject requestJSONObject = context.requestJSON();
        String gameId = requestJSONObject.optString("gameId");
        String data = cloudService.getFromCloud(userId, gameId);
        context.renderJSON(StatusCodes.SUCC).renderData(data);
    }
}
