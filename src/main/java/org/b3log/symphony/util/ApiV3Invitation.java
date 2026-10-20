package org.b3log.symphony.util;

import org.json.JSONObject;
import java.math.BigDecimal;
import java.util.Set;

/** Known-code lookup only; no registration availability or reservation projection. */
public final class ApiV3Invitation {
    private ApiV3Invitation() { }
    public static String code(final JSONObject body) {
        if (body == null || !body.keySet().equals(Set.of("code"))) throw ApiV3WriteInputs.invalid();
        return code(ApiV3WriteInputs.string(body, "code", 16, true));
    }
    public static String code(final String value) {
        if (value == null) throw ApiV3WriteInputs.invalid();
        final String result = value.trim();
        if (!result.matches("[A-Za-z0-9]{16}")) throw ApiV3WriteInputs.invalid();
        return result;
    }
    public static JSONObject status(final JSONObject row) {
        if (!(row.opt("status") instanceof Number number)) throw unavailable();
        final int value;
        try { value = new BigDecimal(number.toString()).intValueExact(); }
        catch (final ArithmeticException | NumberFormatException e) { throw unavailable(); }
        final String state = switch (value) {
            case 0 -> "used";
            case 1 -> "unused";
            case 2 -> "stopped";
            default -> throw unavailable();
        };
        return new JSONObject().put("status", state);
    }
    public static ApiV3Exception unavailable() {
        return new ApiV3Exception(503, "temporarily_unavailable", "邀请码状态暂不可用");
    }
}
