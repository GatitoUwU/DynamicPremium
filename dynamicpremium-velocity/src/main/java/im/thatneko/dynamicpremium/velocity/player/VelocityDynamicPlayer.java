package im.thatneko.dynamicpremium.velocity.player;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import im.thatneko.dynamicpremium.commons.player.DynamicPlayer;
import im.thatneko.dynamicpremium.velocity.DynamicPremium;
import net.kyori.adventure.text.Component;

import java.util.UUID;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public class VelocityDynamicPlayer extends DynamicPlayer {
    private final DynamicPremium dynamicPremium;
    private final Player player;
    private final CommandSource source;


    public VelocityDynamicPlayer(DynamicPremium dynamicPremium, Player player) {
        this(dynamicPremium, player, player);
    }

    public VelocityDynamicPlayer(DynamicPremium dynamicPremium, Player player, CommandSource source) {
        this.dynamicPremium = dynamicPremium;
        this.player = player;
        this.source = source;
    }

    @Override
    public void sendMessage(Component message) {
        source.sendMessage(message);
    }

    @Override
    public void kickPlayer(Component reason) {
        player.disconnect(reason);
    }

    @Override
    public boolean hasPermission(String permission) {
        return source.hasPermission(permission);
    }

    @Override
    public String getName() {
        return player == null ? "console" : player.getUsername();
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public void sendToServer(String s) {
        dynamicPremium.getProxyServer().getServer(s).ifPresent(registeredServer ->
                player.createConnectionRequest(registeredServer).fireAndForget()
        );
    }

    @Override
    public boolean isConsole() {
        return player == null;
    }
}