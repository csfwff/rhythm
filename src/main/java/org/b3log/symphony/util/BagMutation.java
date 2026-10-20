package org.b3log.symphony.util;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.TreeSet;

/** Shared in-memory transitions; callers persist the whole result in their current transaction. */
public final class BagMutation {
    public static final String GAME_ID = "sys-bag";
    public static final String REMAIN = "sysCheckinRemain";
    private BagMutation() { }
    public static final class Unavailable extends RuntimeException { }
    public record Adjustment(JSONObject bag, int status, boolean changed) { }
    public static Adjustment adjust(final JSONObject source, final String item, final int number, final int maxTake) {
        if (maxTake < 0) throw new IllegalArgumentException("negative capacity");
        final JSONObject bag = new JSONObject(source.toString());
        final int has = bag.optInt(item);
        if (number == 0 || (number < 0 && (long) has + number < 0)) return new Adjustment(bag, -1, false);
        final long sum = (long) has + number;
        final boolean capped = number > 0 && sum > maxTake;
        bag.put(item, capped ? maxTake : Math.toIntExact(sum));
        return new Adjustment(bag, capped ? 1 : 0, true);
    }
    public static JSONObject useExemption(final JSONObject source, final String item) {
        final int days = switch (item) { case "checkin1day" -> 1; case "checkin2days" -> 2; default -> throw new IllegalArgumentException("unsupported card"); };
        final int cards = source.optInt(item), remain = source.optInt(REMAIN);
        if (cards < 1 || remain < 0 || (long) remain + days > Integer.MAX_VALUE) throw new Unavailable();
        final JSONObject bag = new JSONObject(source.toString());
        bag.put(item, cards - 1).put(REMAIN, remain + days);
        return bag;
    }
    public static JSONObject dto(final JSONObject bag) {
        final JSONArray list = new JSONArray();
        for (final String code : new TreeSet<>(bag.keySet())) {
            if (bag.opt(code) instanceof Number) list.put(new JSONObject().put("code", code).put("count", bag.optLong(code)));
        }
        return new JSONObject().put("list", list);
    }
}
