package im.thatneko.dynamicpremium.velocity.listeners;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import im.thatneko.dynamicpremium.commons.config.Config;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@RequiredArgsConstructor
public class ChatListener {
    private final Config config;
    private final Config messages;

    @Subscribe(order = PostOrder.FIRST)
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!canChat(player, event.getMessage())) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onCommand(CommandExecuteEvent event) {
        CommandSource commandSource = event.getCommandSource();
        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;

            String cmd = event.getCommand();
            if (!cmd.startsWith("/")) {
                cmd = "/" + cmd;
            }
            if (!canChat(player, cmd)) {
                event.setResult(CommandExecuteEvent.CommandResult.denied());
            }
        }
    }

    public boolean canChat(Player player, String msg) {
        ServerConnection serverConnection = player.getCurrentServer().orElse(null);
        if (serverConnection == null) {
            return false;
        }
        if (this.config.getStringList("auth-servers").contains(serverConnection.getServerInfo().getName())) {
            String[] args = msg.split(" ");
            if (!this.config.getStringList("allowed-commands").contains(args[0])) {
                player.sendMessage(
                        LegacyComponentSerializer.legacy('&').deserialize(
                                this.messages.getString("denied-command")
                        )
                );
                return false;
            }
        }
        return true;
    }
}