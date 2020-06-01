package me.gatogamer.dynamicpremium.cache;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    public static Map<ProxiedPlayer, Cache> cacheMap = new HashMap<>();

    public static Cache getCacheOrGetNew(ProxiedPlayer proxiedPlayer) {
        if (cacheMap.containsKey(proxiedPlayer)) {
            return cacheMap.get(proxiedPlayer);
        } else {
            Cache cache = new Cache();
            cacheMap.put(proxiedPlayer, cache);
            return cache;
        }
    }

}
