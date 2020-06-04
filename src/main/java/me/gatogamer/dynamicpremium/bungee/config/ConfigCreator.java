package me.gatogamer.dynamicpremium.bungee.config;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;

public class ConfigCreator
{
    private static ConfigCreator instance;
    private File pluginDir;
    private File configFile;

    public void setupBungee(final Plugin p, final String configname) {
        if (!p.getDataFolder().exists()) {
            p.getDataFolder().mkdir();
        }
        final File configFile = new File(p.getDataFolder(), String.valueOf(configname) + ".yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                Throwable t = null;
                try {
                    final InputStream is = p.getResourceAsStream(String.valueOf(configname) + ".yml");
                    try {
                        final OutputStream os = new FileOutputStream(configFile);
                        try {
                            ByteStreams.copy(is, os);
                        }
                        finally {
                            if (os != null) {
                                os.close();
                            }
                        }
                        if (is != null) {
                            is.close();
                        }
                    }
                    finally {
                        if (t == null) {
                            final Throwable t2 = null;
                            t = t2;
                        }
                        else {
                            final Throwable t2 = null;
                            if (t != t2) {
                                t.addSuppressed(t2);
                            }
                        }
                        if (is != null) {
                            is.close();
                        }
                    }
                }
                finally {
                    if (t == null) {
                        final Throwable t3 = null;
                        t = t3;
                    }
                    else {
                        final Throwable t3 = null;
                        if (t != t3) {
                            t.addSuppressed(t3);
                        }
                    }
                }
            }
            catch (IOException e) {
                throw new RuntimeException("Error on create " + configname + " please contact with Developer: gatogamer#1111", e);
            }
        }
    }

    public static ConfigCreator get() {
        if (ConfigCreator.instance == null) {
            ConfigCreator.instance = new ConfigCreator();
        }
        return ConfigCreator.instance;
    }
}