package org.b3log.symphony.util;

import org.b3log.symphony.processor.LoginProcessor;

/** Old reset pages and v3 completion share the same existing attempt budget. */
public final class PasswordResetQuota {
    private PasswordResetQuota(){ }
    public static boolean access(String ip){
        final var limiter=LoginProcessor.resetCodeLimiter;
        synchronized(limiter){return limiter.access(ip);}
    }
}
