package im.thatneko.dynamicpremium.bungee.listeners;

import im.thatneko.dynamicpremium.bungee.DynamicPremium;
import im.thatneko.dynamicpremium.commons.cache.Cache;
import im.thatneko.dynamicpremium.commons.config.Config;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@RequiredArgsConstructor
public class ChooseServerListener implements Listener {
    private final DynamicPremium dynamicPremium;

    @EventHandler
    public void onPreConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Cache cache = dynamicPremium.getCacheManager().getOrCreateCache(player.getName());
        cache.updateUsage();
        Config settingsConfig = this.dynamicPremium.getConfigManager().getSettingsConfig();
        Config messagesConfig = this.dynamicPremium.getConfigManager().getMessagesConfig();
        if (!cache.isPremium()) {
            List<String> onlyPremium = settingsConfig.getStringList("access-only-if-player-is-premium");
            if (onlyPremium.contains(event.getTarget().getName().toLowerCase())) {
                String message = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("access-only-if-player-is-premium-message"));

                if (!cache.isPremium()) {
                    event.setCancelled(true);
                    player.sendMessage(message);
                    if (player.getServer() == null) {
                        player.disconnect(message); // disconnect because they don't have any server to connect to.
                    }
                }
            }
            return;
        }

        List<String> authServers = settingsConfig.getStringList("auth-servers");
        if (!authServers.contains(event.getTarget().getName())) {
            return;
        }
        event.setTarget(ProxyServer.getInstance().getServerInfo(settingsConfig.getString("lobby-server")));
    }
}