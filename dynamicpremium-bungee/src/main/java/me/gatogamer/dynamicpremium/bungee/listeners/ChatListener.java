package me.gatogamer.dynamicpremium.bungee.listeners;

import lombok.RequiredArgsConstructor;
import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import me.gatogamer.dynamicpremium.bungee.config.ConfigUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class ChatListener implements Listener {
    private final Configuration configuration;

    @EventHandler
    public void onCommands(ChatEvent e) {
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        if (configuration.getStringList("AuthServers").contains(p.getServer().getInfo().getName())) {
            String[] messages = e.getMessage().split(" ");
            if (!configuration.getStringList("AllowedCommands").contains(messages[0])) {
                e.setCancelled(true);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', configuration.getString("DeniedCommand")));
            }
        }
    }
}