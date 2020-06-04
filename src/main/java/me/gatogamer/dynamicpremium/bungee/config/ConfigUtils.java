package me.gatogamer.dynamicpremium.bungee.config;

import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigUtils {
    public void saveConfig(Plugin plugin, String configname) throws IOException {
        File pluginDir = plugin.getDataFolder();
        File configFile = new File(pluginDir, String.valueOf(configname) + ".yml");
        Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
    }

    public void saveConfig(Configuration configuration, String configName) {
        File pluginDir = DynamicPremium.getInstance().getDataFolder();
        File configFile = new File(pluginDir, String.valueOf(configName) + ".yml");
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Configuration getConfig(Plugin plugin, String configname) {
        File pluginDir = plugin.getDataFolder();
        File configFile = new File(pluginDir, String.valueOf(configname) + ".yml");
        Configuration configuration = null;
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configuration;
    }

    public Configuration getConfigurationSection(Plugin plugin, String configname, String section) {
        return getConfig(plugin, configname).getSection(section);
    }

    public File getFile(Plugin plugin, String configname) {
        File pluginDir = plugin.getDataFolder();
        File configFile = new File(pluginDir, String.valueOf(configname) + ".yml");
        return configFile;
    }
}
