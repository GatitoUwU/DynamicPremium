package me.gatogamer.dynamicpremium.bungee.listeners;

import com.google.common.base.Charsets;
import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import me.gatogamer.dynamicpremium.bungee.config.ConfigUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class Listeners implements Listener {

    @EventHandler(priority = -128)
    public void onPreLoginEvent(PreLoginEvent e) {
        Configuration configuration = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
        if (e.isCancelled()) {
            return;
        }
        PendingConnection pendingConnection = e.getConnection();
        DynamicPremium plugin = DynamicPremium.getInstance();
        e.registerIntent(plugin);
        UUID offlineuuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + pendingConnection.getName()).getBytes(Charsets.UTF_8));

        if (plugin.getDatabaseManager().getDatabase().playerIsPremium(pendingConnection.getName())) {
            e.completeIntent(plugin);
            //.connect(ProxyServer.getInstance().getServerInfo(DynamicPremium.getInstance().getConfigUtils().getConfig(DynamicPremium.getInstance(), "Settings").getString("LobbyServer")));
            pendingConnection.setOnlineMode(true);
            if (configuration.getString("UUIDMode").equalsIgnoreCase("NO_PREMIUM")) {
                pendingConnection.setUniqueId(offlineuuid);
            }
        } else {
            e.setCancelled(false);
            e.completeIntent(plugin);
            pendingConnection.setOnlineMode(false);
            pendingConnection.setUniqueId(offlineuuid);
        }
    }

    @EventHandler
    public void onServerChangeEvent(ServerConnectEvent e) {
        Configuration settings = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
        ProxiedPlayer proxiedPlayer = e.getPlayer();
        DynamicPremium plugin = DynamicPremium.getInstance();
        if (plugin.getDatabaseManager().getDatabase().playerIsPremium(proxiedPlayer.getName())) {
            if (settings.getString("LoginType").equals("DIRECT")) {
                ServerInfo newTarget = ProxyServer.getInstance().getServerInfo(settings.getString("LobbyServer"));
                if (settings.getStringList("AuthServers").contains(e.getTarget().getName())) {
                    e.setTarget(newTarget);
                }
            }
            if (settings.getStringList("AuthServers").contains(e.getTarget().getName())) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(stream);
                try {
                    out.writeUTF(e.getPlayer().getName());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                ProxyServer.getInstance().getScheduler().runAsync(DynamicPremium.getInstance(), () -> {
                    try {
                        sleep(1000L);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("Sending logging to " + proxiedPlayer.getName() + ", he's in the server " + e.getTarget());
                    e.getTarget().sendData("DynamicPremium-Auth", stream.toByteArray());
                });
            }
        }
    }

    @EventHandler
    public void onPostLoginEvent(PostLoginEvent e) {
        DynamicPremium plugin = DynamicPremium.getInstance();
        if (plugin.getDatabaseManager().getDatabase().playerIsPremium(e.getPlayer().getName())) {
            Configuration configuration = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
            DynamicPremium.getInstance().getProxy().getScheduler().runAsync(DynamicPremium.getInstance(), () -> {
                try {
                    sleep(10L);
                    e.getPlayer().connect(ProxyServer.getInstance().getServerInfo(ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings").getString("LobbyServer")));
                    e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', configuration.getString("SendLobbyMessage")));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    @EventHandler
    public void onCommands(ChatEvent e) {
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        Configuration configuration = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
        if (configuration.getStringList("AuthServers").contains(p.getServer().getInfo().getName())) {
            String[] messages = e.getMessage().split(" ");
            if (!configuration.getStringList("AllowedCommands").contains(messages[0])) {
                e.setCancelled(true);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', configuration.getString("DeniedCommand")));
            }
        }
    }
}
