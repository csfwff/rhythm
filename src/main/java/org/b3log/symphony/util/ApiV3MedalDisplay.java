package org.b3log.symphony.util;

import org.json.JSONObject;
import java.util.Set;

/** Display is an explicit boolean setting; medal IDs remain exact bounded definition keys, including "0". */
public final class ApiV3MedalDisplay {
    private ApiV3MedalDisplay() { }
    public static String medalId(final String id) {
        if (id == null || id.isBlank() || id.length() > 128 || id.codePoints().anyMatch(Character::isISOControl)) throw ApiV3WriteInputs.invalid();
        try { ApiV3Json.canonical(id); }
        catch (final IllegalArgumentException e) { throw ApiV3WriteInputs.invalid(); }
        return id;
    }
    public static boolean displayed(final JSONObject body) {
        if (body == null || !body.keySet().equals(Set.of("displayed")) || !(body.opt("displayed") instanceof Boolean value)) throw ApiV3WriteInputs.invalid();
        return value;
    }
    public static boolean stored(final Object value) {
        if (value instanceof Boolean flag) return flag;
        if (value instanceof Number) {
            final long number = expiry(value);
            if (number == 0 || number == 1) return number == 1;
            throw inconsistent();
        }
        if ("true".equalsIgnoreCase(String.valueOf(value)) || "1".equals(value)) return true;
        if ("false".equalsIgnoreCase(String.valueOf(value)) || "0".equals(value)) return false;
        throw inconsistent();
    }
    public static long expiry(final Object value) {
        if (!(value instanceof Number)) throw inconsistent();
        try { return new java.math.BigDecimal(value.toString()).longValueExact(); }
        catch (final ArithmeticException | NumberFormatException e) { throw inconsistent(); }
    }
    public static ApiV3Exception inconsistent() { return new ApiV3Exception(409, "inconsistent_state", "勋章状态暂不可修改"); }
}
