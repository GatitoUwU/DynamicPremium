package me.gatogamer.dynamicpremium.shared.cache;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    public static Map<String, Cache> cacheMap = new HashMap<>();

    public static Cache getCacheOrGetNew(String username) {
        if (cacheMap.containsKey(username)) {
            return cacheMap.get(username);
        } else {
            Cache cache = new Cache();
            cacheMap.put(username, cache);
            return cache;
        }
    }

}
