package org.b3log.symphony.util;

import org.b3log.latke.Keys;
import org.b3log.symphony.model.Notification;
import org.json.JSONObject;

/** Raw notification references/owner/content are never copied into a response. */
public final class ApiV3NotificationDto {
    private ApiV3NotificationDto() { }
    public static JSONObject base(final JSONObject row) {
        final int type = row.optInt(Notification.NOTIFICATION_DATA_TYPE);
        return new JSONObject().put("id", row.optString(Keys.OBJECT_ID)).put("type", type)
                .put("category", ApiV3NotificationTypes.category(type)).put("title", ApiV3NotificationTypes.label(type))
                .put("hasRead", row.optBoolean(Notification.NOTIFICATION_HAS_READ)).put("createdAt", row.optLong(Keys.OBJECT_ID))
                .put("descriptionHtml", "").put("description", "").put("url", "").put("contentAvailable", true);
    }
    public static JSONObject unavailable(final JSONObject dto) {
        dto.remove("article"); dto.remove("comment"); dto.remove("actor");
        return dto.put("contentAvailable", false).put("url", "").put("descriptionHtml", "相关内容不存在或不可访问")
                .put("description", "相关内容不存在或不可访问");
    }
}
