package me.gatogamer.dynamicpremium.velocity;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.gatogamer.dynamicpremium.shared.database.DatabaseManager;
import me.gatogamer.dynamicpremium.shared.database.type.MySQL;
import me.gatogamer.dynamicpremium.velocity.command.PremiumCommand;
import me.gatogamer.dynamicpremium.velocity.config.NyaConfiguration;
import me.gatogamer.dynamicpremium.velocity.listeners.ConnectionListener;
import org.slf4j.Logger;
import sun.misc.Unsafe;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.nio.file.Path;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@Plugin(id = "dynamicpremium", name = "DynamicPremium", authors = "gatogamer_", version = "${project.version}")
public class DynamicPremium {

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Path dataFolder;

    private NyaConfiguration nyaConfiguration;

    private DatabaseManager databaseManager;

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

        nyaConfiguration = new NyaConfiguration(proxyServer, logger, "config", dataFolder.toFile());

        Toml mainSettings = nyaConfiguration.getToml();

        databaseManager = new DatabaseManager("MYSQL");
        if (databaseManager.getDatabase() instanceof MySQL) {
            MySQL mySQL = (MySQL) databaseManager.getDatabase();

            mySQL.setHost(mainSettings.getString("MySQL.Host"));
            mySQL.setPort(mainSettings.getString("MySQL.Port"));
            mySQL.setUsername(mainSettings.getString("MySQL.Username"));
            mySQL.setPassword(mainSettings.getString("MySQL.Password"));
            mySQL.setDatabase(mainSettings.getString("MySQL.Database"));
        }
        databaseManager.getDatabase().loadDatabase(databaseManager);

        proxyServer.getCommandManager().register("premium", new PremiumCommand(nyaConfiguration.getToml(), databaseManager.getDatabase()));

        proxyServer.getEventManager().register(this, new ConnectionListener(nyaConfiguration.getToml(), databaseManager.getDatabase(), proxyServer));
    }


}