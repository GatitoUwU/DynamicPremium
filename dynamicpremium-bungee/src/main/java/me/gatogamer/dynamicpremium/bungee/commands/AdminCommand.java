package me.gatogamer.dynamicpremium.bungee.commands;

import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import me.gatogamer.dynamicpremium.bungee.config.ConfigUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class AdminCommand extends Command {
    public AdminCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Configuration mainSettings = DynamicPremium.getInstance().getMainSettings();
        if (sender.hasPermission("dynamicpremium.toggle")) {
            if (args.length < 2) {
                for (String msg : mainSettings.getStringList("Admin.Usage")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                }
            } else {
                ProxyServer.getInstance().getScheduler().runAsync(DynamicPremium.getInstance(), () -> {
                    boolean isPremium = DynamicPremium.getInstance().getDatabaseManager().getDatabase().playerIsPremium(args[1]);
                    if (isPremium) {
                        DynamicPremium.getInstance().getDatabaseManager().getDatabase().removePlayer(args[1]);
                    } else {
                        DynamicPremium.getInstance().getDatabaseManager().getDatabase().addPlayer(args[1]);
                    }
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString(
                            "Admin.Toggled." + (isPremium ? "Dis" : "En") + "abled").replaceAll("%player%", args[1])
                    ));
                });
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', mainSettings.getString("Admin.No Permission")));
        }
    }
}
