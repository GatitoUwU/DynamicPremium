package me.gatogamer.dynamicpremium.bungee.commands;

import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import me.gatogamer.dynamicpremium.bungee.config.ConfigUtils;
import me.gatogamer.dynamicpremium.shared.cache.Cache;
import me.gatogamer.dynamicpremium.shared.cache.CacheManager;
import me.gatogamer.dynamicpremium.shared.database.Database;
import me.gatogamer.dynamicpremium.shared.utils.TimeUtils;
import me.gatogamer.dynamicpremium.shared.utils.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class PremiumCommand extends Command {
    public PremiumCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Configuration mainSettings = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            ProxyServer.getInstance().getScheduler().runAsync(DynamicPremium.getInstance(), () -> {
                Database database = DynamicPremium.getInstance().getDatabaseManager().getDatabase();
                if (database.playerIsPremium(player.getName())) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("Alert.Disabled")));
                    database.removePlayer(player.getName());
                } else {
                    Cache cache = CacheManager.getCacheOrGetNew(player.getName());
                    if (!TimeUtils.elapsed(mainSettings.getLong("PremiumCommandDelay"), cache.lastUsage)) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("Alert.Cooldown")));
                        return;
                    }
                    cache.lastUsage = System.currentTimeMillis();
                    String mojangId = UUIDUtils.getOnlineUUID(player.getName());
                    if (mojangId != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("Alert.Enabled")));
                        database.addPlayer(player.getName());
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("Alert.NoPremium")));
                    }
                }
            });
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("Alert.PlayerOnly")));
        }
    }

}
