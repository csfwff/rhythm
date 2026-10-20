package org.b3log.symphony.util;

import org.b3log.symphony.model.Milestone;
import org.json.JSONObject;

/** Internal row whitelist; input content policy is checked by the entry point. */
public final class MilestoneSubmission {
    private MilestoneSubmission() { }
    public static JSONObject pending(final String actor, final JSONObject input, final long now) {
        if (null == actor || actor.isBlank()) throw new IllegalArgumentException("milestone author required");
        return new JSONObject().put(Milestone.MILESTONE_TITLE, input.getString(Milestone.MILESTONE_TITLE))
                .put(Milestone.MILESTONE_DATE, input.getLong(Milestone.MILESTONE_DATE))
                .put(Milestone.MILESTONE_END_DATE, input.optLong(Milestone.MILESTONE_END_DATE))
                .put(Milestone.MILESTONE_CONTENT, input.getString(Milestone.MILESTONE_CONTENT))
                .put(Milestone.MILESTONE_MEDIA_URL, input.optString(Milestone.MILESTONE_MEDIA_URL))
                .put(Milestone.MILESTONE_MEDIA_TYPE, input.optString(Milestone.MILESTONE_MEDIA_TYPE))
                .put(Milestone.MILESTONE_MEDIA_CAPTION, input.optString(Milestone.MILESTONE_MEDIA_CAPTION))
                .put(Milestone.MILESTONE_LINK, input.optString(Milestone.MILESTONE_LINK))
                .put(Milestone.MILESTONE_AUTHOR_ID, actor).put(Milestone.MILESTONE_STATUS, Milestone.STATUS_C_PENDING)
                .put(Milestone.MILESTONE_CREATE_TIME, now).put(Milestone.MILESTONE_UPDATE_TIME, now);
    }
}
