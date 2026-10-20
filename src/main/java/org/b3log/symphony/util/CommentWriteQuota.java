package org.b3log.symphony.util;

import pers.adlered.simplecurrentlimiter.main.SimpleCurrentLimiter;

/** Existing comment publish/edit allowances, shared by all entry points. */
public final class CommentWriteQuota {
    private static final SimpleCurrentLimiter ADD = new SimpleCurrentLimiter(60 * 10, 5);
    private static final SimpleCurrentLimiter UPDATE = new SimpleCurrentLimiter(60 * 30, 3);
    private CommentWriteQuota() { }
    public static synchronized boolean access(final String actor, final boolean adding) {
        return (adding ? ADD : UPDATE).access(actor);
    }
}
