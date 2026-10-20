package org.b3log.symphony.util;

import pers.adlered.simplecurrentlimiter.main.SimpleCurrentLimiter;

/** Shared business quotas across the legacy and v3 entry points. */
public final class FishGameWriteQuota {
    private static final SimpleCurrentLimiter SUBMISSION = new SimpleCurrentLimiter(60 * 30, 10);
    private static final SimpleCurrentLimiter COMMENT = new SimpleCurrentLimiter(60 * 10, 5);
    private static final SimpleCurrentLimiter VOTE = new SimpleCurrentLimiter(60, 10);
    private FishGameWriteQuota() { }
    public enum Kind { SUBMISSION, COMMENT, VOTE }
    public static boolean access(final Kind kind, final String actor) {
        final SimpleCurrentLimiter limiter = switch (kind) { case SUBMISSION -> SUBMISSION; case COMMENT -> COMMENT; case VOTE -> VOTE; };
        synchronized (limiter) { return limiter.access(actor); }
    }
}
