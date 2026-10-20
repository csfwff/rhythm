package org.b3log.symphony.util;

import org.jsoup.Jsoup;
import org.json.JSONObject;

/** Fixed medal response fields. Raw styling JSON, grant data and grant identifiers never escape. */
public final class ApiV3MedalDto {
    private ApiV3MedalDto() { }
    private static String text(final String value, final int limit) {
        if (value.length() > limit || value.codePoints().anyMatch(c -> Character.isISOControl(c) && c != '\n' && c != '\r' && c != '\t')) return "";
        return Jsoup.parse(value).text();
    }
    public static JSONObject project(final JSONObject row, final boolean owner) {
        String description = row.optString("medal_description");
        final String data = row.optString("data");
        if (description.length() > 1024 || data.length() > 128) description = "";
        else {
            final String[] vars = data.split(";", -1);
            if (!data.isEmpty()) for (int i = 0; i < vars.length; i++) {
                description = description.replace("{var" + (i + 1) + "}", vars[i]);
                if (description.length() > 1024) { description = ""; break; }
            }
        }
        final JSONObject dto = new JSONObject().put("id", text(row.optString("medal_id"), 128))
                .put("name", text(row.optString("medal_name"), 128)).put("type", text(row.optString("medal_type"), 128))
                .put("description", text(description, 1024)).put("order", row.optInt("display_order"))
                .put("expiresAt", row.optLong("expire_time"));
        if (owner) {
            final Object displayed = row.opt("display");
            dto.put("displayed", Boolean.TRUE.equals(displayed) || displayed instanceof Number number && number.intValue() == 1
                    || "true".equalsIgnoreCase(String.valueOf(displayed)) || "1".equals(String.valueOf(displayed)));
        }
        return dto;
    }
}
