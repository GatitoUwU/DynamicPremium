package im.thatneko.dynamicpremium.commons.cache;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@Getter
public class CacheManager {
    private final Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

    public void tick() {
        this.cacheMap.forEach((s, cache) -> {
            if(cache.shouldPurge()) {
                this.cacheMap.remove(s);
            }
        });
    }

    public Cache getOrCreateCache(String username) {
        return this.cacheMap.computeIfAbsent(username, Cache::new);
    }

    public void invalidate(String username) {
        this.cacheMap.remove(username);
    }
}