package im.thatneko.dynamicpremium.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import im.thatneko.dynamicpremium.commons.BaseDynamicPremium;
import im.thatneko.dynamicpremium.velocity.command.VelocityCommandImpl;
import im.thatneko.dynamicpremium.velocity.listeners.ChatListener;
import im.thatneko.dynamicpremium.velocity.listeners.ChooseServerListener;
import im.thatneko.dynamicpremium.velocity.listeners.ConnectionListener;
import im.thatneko.dynamicpremium.velocity.listeners.PreConnectionListener;
import im.thatneko.dynamicpremium.velocity.listeners.ProfileRequestListener;
import im.thatneko.dynamicpremium.velocity.tasks.CacheTask;
import lombok.Getter;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.logging.Logger;

@Plugin(
        id = "dynamicpremium-velocity",
        name = "DynamicPremium",
        version = "2.0.3",
        description = "The simplest /premium plugin ever.",
        url = "https://thatneko.im",
        authors = {"gatogamer"}
)
@Getter
public class DynamicPremium extends BaseDynamicPremium {
    private ProxyServer proxyServer;
    private Logger logger;

    @Inject
    public DynamicPremium(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataFolder) {
        super(dataFolder.toFile());
        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Arrays.asList(
                new ChatListener(getConfigManager().getSettingsConfig(), getConfigManager().getMessagesConfig()),
                new ChooseServerListener(this),
                new ConnectionListener(this),
                new PreConnectionListener(this),
                new ProfileRequestListener(this)
        ).forEach(listener -> this.proxyServer.getEventManager().register(this, listener));

        super.getCommandManager().getCommands().forEach((s, command) -> new VelocityCommandImpl(this, command));

        this.proxyServer.getScheduler().buildTask(this, new CacheTask(this))
                .repeat(Duration.ofMillis(500)).schedule();
    }
}