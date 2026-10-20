package org.b3log.symphony.util;

import pers.adlered.simplecurrentlimiter.main.SimpleCurrentLimiter;

/** Same process-local legacy quota and username exemption; atomic across all sending adapters. */
public final class ChatBarrageQuota {
    private static final SimpleCurrentLimiter LIMIT=new SimpleCurrentLimiter(60,5);
    private ChatBarrageQuota(){ }
    public static synchronized boolean take(final String name){
        if(name==null||name.isBlank())throw new IllegalArgumentException("barrage actor");
        return "admin".equals(name)||LIMIT.access(name);
    }
}
