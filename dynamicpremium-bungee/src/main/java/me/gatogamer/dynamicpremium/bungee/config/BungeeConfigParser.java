package me.gatogamer.dynamicpremium.bungee.config;

import lombok.RequiredArgsConstructor;
import me.gatogamer.dynamicpremium.commons.config.IConfigParser;
import net.md_5.bungee.config.Configuration;

import java.util.List;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class BungeeConfigParser implements IConfigParser {
    private final Configuration configuration;

    @Override
    public String getString(String key) {
        return configuration.getString(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return configuration.getBoolean(key);
    }

    @Override
    public int getInt(String key) {
        return configuration.getInt(key);
    }

    @Override
    public List<String> getStringList(String key) {
        return configuration.getStringList(key);
    }
}