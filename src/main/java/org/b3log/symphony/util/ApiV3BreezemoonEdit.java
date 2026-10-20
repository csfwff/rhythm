package org.b3log.symphony.util;

import org.b3log.symphony.model.Breezemoon;
import org.json.JSONObject;

/** Owned edits preserve author, creation time, city and moderation state. */
public final class ApiV3BreezemoonEdit {
    private ApiV3BreezemoonEdit() { }
    public static JSONObject apply(final JSONObject old, final String content, final String ip, final String ua,
                                   final long now, final int maxUa) {
        final String agent = null == ua ? "" : ua;
        return new JSONObject(old.toString()).put(Breezemoon.BREEZEMOON_CONTENT, content)
                .put(Breezemoon.BREEZEMOON_IP, ip).put(Breezemoon.BREEZEMOON_UA, agent.substring(0, Math.min(agent.length(), maxUa)))
                .put(Breezemoon.BREEZEMOON_UPDATED, now);
    }
}
