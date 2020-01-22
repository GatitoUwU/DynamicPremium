package me.gatogamer.dynamicpremium.listeners;

import com.google.common.base.Charsets;
import me.gatogamer.dynamicpremium.DynamicPremium;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.UUID;

public class Listeners implements Listener {

    @EventHandler
    public void onPreLoginEvent(PreLoginEvent e) {
        if (e.isCancelled()) {
            return;
        }
        PendingConnection pendingConnection = e.getConnection();
        DynamicPremium plugin = DynamicPremium.getInstance();
        e.registerIntent(plugin);
        UUID mojangUUID = getUUID(e.getConnection().getName());
        String mojangId = mojangUUID.toString();

        if (DynamicPremium.isPlayerPremiumEnabled(e.getConnection().getName())) {
            e.completeIntent(plugin);
        } else {
            e.setCancelled(false);
            e.completeIntent(plugin);
            e.getConnection().setOnlineMode(false);
            e.getConnection().setUniqueId(UUID.nameUUIDFromBytes(("OfflinePlayer:" + e.getConnection().getName()).getBytes(Charsets.UTF_8)));
            if (mojangId != null) {
                e.getConnection().setUniqueId(mojangUUID);
            } else {
                e.getConnection().setUniqueId(UUID.nameUUIDFromBytes(("OfflinePlayer:" + e.getConnection().getName()).getBytes(Charsets.UTF_8)));
            }
        }
    }

    @EventHandler
    public void onPostLoginEvent(PostLoginEvent e) {
        if (DynamicPremium.isPlayerPremiumEnabled(e.getPlayer().getName())) {
            Configuration configuration = DynamicPremium.getInstance().getConfigUtils().getConfig(DynamicPremium.getInstance(), "Settings");
            if (e.getPlayer().getUniqueId().equals(getUUID(e.getPlayer().getName()))) {
                DynamicPremium.getInstance().getProxy().getScheduler().runAsync(DynamicPremium.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000L);
                            e.getPlayer().connect(ProxyServer.getInstance().getServerInfo(DynamicPremium.getInstance().getConfigUtils().getConfig(DynamicPremium.getInstance(), "Settings").getString("LobbyServer")));
                            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', configuration.getString("SendLobbyMessage")));
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            } else {
                e.getPlayer().disconnect(ChatColor.translateAlternateColorCodes('&', configuration.getString("ConnectionCancelled")));
            }
        }
    }

    @EventHandler
    public void onCommands(ChatEvent e) {
        ProxiedPlayer p = (ProxiedPlayer)e.getSender();
        Configuration configuration = DynamicPremium.getInstance().getConfigUtils().getConfig(DynamicPremium.getInstance(), "Settings");
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
        } catch (Exception exception) {}
        return onlineUUID;
    }

    public UUID getUUID(String playername) {
        String output = callURL("https://api.mojang.com/users/profiles/minecraft/" + playername);

        StringBuilder result = new StringBuilder();

        readData(output, result);

        String u = result.toString();

        String uuid = "";

        for(int i = 0; i <= 31; i++) {
            uuid = uuid + u.charAt(i);
            if(i == 7 || i == 11 || i == 15 || i == 19) {
                uuid = uuid + "-";
            }
        }

        return UUID.fromString(uuid);
    }

    private void readData(String toRead, StringBuilder result) {
        int i = 7;

        while(i < 200) {
            if(!String.valueOf(toRead.charAt(i)).equalsIgnoreCase("\"")) {

                result.append(String.valueOf(toRead.charAt(i)));

            } else {
                break;
            }

            i++;
        }
    }

    private String callURL(String URL) {
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;
        try {
            java.net.URL url = new URL(URL);
            urlConn = url.openConnection();

            if (urlConn != null) urlConn.setReadTimeout(60 * 1000);

            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);

                if (bufferedReader != null) {
                    int cp;

                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }

                    bufferedReader.close();
                }
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
