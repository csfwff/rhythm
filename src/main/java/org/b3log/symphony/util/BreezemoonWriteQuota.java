package org.b3log.symphony.util;

import pers.adlered.simplecurrentlimiter.main.SimpleCurrentLimiter;

public final class BreezemoonWriteQuota {
    private static final SimpleCurrentLimiter LIMITER = new SimpleCurrentLimiter(60 * 60, 5);
    private BreezemoonWriteQuota() { }
    public static synchronized boolean access(final String userId) { return LIMITER.access(userId); }
}
