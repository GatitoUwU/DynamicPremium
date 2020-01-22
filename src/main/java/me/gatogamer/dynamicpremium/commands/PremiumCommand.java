package me.gatogamer.dynamicpremium.commands;

import me.gatogamer.dynamicpremium.DynamicPremium;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.List;

public class PremiumCommand extends Command {
    public PremiumCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            Configuration configuration = DynamicPremium.getInstance().getConfigUtils().getConfig(DynamicPremium.getInstance(), "Settings");
            Configuration premiums = DynamicPremium.getInstance().getConfigUtils().getConfig(DynamicPremium.getInstance(), "PremiumUsers");
            List<String> premiumUsers = premiums.getStringList("PremiumUsers");
            if (premiumUsers.contains(player.getName())) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', configuration.getString("Premium Toggle.off")));

                premiumUsers.remove(player.getName());
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', configuration.getString("Premium Toggle.on")));

                premiumUsers.add(player.getName());
            }
            premiums.set("PremiumUsers", premiumUsers);
            DynamicPremium.getInstance().getConfigUtils().saveConfig(premiums, "PremiumUsers");
        } else {
            sender.sendMessage("&cFuck you!");
        }
    }

    public String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}
