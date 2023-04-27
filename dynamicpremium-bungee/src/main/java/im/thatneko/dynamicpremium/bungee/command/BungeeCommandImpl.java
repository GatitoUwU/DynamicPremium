package im.thatneko.dynamicpremium.bungee.command;

import im.thatneko.dynamicpremium.bungee.DynamicPremium;
import im.thatneko.dynamicpremium.bungee.player.BungeeDynamicPlayer;
import im.thatneko.dynamicpremium.commons.command.Command;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
public class BungeeCommandImpl extends net.md_5.bungee.api.plugin.Command {
    private final DynamicPremium dynamicPremium;
    private final Command command;

    public BungeeCommandImpl(DynamicPremium dynamicPremium, Command command) {
        super(command.getName(), null, command.getAliases() == null ? new String[]{"dp-" + command.getName()} : command.getAliases());

        this.dynamicPremium = dynamicPremium;
        this.command = command;

        ProxyServer.getInstance().getPluginManager().registerCommand(dynamicPremium.getPlugin(), this);
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        ProxiedPlayer player = sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null;
        command.handle(new BungeeDynamicPlayer(dynamicPremium, player, sender), strings);
    }
}