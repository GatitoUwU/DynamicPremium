package me.gatogamer.dynamicpremium.spigot.listeners;

import me.gatogamer.dynamicpremium.spigot.DynamicPremium;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public class ConnectionListener implements Listener {
    @EventHandler
    public void onDisconnection(PlayerQuitEvent event) {
        DynamicPremium.getInstance().getCacheManager().invalidate(event.getPlayer().getName());
    }
}