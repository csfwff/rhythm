package org.b3log.symphony.util;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/** Same published content length and HTML stripping rules as the legacy POST. */
public final class ApiV3BreezemoonInputs {
    private ApiV3BreezemoonInputs() { }
    public static String content(final JSONObject body) {
        final String value = ApiV3WriteInputs.string(body, "content", 512, true);
        if (value.isEmpty() || value.codePoints().anyMatch(c -> Character.isISOControl(c) && c != '\n' && c != '\r' && c != '\t')) throw ApiV3WriteInputs.invalid();
        return value;
    }
    /** Legacy PUT keeps source markup; only the rendered HTML may be inserted into a page. */
    public static String editable(final String filtered) {
        return content(new JSONObject().put("content", filtered));
    }
    public static String publishable(final String filtered) {
        if (null == filtered || filtered.trim().isEmpty() || filtered.trim().length() > 512) throw ApiV3WriteInputs.invalid();
        final String value = Jsoup.clean(filtered, Safelist.none()).trim();
        if (value.isEmpty()) throw ApiV3WriteInputs.invalid();
        return value;
    }
}
