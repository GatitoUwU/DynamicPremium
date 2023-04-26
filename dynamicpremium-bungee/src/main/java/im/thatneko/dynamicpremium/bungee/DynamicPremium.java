package im.thatneko.dynamicpremium.bungee;

import im.thatneko.dynamicpremium.bungee.command.BungeeCommandImpl;
import im.thatneko.dynamicpremium.bungee.listeners.ChatListener;
import im.thatneko.dynamicpremium.bungee.listeners.ChooseServerListener;
import im.thatneko.dynamicpremium.bungee.listeners.ConnectionListener;
import im.thatneko.dynamicpremium.bungee.listeners.PreConnectionListener;
import im.thatneko.dynamicpremium.bungee.tasks.CacheTask;
import im.thatneko.dynamicpremium.commons.BaseDynamicPremium;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@Getter
public class DynamicPremium extends BaseDynamicPremium {
    private final DynamicPremiumBootstrap plugin;

    public DynamicPremium(DynamicPremiumBootstrap dynamicPremiumBootstrap) {
        super(dynamicPremiumBootstrap.getDataFolder());
        this.plugin = dynamicPremiumBootstrap;

        Arrays.asList(
                new ChatListener(getConfigManager().getSettingsConfig(), getConfigManager().getMessagesConfig()),
                new ChooseServerListener(this),
                new ConnectionListener(this),
                new PreConnectionListener(this)
        ).forEach(listener ->
                ProxyServer.getInstance().getPluginManager().registerListener(dynamicPremiumBootstrap, listener)
        );

        super.getCommandManager().getCommands().forEach((s, command) -> new BungeeCommandImpl(this, command));


        ProxyServer.getInstance().getScheduler().schedule(
                plugin, new CacheTask(this), 500L, TimeUnit.MILLISECONDS
        );
    }
}
