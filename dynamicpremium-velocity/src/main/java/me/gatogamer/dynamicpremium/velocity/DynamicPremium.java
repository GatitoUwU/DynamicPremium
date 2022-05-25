package me.gatogamer.dynamicpremium.velocity;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import me.gatogamer.dynamicpremium.commons.cache.CacheManager;
import me.gatogamer.dynamicpremium.commons.database.DatabaseManager;
import me.gatogamer.dynamicpremium.commons.database.type.MySQLDatabase;
import me.gatogamer.dynamicpremium.velocity.command.PremiumCommand;
import me.gatogamer.dynamicpremium.velocity.config.NyaConfiguration;
import me.gatogamer.dynamicpremium.velocity.config.VelocityConfigParser;
import me.gatogamer.dynamicpremium.velocity.listeners.ConnectionListener;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@Plugin(
        id = "dynamicpremium",
        name = "DynamicPremium",
        authors = "gatogamer_",
        version = "${project.version}",
        description = "The simplest /premium plugin ever."
)
@Getter
public class DynamicPremium {

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Path dataFolder;

    private NyaConfiguration nyaConfiguration;
    private Toml mainSettings;
    private DatabaseManager databaseManager;
    private CacheManager cacheManager;

    @Inject
    public DynamicPremium(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataFolder) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataFolder = dataFolder;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        if (!dataFolder.toFile().exists()) {
            dataFolder.toFile().mkdir();
        }

        cacheManager = new CacheManager();
        nyaConfiguration = new NyaConfiguration(proxyServer, logger, "config", dataFolder.toFile());
        mainSettings = nyaConfiguration.getToml();

        databaseManager = new DatabaseManager(new VelocityConfigParser(mainSettings), dataFolder.toFile());

        proxyServer.getCommandManager().register("premium", new PremiumCommand(this));

        proxyServer.getEventManager().register(this, new ConnectionListener(nyaConfiguration.getToml(), databaseManager.getDatabase(), proxyServer));
    }
}