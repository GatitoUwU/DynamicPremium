package me.gatogamer.dynamicpremium.bungee.listeners;

import com.google.common.base.Charsets;
import lombok.Getter;
import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import me.gatogamer.dynamicpremium.bungee.config.ConfigUtils;
import me.gatogamer.dynamicpremium.shared.cache.CacheManager;
import me.gatogamer.dynamicpremium.shared.utils.NyaUtils;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

@Getter
public class Listeners implements Listener {

    private final Set<String> premiumList = ConcurrentHashMap.newKeySet();
    private final Set<String> pendingVerification = ConcurrentHashMap.newKeySet();
    private final Set<String> onVerification = ConcurrentHashMap.newKeySet();

    private final Pattern allowedNickCharacters = Pattern.compile("[a-zA-Z0-9_]*");

    @EventHandler(priority = -64)
    public void onLogin(LoginEvent e) {
        if (e.getConnection() == null || !e.getConnection().isConnected()) {
            return;
        }
        Configuration configuration = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
        if (configuration.getString("UUIDMode").equalsIgnoreCase("NO_PREMIUM")) {
            PendingConnection pendingConnection = e.getConnection();
            UUID offlineUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + pendingConnection.getName()).getBytes(Charsets.UTF_8));
            setUuid(pendingConnection, offlineUuid);
        }
    }

    @EventHandler(priority = -64)
    public void onPreLoginEvent(PreLoginEvent e) {
        Configuration configuration = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
        if (e.getConnection() == null || e.isCancelled() || !e.getConnection().isConnected()) {
            return;
        }
        PendingConnection pendingConnection = e.getConnection();
        DynamicPremium plugin = DynamicPremium.getInstance();
        UUID offlineUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + pendingConnection.getName()).getBytes(Charsets.UTF_8));

        if (!allowedNickCharacters.matcher(pendingConnection.getName()).matches()) {
            e.setCancelReason("Invalid nickname detected.");
            e.setCancelled(true);
            return;
        }

        if (onVerification.contains(pendingConnection.getName())) {
            pendingVerification.remove(pendingConnection.getName());
            onVerification.remove(pendingConnection.getName());
            premiumList.remove(pendingConnection.getName());
        }

        if (pendingVerification.contains(pendingConnection.getName())) {
            pendingConnection.setOnlineMode(true);
            onVerification.add(pendingConnection.getName());
            premiumList.add(pendingConnection.getName());
            if (configuration.getString("UUIDMode").equalsIgnoreCase("NO_PREMIUM")) {
                setUuid(pendingConnection, offlineUuid);
            }
            return;
        }

        e.registerIntent(plugin);

        try {
            plugin.getProxy().getScheduler().runAsync(plugin, () -> {
                if (plugin.getDatabaseManager().getDatabase().playerIsPremium(pendingConnection.getName())) {
                    //.connect(ProxyServer.getInstance().getServerInfo(DynamicPremium.getInstance().getConfigUtils().getConfig(DynamicPremium.getInstance(), "Settings").getString("LobbyServer")));
                    pendingConnection.setOnlineMode(true);
                    if (configuration.getString("UUIDMode").equalsIgnoreCase("NO_PREMIUM")) {
                        setUuid(pendingConnection, offlineUuid);
                    }
                    premiumList.add(pendingConnection.getName());
                } else {
                    e.setCancelled(false);
                    pendingConnection.setOnlineMode(false);
                    setUuid(pendingConnection, offlineUuid);
                    premiumList.remove(pendingConnection.getName());
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
        if (premiumList.contains(proxiedPlayer.getName())) {
            ServerInfo newTarget = ProxyServer.getInstance().getServerInfo(settings.getString("LobbyServer"));
            if (settings.getStringList("AuthServers").contains(e.getTarget().getName())) {
                e.setTarget(newTarget);
            }
        }
    }

    @EventHandler
    public void onPostLoginEvent(PostLoginEvent e) {
        Configuration mainSettings = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
        if (onVerification.contains(e.getPlayer().getName())) {
            onVerification.remove(e.getPlayer().getName());
            pendingVerification.remove(e.getPlayer().getName());
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("Alert.Enabled")));
            NyaUtils.run(() -> DynamicPremium.getInstance().getDatabaseManager().getDatabase().addPlayer(e.getPlayer().getName()));
        }
        if (premiumList.contains(e.getPlayer().getName())) {
            if (!mainSettings.getString("LoginType").equals("DIRECT")) { // Fixes xd
                DynamicPremium.getInstance().getProxy().getScheduler().runAsync(DynamicPremium.getInstance(), () -> {
                    try {
                        sleep(10L);
                        e.getPlayer().connect(ProxyServer.getInstance().getServerInfo(ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings").getString("LobbyServer")));
                        e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("SendLobbyMessage")));
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