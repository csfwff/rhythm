package org.b3log.symphony.util;

import pers.adlered.simplecurrentlimiter.main.SimpleCurrentLimiter;

/** Existing article publish/edit allowance shared by every entry point. Role exemptions stay with callers. */
public final class ArticleWriteQuota {
    private static final SimpleCurrentLimiter LIMITER = new SimpleCurrentLimiter(60 * 30, 10);
    private ArticleWriteQuota() { }
    public static synchronized boolean access(final String actor) { return LIMITER.access(actor); }
}
