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
package org.b3log.symphony.service;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.*;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.Reward;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.repository.RewardRepository;
import org.b3log.symphony.util.StatusCodes;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Reward query service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.1, Oct 17, 2015
 * @since 1.3.0
 */
@Service
public class RewardQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(RewardQueryService.class);

    /**
     * Reward repository.
     */
    @Inject
    private RewardRepository rewardRepository;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    @Inject
    private AvatarQueryService avatarQueryService;

    /**
     * Get rewarded senders by dataId.
     */
    public void rewardedSenders(final RequestContext context) {
        String articleId = context.pathVar("aId");

        final Query query = new Query();
        final List<Filter> filters = new ArrayList<>();
        filters.add(new PropertyFilter(Reward.DATA_ID, FilterOperator.EQUAL, articleId));
        filters.add(new PropertyFilter(Reward.TYPE, FilterOperator.EQUAL, Reward.TYPE_C_THANK_ARTICLE));

        query.select(Reward.SENDER_ID)
                .setFilter(new CompositeFilter(CompositeFilterOperator.AND, filters));

        try {
            List<JSONObject> list = rewardRepository.getList(query);
            JSONArray array = new JSONArray();
            // 渲染用户信息
            for (JSONObject object : list) {
                JSONObject userObject = new JSONObject();
                try {
                    JSONObject user = userQueryService.getUser(object.optString(Reward.SENDER_ID));
                    userObject.put(User.USER_NAME, user.optString(User.USER_NAME));
                    userObject.put(UserExt.USER_AVATAR_URL, user.optString(UserExt.USER_AVATAR_URL));
                    avatarQueryService.fillUserAvatarURL(userObject);
                } catch (Exception ignored) {
                }
                array.put(userObject);
            }
            context.renderJSON(StatusCodes.SUCC).renderData(array);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "get rewarded senders failed", e);

            context.renderJSON(StatusCodes.ERR);
        }
    }

    /**
     * Gets rewarded count.
     *
     * @param dataId the specified data id
     * @param type   the specified type
     * @return rewarded count
     */
    public long rewardedCount(final String dataId, final int type) {
        final Query query = new Query();
        final List<Filter> filters = new ArrayList<>();
        filters.add(new PropertyFilter(Reward.DATA_ID, FilterOperator.EQUAL, dataId));
        filters.add(new PropertyFilter(Reward.TYPE, FilterOperator.EQUAL, type));

        query.setFilter(new CompositeFilter(CompositeFilterOperator.AND, filters));

        try {
            return rewardRepository.count(query);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Rewarded count failed", e);

            return 0;
        }
    }

    /**
     * Determines the user specified by the given user id has rewarded the data (article/comment/user) or not.
     *
     * @param userId the specified user id
     * @param dataId the specified data id
     * @param type   the specified type
     * @return {@code true} if has rewared
     */
    public boolean isRewarded(final String userId, final String dataId, final int type) {
        final Query query = new Query();
        final List<Filter> filters = new ArrayList<>();
        filters.add(new PropertyFilter(Reward.SENDER_ID, FilterOperator.EQUAL, userId));
        filters.add(new PropertyFilter(Reward.DATA_ID, FilterOperator.EQUAL, dataId));
        filters.add(new PropertyFilter(Reward.TYPE, FilterOperator.EQUAL, type));

        query.setFilter(new CompositeFilter(CompositeFilterOperator.AND, filters));

        try {
            return 0 != rewardRepository.count(query);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Determines reward failed", e);

            return false;
        }
    }
}
