package me.gatogamer.dynamicpremium.velocity.listeners;

import com.google.common.base.Charsets;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.RequiredArgsConstructor;
import me.gatogamer.dynamicpremium.commons.database.Database;
import me.gatogamer.dynamicpremium.commons.utils.NyaUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class ConnectionListener {

    private final Toml config;
    private final Database database;
    private final ProxyServer proxyServer;

    private final Set<String> premiumList = ConcurrentHashMap.newKeySet();

    @Subscribe(order = PostOrder.LAST)
    public void on(PreLoginEvent event) {
        if (event.getConnection() == null) {
            return;
        }
        String username = event.getUsername();
        try {
            if (database.playerIsPremium(username)) {
                event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
                premiumList.add(username);
            } else {
                premiumList.remove(username);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(Component.text("An exception has occurred while authentication.").color(NamedTextColor.RED)));
        }
    }

    @Subscribe(order = PostOrder.LATE)
    public void onPostLogin(PostLoginEvent event) {
        if (!config.getString("LoginType").equals("DIRECT")) {
            if (premiumList.contains(event.getPlayer().getUsername())) {
                NyaUtils.run(() -> {
                    try {
                        Thread.sleep(500L);
                        String serverName = config.getString("LobbyServer");

                        event.getPlayer().sendMessage(LegacyComponentSerializer.legacy('&').deserialize(config.getString("SendLobbyMessage")));
                        proxyServer.getServer(serverName).ifPresent(server -> event.getPlayer().createConnectionRequest(server).fireAndForget());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void on(GameProfileRequestEvent event) {
        String username = event.getUsername();

        String preUuid = "OfflinePlayer:" + username;
        UUID offlineUuid = UUID.nameUUIDFromBytes(preUuid.getBytes(Charsets.UTF_8));

        if (premiumList.contains(username)) {
            if (config.getString("UUIDMode").equalsIgnoreCase("PREMIUM")) {
                return;
            }
        }

        event.setGameProfile(event.getGameProfile().withId(offlineUuid));
    }

    @Subscribe(order = PostOrder.FIRST)
    public void on(PlayerChooseInitialServerEvent event) {
        Player player = event.getPlayer();

        if (config.getString("LoginType").equals("DIRECT")) {
            if (premiumList.contains(player.getUsername())) {
                String serverName = config.getString("LobbyServer");
                Optional<RegisteredServer> optionalRegisteredServer = proxyServer.getServer(serverName);

                optionalRegisteredServer.ifPresent(registeredServer -> {
                    System.out.println("DynamicPremium > Routing "+player.getUsername()+" into " + serverName + ".");
                    event.setInitialServer(registeredServer);
                });

                if (!optionalRegisteredServer.isPresent()) {
                    System.out.println("DynamicPremium > The server " + serverName + " doesn't exist.");
                }
            }
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void on(PlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!canChat(player, event.getMessage())) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void on(CommandExecuteEvent event) {
        CommandSource commandSource = event.getCommandSource();
        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;

            if (!canChat(player, event.getCommand())) {
                event.setResult(CommandExecuteEvent.CommandResult.denied());
            }
        }
    }

    public boolean canChat(Player player, String msg) {
        ServerConnection serverConnection = player.getCurrentServer().orElse(null);
        if (serverConnection == null) {
            return false;
        }
        if (config.getList("AuthServers").contains(serverConnection.getServerInfo().getName())) {
            String[] messages = msg.split(" ");
            if (!config.getList("AllowedCommands").contains(messages[0])) {
                player.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(config.getString("DeniedCommand")));
                return false;
            }
        }
        return true;
    }
}