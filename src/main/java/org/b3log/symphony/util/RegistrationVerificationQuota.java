package org.b3log.symphony.util;

import org.b3log.symphony.processor.LoginProcessor;

/** Verification pages and old/new activation share the existing attempt budget. */
public final class RegistrationVerificationQuota {
    private RegistrationVerificationQuota(){ }
    public static boolean access(String ip){
        final var limiter=LoginProcessor.verifyCodeLimiter;
        synchronized(limiter){return limiter.access(ip);}
    }
}
