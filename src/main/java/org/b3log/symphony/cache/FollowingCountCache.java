package org.b3log.symphony.cache;

import org.b3log.latke.ioc.Singleton;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Singleton
public class FollowingCountCache {

    public static final Map<String, JSONObject> COUNT_CACHE = Collections.synchronizedMap(new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 2000;
        }
    });
}
