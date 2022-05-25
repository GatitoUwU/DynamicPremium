package me.gatogamer.dynamicpremium.spigot.messages;

import me.gatogamer.dynamicpremium.spigot.DynamicPremium;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageAPI {
    public void sendMessage(boolean debug, boolean pluginMessage, Player player, String message) {
        if (debug) {
            if (DynamicPremium.getInstance().getConfig().getBoolean("Debug")) {
                player.sendMessage(returnMessage(debug, pluginMessage, message));
            }
        } else {
            player.sendMessage(returnMessage(debug, pluginMessage, message));
        }
    }

    public void sendMessage(boolean debug, boolean pluginMessage, CommandSender sender, String message) {
        if (debug) {
            if (DynamicPremium.getInstance().getConfig().getBoolean("Debug")) {
                sender.sendMessage(returnMessage(debug, pluginMessage, message));
            }
        } else {
            sender.sendMessage(returnMessage(debug, pluginMessage, message));
        }
    }

    public void sendMessage(boolean debug, boolean pluginMessage, String message) {
        if (debug) {
            if (DynamicPremium.getInstance().getConfig().getBoolean("Debug")) {
                Bukkit.getConsoleSender().sendMessage(returnMessage(debug, pluginMessage, message));
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(returnMessage(debug, pluginMessage, message));
        }
    }

    private String returnMessage(boolean debug, boolean pluginMessage, String message) {
        return colorize((pluginMessage ? "&cDynamicPremium &7-> &f" : "")+ (debug ? "&cDebug &7-> &f" : "") + message);
    }

    public String colorize(String toColorize) {
        return ChatColor.translateAlternateColorCodes('&', toColorize);
    }
}