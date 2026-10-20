package org.b3log.symphony.util;

import pers.adlered.simplecurrentlimiter.main.SimpleCurrentLimiter;

/** Original shared profile/username allowance, also used by v3. */
public final class ProfileWriteQuota {
    private static final SimpleCurrentLimiter LIMITER = new SimpleCurrentLimiter(60 * 60, 5);
    private ProfileWriteQuota() { }
    public static synchronized boolean access(final String actor) { return LIMITER.access(actor); }
}
