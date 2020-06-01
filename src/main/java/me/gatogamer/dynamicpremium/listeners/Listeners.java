package me.gatogamer.dynamicpremium.listeners;

import com.google.common.base.Charsets;
import me.gatogamer.dynamicpremium.DynamicPremium;
import me.gatogamer.dynamicpremium.config.ConfigUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class Listeners implements Listener {

    @EventHandler(priority = -128)
    public void onPreLoginEvent(PreLoginEvent e) {
        if (e.isCancelled()) {
            return;
        }
        PendingConnection pendingConnection = e.getConnection();
        DynamicPremium plugin = DynamicPremium.getInstance();
        e.registerIntent(plugin);
        UUID offlineuuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + pendingConnection.getName()).getBytes(Charsets.UTF_8));

        if (DynamicPremium.playerHasPremiumEnabled(pendingConnection.getName())) {
            e.completeIntent(plugin);
            //.connect(ProxyServer.getInstance().getServerInfo(DynamicPremium.getInstance().getConfigUtils().getConfig(DynamicPremium.getInstance(), "Settings").getString("LobbyServer")));
            pendingConnection.setOnlineMode(true);
            pendingConnection.setUniqueId(offlineuuid);
        } else {
            e.setCancelled(false);
            e.completeIntent(plugin);
            pendingConnection.setOnlineMode(false);
            pendingConnection.setUniqueId(offlineuuid);
        }
    }

    @EventHandler
    public void onPostLoginEvent(PostLoginEvent e) {
        if (DynamicPremium.playerHasPremiumEnabled(e.getPlayer().getName())) {
            Configuration configuration = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
            DynamicPremium.getInstance().getProxy().getScheduler().runAsync(DynamicPremium.getInstance(), () -> {
                try {
                    Thread.sleep(10L);
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
            if (!configuration.getStringList("AllowedCommands").contains(e.getMessage().split(" ")[0])) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', configuration.getString("DeniedCommand")));
                e.setCancelled(true);
            }
        }
    }

    private String getOnlineUUID(String name) {
        String onlineUUID = null;
        try {
            URLConnection connection = (new URL("https://api.mojang.com/users/profiles/minecraft/" + name)).openConnection();
            connection.setDoOutput(true);
            connection.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null)
                response.append(inputLine).append("\n");
            bufferedReader.close();
            onlineUUID = response.toString();
            JSONObject jsonObject = new JSONObject(onlineUUID);
            String uuid = jsonObject.getString("id");
            System.out.println(name + "'s uuid is " + uuid);
            return uuid;
        } catch (Exception exception) {
        }
        return onlineUUID;
    }

}
