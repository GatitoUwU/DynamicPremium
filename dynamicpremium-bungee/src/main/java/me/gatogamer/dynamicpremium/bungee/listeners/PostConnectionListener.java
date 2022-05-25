package me.gatogamer.dynamicpremium.bungee.listeners;

import lombok.RequiredArgsConstructor;
import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import me.gatogamer.dynamicpremium.bungee.api.event.NekoConnectionFinishedEvent;
import me.gatogamer.dynamicpremium.commons.cache.Cache;
import me.gatogamer.dynamicpremium.commons.utils.NyaUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class PostConnectionListener implements Listener {

    private final DynamicPremium dynamicPremium;

    @EventHandler
    public void onPostLoginEvent(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Cache cache = dynamicPremium.getCacheManager().getOrCreateCache(player.getName());
        cache.updateUsage();

        Configuration mainSettings = dynamicPremium.getMainSettings();
        if (cache.isOnVerification()) {
            cache.setOnVerification(false);
            cache.setPendingVerification(false);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("Alert.Enabled")));
            NyaUtils.run(() -> dynamicPremium.getDatabaseManager().getDatabase().addPlayer(player.getName()));
        }
        NekoConnectionFinishedEvent nekoConnectionFinishedEvent = new NekoConnectionFinishedEvent(
                player, dynamicPremium.getLobbySelector().getForcedHostFor(player), cache.isPremium()
        );

        ProxyServer.getInstance().getPluginManager().callEvent(nekoConnectionFinishedEvent);

        if (cache.isPremium()) {
            if (!mainSettings.getString("LoginType").equals("DIRECT")) { // Fixes xd
                dynamicPremium.getProxy().getScheduler().schedule(dynamicPremium, () -> {
                    ServerInfo newTarget = dynamicPremium.getLobbySelector().getLobby(mainSettings, player);
                    player.connect(newTarget);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("SendLobbyMessage")));
                }, 500L, TimeUnit.MILLISECONDS);
            }
        }
    }
}