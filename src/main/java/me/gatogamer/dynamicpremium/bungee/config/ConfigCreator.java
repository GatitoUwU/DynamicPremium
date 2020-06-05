package me.gatogamer.dynamicpremium.bungee.config;

import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigCreator {
    private static ConfigCreator instance;

    public void setupBungee(Plugin p, String file) {
        try {
            File configFile = new File(p.getDataFolder(), file+".yml");
            if (!configFile.exists()) {
                final InputStream is = p.getResourceAsStream(file + ".yml");
                if (is != null) {
                    Files.copy(is, configFile.toPath());
                }
            }
        } catch (IOException ignored) {
            System.out.println("Error on create " + file + " please contact with Developer: gatogamer#1111");
        }
    }

    public static ConfigCreator get() {
        if (ConfigCreator.instance == null) {
            ConfigCreator.instance = new ConfigCreator();
        }
        return ConfigCreator.instance;
    }
}