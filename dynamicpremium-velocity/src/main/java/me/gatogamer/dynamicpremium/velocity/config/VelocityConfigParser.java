package me.gatogamer.dynamicpremium.velocity.config;

import com.moandjiezana.toml.Toml;
import lombok.RequiredArgsConstructor;
import me.gatogamer.dynamicpremium.commons.config.IConfigParser;

import java.util.List;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class VelocityConfigParser implements IConfigParser {
    private final Toml configuration;

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
        return Math.toIntExact(configuration.getLong(key));
    }

    @Override
    public List<String> getStringList(String key) {
        return configuration.getList(key);
    }
}