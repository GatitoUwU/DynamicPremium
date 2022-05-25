package me.gatogamer.dynamicpremium.commons.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {
    public Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

    public void tick() {
        cacheMap.forEach((s, cache) -> {
            if(cache.shouldPurge()) {
                cacheMap.remove(s);
            }
        });
    }

    public Cache getOrCreateCache(String username) {
        return cacheMap.computeIfAbsent(username, Cache::new);
    }

    public void invalidate(String username) {
        cacheMap.remove(username);
    }
}