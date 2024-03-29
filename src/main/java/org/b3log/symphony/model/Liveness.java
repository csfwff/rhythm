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
package org.b3log.symphony.model;

import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;

/**
 * This class defines all liveness model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Jun 12, 2018
 * @since 1.4.0
 */
public final class Liveness {

    /**
     * Liveness.
     */
    public static final String LIVENESS = "liveness";

    /**
     * Key of user id.
     */
    public static final String LIVENESS_USER_ID = "livenessUserId";

    /**
     * Key of liveness date.
     */
    public static final String LIVENESS_DATE = "livenessDate";

    /**
     * Key of liveness point.
     */
    public static final String LIVENESS_POINT = "livenessPoint";

    /**
     * Key of liveness article.
     */
    public static final String LIVENESS_ARTICLE = "livenessArticle";

    /**
     * Key of liveness comment.
     */
    public static final String LIVENESS_COMMENT = "livenessComment";

    /**
     * Key of liveness activity.
     */
    public static final String LIVENESS_ACTIVITY = "livenessActivity";

    /**
     * Key of liveness thank.
     */
    public static final String LIVENESS_THANK = "livenessThank";

    /**
     * Key of liveness vote.
     */
    public static final String LIVENESS_VOTE = "livenessVote";

    /**
     * Key of liveness reward.
     */
    public static final String LIVENESS_REWARD = "livenessReward";

    /**
     * Key of liveness PV.
     */
    public static final String LIVENESS_PV = "livenessPV";

    /**
     * Key of liveness accept answer.
     */
    public static final String LIVENESS_ACCEPT_ANSWER = "livenessAcceptAnswer";

    /**
     * Calculates point of the specified liveness.
     *
     * @param liveness the specified liveness
     * @return point
     */
    public static int calcPoint(final JSONObject liveness) {
        final float activityPer = Symphonys.ACTIVITY_YESTERDAY_REWARD_ACTIVITY_PER;
        final float articlePer = Symphonys.ACTIVITY_YESTERDAY_REWARD_ARTICLE_PER;
        final float commentPer = Symphonys.ACTIVITY_YESTERDAY_REWARD_COMMENT_PER;
        final float pvPer = Symphonys.ACTIVITY_YESTERDAY_REWARD_PV_PER;
        final float rewardPer = Symphonys.ACTIVITY_YESTERDAY_REWARD_REWARD_PER;
        final float thankPer = Symphonys.ACTIVITY_YESTERDAY_REWARD_THANK_PER;
        final float votePer = Symphonys.ACTIVITY_YESTERDAY_REWARD_VOTE_PER;
        final float acceptAnswerPer = Symphonys.ACTIVITY_YESTERDAY_REWARD_ACCEPT_ANSWER_PER;

        final int activity = liveness.optInt(Liveness.LIVENESS_ACTIVITY);
        int article = liveness.optInt(Liveness.LIVENESS_ARTICLE);
        if (article > 1) {
            article = 1;
        }
        int comment = liveness.optInt(Liveness.LIVENESS_COMMENT);
        if (comment > 5) {
            comment = 5;
        }
        int pv = liveness.optInt(Liveness.LIVENESS_PV);
        if (pv > 20) {
            pv = 20;
        }
        final int reward = liveness.optInt(Liveness.LIVENESS_REWARD);
        final int thank = liveness.optInt(Liveness.LIVENESS_THANK);
        int vote = liveness.optInt(Liveness.LIVENESS_VOTE);
        if (vote > 2) {
            vote = 2;
        }
        int acceptAnswer = liveness.optInt(Liveness.LIVENESS_ACCEPT_ANSWER);
        if (acceptAnswer > 1) {
            acceptAnswer = 1;
        }

        final int activityPoint = (int) (activity * activityPer);
        final int articlePoint = (int) (article * articlePer);
        final int commentPoint = (int) (comment * commentPer);
        final int pvPoint = (int) (pv * pvPer);
        final int rewardPoint = (int) (reward * rewardPer);
        final int thankPoint = (int) (thank * thankPer);
        final int votePoint = (int) (vote * votePer);
        final int acceptAnswerPoint = (int) (acceptAnswer * acceptAnswerPer);

        int ret = activityPoint + articlePoint + commentPoint + pvPoint + rewardPoint + thankPoint + votePoint + acceptAnswerPoint;

        final int max = Symphonys.ACTIVITY_YESTERDAY_REWARD_MAX;
        if (ret > max) {
            ret = max;
        }

        return ret;
    }

    /**
     * Private constructor.
     */
    private Liveness() {
    }
}
