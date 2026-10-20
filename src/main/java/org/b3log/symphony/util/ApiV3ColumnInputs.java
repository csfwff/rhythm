package org.b3log.symphony.util;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** Strict inputs for private column mutations. */
public final class ApiV3ColumnInputs {
    private ApiV3ColumnInputs() { }
    public static String text(final JSONObject body, final String field, final int max, final boolean emptyAllowed) {
        if (!body.has(field)) throw ApiV3WriteInputs.invalid();
        final String value = ApiV3WriteInputs.string(body, field, max, true);
        if ((!emptyAllowed && value.isEmpty()) || value.codePoints().anyMatch(Character::isISOControl)) throw ApiV3WriteInputs.invalid();
        return value;
    }
    public static List<String> order(final JSONObject body) {
        if (!(body.opt("columnIds") instanceof JSONArray array) || array.isEmpty() || array.length() > 200) throw ApiV3WriteInputs.invalid();
        final List<String> ids = new ArrayList<>();
        final HashSet<String> seen = new HashSet<>();
        for (final Object value : array) {
            if (!(value instanceof String id) || !id.matches("[0-9]{1,19}") || !seen.add(id)) throw ApiV3WriteInputs.invalid();
            ids.add(id);
        }
        return ids;
    }
}
