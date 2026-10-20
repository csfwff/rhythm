package org.b3log.symphony.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** Independent bounded OAuth admission; does not consume the ordinary Bearer mutation budget. */
public final class ApiV3OAuthRateLimit {
    private static final Cache<String,AtomicInteger> COUNTS=Caffeine.newBuilder().maximumSize(100000).expireAfterWrite(1,TimeUnit.MINUTES).build();
    private ApiV3OAuthRateLimit(){ }
    public static boolean ip(String value){return take("ip:"+value,120);}
    public static boolean actor(String value){return take("actor:"+value,30);}
    private static boolean take(String key,int limit){return COUNTS.get(key,k->new AtomicInteger()).incrementAndGet()<=limit;}
}
