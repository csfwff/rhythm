package org.b3log.symphony.util;

import org.json.JSONObject;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.LongSupplier;

/** Shared claim transition. The caller owns the transaction and the account-pair transfer lock. */
public final class LivenessRewardClaim {
    private LivenessRewardClaim() { }
    public record Period(String date, long start, long end) {
        public static Period at(final long now, final ZoneId zone) {
            final LocalDate today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate();
            return new Period(today.minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE),
                    today.atStartOfDay(zone).toInstant().toEpochMilli(), today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli());
        }
    }
    public interface Store {
        void lockOwner() throws Exception;
        boolean alreadyClaimed(Period period) throws Exception;
        int amount(Period period) throws Exception;
        String transfer(int amount, long now) throws Exception;
    }
    public record Result(String date, int amount, String transferId) {
        public JSONObject dto() { return new JSONObject().put("date", date).put("amount", amount).put("claimed", true); }
    }
    public static final class Unavailable extends RuntimeException { }
    public static Result claim(final Store store, final long now, final ZoneId zone) throws Exception {
        return claim(store, () -> now, zone);
    }
    public static Result claim(final Store store, final LongSupplier clock, final ZoneId zone) throws Exception {
        store.lockOwner();
        final long now = clock.getAsLong();
        final Period period = Period.at(now, zone);
        if (store.alreadyClaimed(period)) throw new Unavailable();
        final int amount = store.amount(period);
        if (amount <= 0) throw new Unavailable();
        final String transfer = store.transfer(amount, now);
        if (null == transfer || transfer.isEmpty()) throw new IllegalStateException("liveness reward transfer failed");
        return new Result(period.date(), amount, transfer);
    }
    /** Table names come only from repositories; values remain bound by the caller. */
    public static String claimedSql(final String table, final boolean lock) {
        return "SELECT oId FROM " + table + " WHERE fromId=? AND toId=? AND type=? AND time>=? AND time<?"
                + " ORDER BY time DESC,oId DESC LIMIT 1" + (lock ? " FOR UPDATE" : "");
    }
    public static String livenessSql(final String table, final boolean lock) {
        return "SELECT * FROM " + table + " WHERE livenessUserId=? AND livenessDate=? LIMIT 1" + (lock ? " FOR UPDATE" : "");
    }
}
