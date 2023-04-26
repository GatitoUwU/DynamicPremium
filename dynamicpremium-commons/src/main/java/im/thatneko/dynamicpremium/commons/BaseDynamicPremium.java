package im.thatneko.dynamicpremium.commons;

import im.thatneko.dynamicpremium.commons.cache.CacheManager;
import im.thatneko.dynamicpremium.commons.command.CommandManager;
import im.thatneko.dynamicpremium.commons.config.ConfigManager;
import im.thatneko.dynamicpremium.commons.database.DatabaseManager;
import im.thatneko.dynamicpremium.commons.handler.PostLoginHandler;
import im.thatneko.dynamicpremium.commons.handler.PreLoginHandler;
import lombok.Getter;

import java.io.File;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@Getter
public class BaseDynamicPremium {
    @Getter
    private static BaseDynamicPremium instance;

    private final CacheManager cacheManager;
    private final DatabaseManager databaseManager;
    private final ConfigManager configManager;
    private final CommandManager commandManager;

    private final PreLoginHandler preLoginHandler;
    private final PostLoginHandler postLoginHandler;

    public BaseDynamicPremium(File baseFolder) {
        instance = this;

        this.cacheManager = new CacheManager();
        this.configManager = new ConfigManager(baseFolder);
        this.databaseManager = new DatabaseManager(configManager.getDatabaseConfig(), baseFolder);
        this.commandManager = new CommandManager(this);

        this.preLoginHandler = new PreLoginHandler(this);
        this.postLoginHandler = new PostLoginHandler(this);
    }
}