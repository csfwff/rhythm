package org.b3log.symphony.util;

import org.b3log.symphony.model.Milestone;
import org.json.JSONObject;

/** Owner-only submission view. No complete row, author object or private account metadata. */
public final class ApiV3MilestoneDto {
    private ApiV3MilestoneDto() { }
    public static JSONObject owned(final JSONObject row) {
        final JSONObject dto = ApiV3CommunityDto.milestone(row, "", row.optString(Milestone.MILESTONE_CONTENT));
        dto.remove("contentHtml");
        return dto.put("status", switch (row.optInt(Milestone.MILESTONE_STATUS)) {
                    case Milestone.STATUS_C_PENDING -> "pending";
                    case Milestone.STATUS_C_APPROVED -> "approved";
                    case Milestone.STATUS_C_REJECTED -> "rejected";
                    default -> "unknown";
                }).put("createdAt", row.optLong(Milestone.MILESTONE_CREATE_TIME))
                .put("updatedAt", row.optLong(Milestone.MILESTONE_UPDATE_TIME));
    }
}
