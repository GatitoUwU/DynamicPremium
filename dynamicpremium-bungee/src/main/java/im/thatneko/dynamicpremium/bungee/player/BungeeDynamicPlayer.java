package im.thatneko.dynamicpremium.bungee.player;

import im.thatneko.dynamicpremium.bungee.DynamicPremium;
import im.thatneko.dynamicpremium.commons.player.DynamicPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public class BungeeDynamicPlayer extends DynamicPlayer {
    private final DynamicPremium dynamicPremium;
    private final ProxiedPlayer player;
    private final CommandSender source;

    public BungeeDynamicPlayer(DynamicPremium dynamicPremium, ProxiedPlayer player) {
        this(dynamicPremium, player, player);
    }

    public BungeeDynamicPlayer(DynamicPremium dynamicPremium, ProxiedPlayer player, CommandSender sender) {
        this.dynamicPremium = dynamicPremium;
        this.player = player;
        this.source = sender;
    }

    @Override
    public void sendMessage(Component message) {
        this.source.sendMessage(BungeeComponentSerializer.get().serialize(message));
    }

    @Override
    public void kickPlayer(Component reason) {
        this.player.disconnect(BungeeComponentSerializer.get().serialize(reason));
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.source.hasPermission(permission);
    }

    @Override
    public String getName() {
        return this.player == null ? "console" : this.player.getName();
    }

    @Override
    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public void sendToServer(String s) {
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(s);
        if (serverInfo == null) {
            return;
        }
        this.player.connect(serverInfo);
    }

    @Override
    public boolean isConsole() {
        return this.player == null;
    }
}