package org.b3log.symphony.util;

import org.b3log.latke.util.TimeZones;
import org.json.JSONArray;
import org.json.JSONObject;

/** The same language/timezone catalog used by the classic settings page. */
public final class SettingsLocaleOptions {
    private SettingsLocaleOptions() { }
    public static JSONObject dto() {
        final JSONArray timezones = new JSONArray();
        for (final TimeZones.TimeZoneWithDisplayNames zone : TimeZones.getInstance().getTimeZones()) {
            timezones.put(new JSONObject().put("id", zone.getTimeZone().getID()).put("name", zone.getDisplayName()));
        }
        return new JSONObject().put("languages", Languages.getAvailableLanguages()).put("timezones", timezones);
    }
}
