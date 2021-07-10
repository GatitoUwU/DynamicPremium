package me.gatogamer.dynamicpremium.spigot.tasks;

import me.gatogamer.dynamicpremium.shared.cache.Cache;
import me.gatogamer.dynamicpremium.shared.cache.CacheManager;
import me.gatogamer.dynamicpremium.shared.database.Database;
import me.gatogamer.dynamicpremium.spigot.DynamicPremium;
import me.gatogamer.dynamicpremium.spigot.compat.Compatibility;
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
public class SessionCheckerTask extends BukkitRunnable {
    @Override
    public void run() {

        CompatibilityManager compatibilityManager = DynamicPremium.getInstance().getCompatibilityManager();
        Database database = DynamicPremium.getInstance().getDatabaseManager().getDatabase();

        Bukkit.getOnlinePlayers().forEach(player -> {
            Cache cache = CacheManager.getCacheOrGetNew(player.getName());
            if (!cache.isAuthenticated()) {
                if (database.playerIsPremium(player.getName())) {
                    compatibilityManager.authPlayer(player);
                    cache.setAuthenticated(true);
                }
            }
        });
    }
}
