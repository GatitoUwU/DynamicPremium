package me.gatogamer.dynamicpremium.spigot.tasks;

import lombok.RequiredArgsConstructor;
import me.gatogamer.dynamicpremium.commons.cache.Cache;
import me.gatogamer.dynamicpremium.commons.cache.CacheManager;
import me.gatogamer.dynamicpremium.commons.database.Database;
import me.gatogamer.dynamicpremium.spigot.DynamicPremium;
import me.gatogamer.dynamicpremium.spigot.compat.CompatibilityManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class SessionCheckerTask extends BukkitRunnable {
    private final DynamicPremium dynamicPremium;

    @Override
    public void run() {
        CompatibilityManager compatibilityManager = dynamicPremium.getCompatibilityManager();
        Database database = dynamicPremium.getDatabaseManager().getDatabase();
        CacheManager cacheManager = dynamicPremium.getCacheManager();

        Bukkit.getOnlinePlayers().forEach(player -> {
            Cache cache = cacheManager.getOrCreateCache(player.getName());
            cache.updateUsage();
            if (!cache.isAuthenticated() && !cache.isCheckedIfPremium()) {
                cache.setCheckedIfPremium(true); // Less database usage.
                if (database.playerIsPremium(player.getName())) {
                    compatibilityManager.authPlayer(player);
                    cache.setAuthenticated(true);
                }
            }
        });
    }
}