package me.gatogamer.dynamicpremium.bungee.commands;

import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import me.gatogamer.dynamicpremium.bungee.config.ConfigUtils;
import me.gatogamer.dynamicpremium.shared.database.Database;
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
                    player.disconnect(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("Alert.Checking")));
                    DynamicPremium.getInstance().getListeners().getPendingVerification().add(player.getName());
                }
            });
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("Alert.PlayerOnly")));
        }
    }

}
