package org.b3log.symphony.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.symphony.util.SettingsLocaleOptions;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.util.ApiV3CommunityDto;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class ApiV3SettingsMgmtService {
    @Inject private UserSettingsMgmtService settings;
    @Inject private ApiV3IdempotencyMgmtService idempotency;
    @Inject private SkinQueryService skins;
    public JSONObject read(final String actor, final String group) throws Exception { return settings.read(actor, group); }
    public JSONObject update(final String actor, final String group, final JSONObject changes, final String key) throws Exception {
        try {
            return idempotency.execute(actor, "settings." + group, key, changes,
                    () -> settings.updateInCurrentTransaction(actor, group, changes, true));
        } finally { settings.refresh(actor); }
    }
    public JSONObject localeOptions() {
        return SettingsLocaleOptions.dto();
    }

    public JSONObject skins() {
        final JSONArray list = new JSONArray();
        for (final String device : new String[]{SkinQueryService.DEVICE_PC, SkinQueryService.DEVICE_MOBILE}) {
            for (final JSONObject skin : skins.getSkins(device)) {
                if (skin.optString("dirName").length() > 32) continue;
                list.put(new JSONObject().put("id", skin.optString("dirName"))
                    .put("device", device).put("name", skin.optString("name")).put("description", skin.optString("memo"))
                    .put("previewURL", ApiV3CommunityDto.safeUrl(skin.optString("previewUrl"))).put("order", skin.optInt("order")));
            }
        }
        return new JSONObject().put("list", list);
    }
}
