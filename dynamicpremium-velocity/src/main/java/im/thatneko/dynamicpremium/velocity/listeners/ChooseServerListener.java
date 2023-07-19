package im.thatneko.dynamicpremium.velocity.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import im.thatneko.dynamicpremium.commons.cache.Cache;
import im.thatneko.dynamicpremium.commons.config.Config;
import im.thatneko.dynamicpremium.velocity.DynamicPremium;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@RequiredArgsConstructor
public class ChooseServerListener {
    private final DynamicPremium dynamicPremium;

    @Subscribe(order = PostOrder.FIRST)
    public void onChooseServer(PlayerChooseInitialServerEvent event) {
        Player player = event.getPlayer();
        Cache cache = dynamicPremium.getCacheManager().getOrCreateCache(player.getUsername());
        cache.updateUsage();
        if (!cache.isPremium() && !cache.isGeyserUser()) {
            return;
        }

        String serverName = dynamicPremium.getConfigManager().getSettingsConfig().getString("lobby-server");
        RegisteredServer registeredServer = dynamicPremium.getProxyServer().getServer(serverName).orElse(null);

        if (registeredServer == null) {
            System.out.println("DynamicPremium > The server " + serverName + " doesn't exist.");
        } else {
            System.out.println("DynamicPremium > Routing " + player.getUsername() + " into " + serverName + ".");
            event.setInitialServer(registeredServer);
        }
    }

    @Subscribe
    public void onPreConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();
        Cache cache = dynamicPremium.getCacheManager().getOrCreateCache(player.getUsername());
        cache.updateUsage();
        Config settingsConfig = dynamicPremium.getConfigManager().getSettingsConfig();
        Config messagesConfig = dynamicPremium.getConfigManager().getMessagesConfig();
        if (!cache.isPremium()) {
            List<String> onlyPremium = settingsConfig.getStringList("access-only-if-player-is-premium");
            if (onlyPremium.contains(event.getOriginalServer().getServerInfo().getName())) {
                TextComponent message = LegacyComponentSerializer.legacy('&').deserialize(messagesConfig.getString("access-only-if-player-is-premium-message"));

                if (!cache.isPremium()) {
                    event.setResult(ServerPreConnectEvent.ServerResult.denied());
                    player.sendMessage(message);
                    if (player.getCurrentServer().isEmpty()) {
                        player.disconnect(message); // disconnect because they don't have any server to connect to.
                    }
                }
            }

            return;
        }

        if (!settingsConfig.getStringList("auth-servers").contains(
                event.getOriginalServer().getServerInfo().getName()
        )) {
            return;
        }
        event.setResult(ServerPreConnectEvent.ServerResult.denied());
    }
}