package me.gatogamer.dynamicpremium.bungee.listeners;

import com.google.common.base.Charsets;
import lombok.Getter;
import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import me.gatogamer.dynamicpremium.bungee.utils.Utils;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

@Getter
public class ConnectionListener implements Listener {
    @EventHandler(priority = -64)
    public void onLogin(LoginEvent e) {
        if (e.getConnection() == null || !e.getConnection().isConnected()) {
            return;
        }
        Configuration configuration = DynamicPremium.getInstance().getMainSettings();
        if (configuration.getString("UUIDMode").equalsIgnoreCase("NO_PREMIUM")) {
            PendingConnection pendingConnection = e.getConnection();
            UUID offlineUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + pendingConnection.getName()).getBytes(Charsets.UTF_8));
            Utils.setUuid(pendingConnection, offlineUuid);
        }
    }
}