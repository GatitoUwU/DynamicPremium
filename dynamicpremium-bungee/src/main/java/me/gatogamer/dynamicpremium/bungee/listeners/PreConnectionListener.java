package me.gatogamer.dynamicpremium.bungee.listeners;

import com.google.common.base.Charsets;
import lombok.RequiredArgsConstructor;
import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import me.gatogamer.dynamicpremium.bungee.api.event.NekoConnectionPreLoginEvent;
import me.gatogamer.dynamicpremium.bungee.utils.Utils;
import me.gatogamer.dynamicpremium.commons.cache.Cache;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class PreConnectionListener implements Listener {
    private final DynamicPremium dynamicPremium;
    private final Pattern allowedNickCharacters = Pattern.compile("[a-zA-Z0-9_]*");

    @EventHandler(priority = -64)
    public void onPreLoginEvent(PreLoginEvent event) {
        if (event.getConnection() == null || event.isCancelled() || !event.getConnection().isConnected() || event.getConnection().getName() == null) {
            event.setCancelReason("Error while processing your connection.");
            event.setCancelled(true);
            return;
        }
        PendingConnection pendingConnection = event.getConnection();
        UUID offlineUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + pendingConnection.getName()).getBytes(Charsets.UTF_8));
        Cache cache = dynamicPremium.getCacheManager().getOrCreateCache(event.getConnection().getName());
        cache.updateUsage();

        if (!allowedNickCharacters.matcher(pendingConnection.getName()).matches()) {
            event.setCancelReason("Invalid nickname detected.");
            event.setCancelled(true);
            return;
        }

        if (cache.isOnVerification()) {
            cache.setPendingVerification(false);
            cache.setOnVerification(false);
            cache.setPremium(false);
        }

        Configuration configuration = DynamicPremium.getInstance().getMainSettings();

        if (cache.isPendingVerification()) {
            pendingConnection.setOnlineMode(true);
            cache.setOnVerification(true);
            cache.setPremium(true);
            if (configuration.getString("UUIDMode").equalsIgnoreCase("NO_PREMIUM")) {
                Utils.setUuid(pendingConnection, offlineUuid);
            }
            return;
        }

        event.registerIntent(dynamicPremium);

        try {
            dynamicPremium.getProxy().getScheduler().runAsync(dynamicPremium, () -> {
                if (dynamicPremium.getDatabaseManager().getDatabase().playerIsPremium(pendingConnection.getName())) {
                    pendingConnection.setOnlineMode(true);
                    if (configuration.getString("UUIDMode").equalsIgnoreCase("NO_PREMIUM")) {
                        Utils.setUuid(pendingConnection, offlineUuid);
                    }
                    cache.setPremium(true);
                    ProxyServer.getInstance().getPluginManager().callEvent(new NekoConnectionPreLoginEvent(event));
                } else {
                    pendingConnection.setOnlineMode(false);
                    Utils.setUuid(pendingConnection, offlineUuid);
                    cache.setPremium(false);
                }
                event.completeIntent(dynamicPremium);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            event.completeIntent(dynamicPremium);
            event.setCancelled(true);
            event.setCancelReason("Error");
        }
    }
}