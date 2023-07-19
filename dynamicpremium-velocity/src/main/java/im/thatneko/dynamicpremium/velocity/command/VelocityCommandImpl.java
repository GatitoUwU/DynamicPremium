package im.thatneko.dynamicpremium.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import im.thatneko.dynamicpremium.commons.command.Command;
import im.thatneko.dynamicpremium.velocity.DynamicPremium;
import im.thatneko.dynamicpremium.velocity.player.VelocityDynamicPlayer;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
public class VelocityCommandImpl implements SimpleCommand {
    private final DynamicPremium dynamicPremium;
    private final Command command;

    public VelocityCommandImpl(DynamicPremium dynamicPremium, Command command) {
        this.dynamicPremium = dynamicPremium;
        this.command = command;

        if (command.getAliases() == null) {
            dynamicPremium.getProxyServer().getCommandManager().register(
                    command.getName(), this
            );
        } else {
            dynamicPremium.getProxyServer().getCommandManager().register(
                    command.getName(), this, command.getAliases()
            );
        }
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        command.handle(
                new VelocityDynamicPlayer(dynamicPremium, source instanceof Player ? (Player) source : null, source),
                invocation.arguments()
        );
    }
}