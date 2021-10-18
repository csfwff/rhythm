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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.CSRFMidware;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.processor.middleware.validate.Activity1A0001CollectValidationMidware;
import org.b3log.symphony.processor.middleware.validate.Activity1A0001ValidationMidware;
import org.b3log.symphony.service.RewardQueryService;
import org.b3log.symphony.service.UserQueryService;
import org.b3log.symphony.util.StatusCodes;
import org.json.JSONObject;

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
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);
        final CSRFMidware csrfMidware = beanManager.getReference(CSRFMidware.class);

        final ApiProcessor apiProcessor = beanManager.getReference(ApiProcessor.class);
        Dispatcher.get("/api/user/exists/{user}", apiProcessor::userExists);

        final RewardQueryService rewardQueryService = beanManager.getReference(RewardQueryService.class);
        Dispatcher.get("/api/article/reward/senders/{aId}", rewardQueryService::rewardedSenders);
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
