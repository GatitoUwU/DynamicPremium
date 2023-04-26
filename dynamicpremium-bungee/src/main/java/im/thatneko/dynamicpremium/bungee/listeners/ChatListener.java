package im.thatneko.dynamicpremium.bungee.listeners;

import im.thatneko.dynamicpremium.commons.config.Config;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@RequiredArgsConstructor
public class ChatListener implements Listener {
    private final Config config;
    private final Config messages;

    @EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if (!canChat(player, event.getMessage())) {
            event.setCancelled(true);
        }
    }

    public boolean canChat(ProxiedPlayer player, String msg) {
        Server server = player.getServer();
        if (server == null) {
            return false;
        }
        if (config.getStringList("auth-servers").contains(server.getInfo().getName())) {
            String[] args = msg.split(" ");
            if (!config.getStringList("allowed-commands").contains(args[0])) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("denied-command")));
                return false;
            }
        }
        return true;
    }
}