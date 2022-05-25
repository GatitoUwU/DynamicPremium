package me.gatogamer.dynamicpremium.velocity.config;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public class NyaConfiguration {

    private ProxyServer proxyServer;
    private Logger logger;

    private String name;

    private Toml toml;
    private File dataFolder;
    private File configFile;

    public NyaConfiguration(ProxyServer proxyServer, Logger logger, String name, File dataFolder) {
        this.proxyServer = proxyServer;
        this.logger = logger;

        this.name = name;
        this.dataFolder = dataFolder;
        this.configFile = new File(this.dataFolder, name + ".toml");

        reload();
    }

    public void save() {
        try {
            toml = null;

            try {
                configFile.delete();
            } catch (Exception ignored) {
                // Just ignore since it either does not exist, or we can overwrite
            }

            try (InputStream in = this.getClass().getResourceAsStream("/" + name + ".toml")) {
                Files.copy(in, configFile.toPath());
            }
        } catch (Exception e) {
            logger.error("Error while saving config.", e);
        }
    }

    public void load() {
        try {
            toml = new Toml().read(configFile);
        } catch (Exception e) {
            logger.error("Error while loading config.", e);
        }
    }

    public void reload() {
        try {
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }

            if (!configFile.exists()) {
                save();
            }

            try {
                load();
            } catch (Exception exception) {
                logger.error("Error while loading config, regenerating it.", exception);

                save();
                reload();
            }
        } catch (Exception e) {
            logger.error("Error while reloading the config", e);
        }
    }

    public Toml getToml() {
        return toml;
    }
}