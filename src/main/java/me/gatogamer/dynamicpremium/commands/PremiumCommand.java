package me.gatogamer.dynamicpremium.commands;

import me.gatogamer.dynamicpremium.DynamicPremium;
import me.gatogamer.dynamicpremium.cache.Cache;
import me.gatogamer.dynamicpremium.cache.CacheManager;
import me.gatogamer.dynamicpremium.config.ConfigUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class PremiumCommand extends Command {
    public PremiumCommand(String name) {
        super(name);
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        Configuration mainSettings = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            Cache cache = CacheManager.getCacheOrGetNew(player);
            if (!elapsed(3000L, cache.lastUsage)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("Alert.Cooldown")));
                return;
            }
            String mojangId = getOnlineUUID(player.getName());
            Configuration premiums = ConfigUtils.getConfig(DynamicPremium.getInstance(), "PremiumUsers");
            List<String> premiumUsers = premiums.getStringList("PremiumUsers");
            cache.lastUsage = System.currentTimeMillis();
            if (mojangId != null) {
                if (premiumUsers.contains(player.getName())) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("Alert.Disabled")));

                    premiumUsers.remove(player.getName());
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("Alert.Enabled")));

                    premiumUsers.add(player.getName());
                }
                premiums.set("PremiumUsers", premiumUsers);
                DynamicPremium.getInstance().getConfigUtils().saveConfig(premiums, "PremiumUsers");
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("Alert.NoPremium")));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("Alert.PlayerOnly")));
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
        } catch (Exception exception) {
        }
        return onlineUUID;
    }

    public String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }

    public static boolean elapsed(long needed, long time) {
        return elapsed(time) >= needed;
    }

    public static long elapsed(long time) {
        return System.currentTimeMillis() - time;
    }
}
