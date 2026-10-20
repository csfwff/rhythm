package org.b3log.symphony.util;

import java.time.LocalDate;
import java.util.Objects;

/** Confirmed daily policy: first withdrawal free, then 32 points per previous successful withdrawal. */
public final class ChatMessageRevokeFees {
    public static final int POINT_STEP = 32;
    public record Quote(LocalDate day, int cost, int nextCount, boolean counted) { }
    private ChatMessageRevokeFees() { }
    /** Caller supplies the platform's natural day, and persists nextCount only with a successful removal. */
    public static Quote quote(final LocalDate recordedDay, final int count, final LocalDate today, final boolean privileged) {
        Objects.requireNonNull(today);
        // Existing operators skip the counter and transfer branch entirely.
        if (privileged) return new Quote(today, 0, 0, false);
        if (count < 0 || recordedDay == null && count != 0) throw new IllegalStateException("invalid stored chat withdrawal counter");
        final int used = today.equals(recordedDay) ? count : 0;
        try { return new Quote(today, Math.multiplyExact(used, POINT_STEP), Math.addExact(used, 1), true); }
        catch (final ArithmeticException e) { throw new ApiV3Exception(409, "withdrawal_limit", "今日撤回次数过多"); }
    }
}
