package me.gatogamer.dynamicpremium.bungee.listeners;

import com.google.common.base.Charsets;
import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import me.gatogamer.dynamicpremium.bungee.config.ConfigUtils;
import me.gatogamer.dynamicpremium.shared.cache.CacheManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class Listeners implements Listener {

    public HashMap<String, Boolean> checking = new HashMap<>();
    public List<String> working = new ArrayList<>();

    @EventHandler(priority = -64)
    public void onLogin(LoginEvent e) {
        if (e.getConnection() == null || !e.getConnection().isConnected()) {
            return;
        }
        if (e.isCancelled()) {
            System.out.println("Cancel reason of player " + e.getConnection().getName() + " is " + e.getCancelReason());
            return;
        }
        Configuration configuration = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
        if (configuration.getString("UUIDMode").equalsIgnoreCase("NO_PREMIUM")) {
            PendingConnection pendingConnection = e.getConnection();
            UUID offlineuuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + pendingConnection.getName()).getBytes(Charsets.UTF_8));
            setUuid(pendingConnection, offlineuuid);
        }
    }

    @EventHandler(priority = -64)
    public void onPreLoginEvent(PreLoginEvent e) {
        Configuration configuration = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
        if (e.getConnection() == null || e.isCancelled() || !e.getConnection().isConnected()) {
            return;
        }
        DynamicPremium plugin = DynamicPremium.getInstance();
        e.registerIntent(plugin);
        PendingConnection pendingConnection = e.getConnection();
        UUID offlineuuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + pendingConnection.getName()).getBytes(Charsets.UTF_8));

        try {
            plugin.getProxy().getScheduler().runAsync(plugin, () -> {
                if (plugin.getDatabaseManager().getDatabase().playerIsPremium(pendingConnection.getName())) {
                    //.connect(ProxyServer.getInstance().getServerInfo(DynamicPremium.getInstance().getConfigUtils().getConfig(DynamicPremium.getInstance(), "Settings").getString("LobbyServer")));
                    pendingConnection.setOnlineMode(true);
                    if (configuration.getString("UUIDMode").equalsIgnoreCase("NO_PREMIUM")) {
                        setUuid(pendingConnection, offlineuuid);
                    }
                } else {
                    e.setCancelled(false);
                    pendingConnection.setOnlineMode(false);
                    setUuid(pendingConnection, offlineuuid);
                }
                e.completeIntent(plugin);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            e.completeIntent(plugin);
            e.setCancelled(true);
            e.setCancelReason("Error");
        }
    }

    public void setUuid(PendingConnection pendingConnection, UUID uuid) {
        try {
            Field field = InitialHandler.class.getDeclaredField("uniqueId");
            field.setAccessible(true);
            field.set(pendingConnection, uuid);
        } catch (NoSuchFieldException | IllegalAccessException noSuchFieldException) {
            noSuchFieldException.printStackTrace();
        }
    }


    @EventHandler
    public void onServerChangeEvent(ServerConnectEvent e) {
        Configuration settings = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
        ProxiedPlayer proxiedPlayer = e.getPlayer();
        DynamicPremium plugin = DynamicPremium.getInstance();
        if (plugin.getDatabaseManager().getDatabase().playerIsPremium(proxiedPlayer.getName())) {
            ServerInfo newTarget = ProxyServer.getInstance().getServerInfo(settings.getString("LobbyServer"));
            if (settings.getStringList("AuthServers").contains(e.getTarget().getName())) {
                e.setTarget(newTarget);
            }
        }
    }


    @EventHandler
    public void onPostLoginEvent(PostLoginEvent e) {
        DynamicPremium plugin = DynamicPremium.getInstance();
        if (plugin.getDatabaseManager().getDatabase().playerIsPremium(e.getPlayer().getName())) {
            Configuration configuration = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
            if (!configuration.getString("LoginType").equals("DIRECT")) { // Fixes xd
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
