package im.thatneko.dynamicpremium.commons.config;

import lombok.Getter;
import lombok.SneakyThrows;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@Getter
public class ConfigManager {
    private final File dataFolder;
    private Config databaseConfig;
    private CommentedConfigurationNode databaseNode;
    private Config forcedHostsConfig;
    private CommentedConfigurationNode forcedHostsNode;
    private Config messagesConfig;
    private CommentedConfigurationNode messagesNode;
    private Config settingsConfig;
    private CommentedConfigurationNode settingsNode;

    public ConfigManager(File dataFolder) {
        this.dataFolder = dataFolder;
        this.dataFolder.mkdir();
        this.loadConfigs();
    }

    public void loadConfigs() {
        this.databaseNode = this.loadConfig("database");
        this.databaseConfig = new Config(databaseNode);

        this.forcedHostsNode = this.loadConfig("forced-hosts");
        this.forcedHostsConfig = new Config(forcedHostsNode);

        this.messagesNode = this.loadConfig("messages");
        this.messagesConfig = new Config(messagesNode);

        this.settingsNode = this.loadConfig("settings");
        this.settingsConfig = new Config(settingsNode);
    }

    @SneakyThrows
    public CommentedConfigurationNode loadConfig(String s) {
        File file = new File(this.dataFolder, s + ".yml");
        if (!file.exists()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(s + ".yml")) {
                file.createNewFile();
                assert in != null;
                Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        YamlConfigurationLoader yamlConfigurationLoader = YamlConfigurationLoader.builder()
                .path(file.toPath())
                .build();
        CommentedConfigurationNode config;
        try {
            config = yamlConfigurationLoader.load();
            return config;
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }
}