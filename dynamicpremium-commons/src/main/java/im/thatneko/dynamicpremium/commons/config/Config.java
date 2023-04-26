package im.thatneko.dynamicpremium.commons.config;

import lombok.RequiredArgsConstructor;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class Config {
    private final CommentedConfigurationNode configurationNode;

    public String getString(String key, String defaultValue) {
        return getNode(key).getString(defaultValue);
    }

    public String getString(String key) {
        return getNode(key).getString();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return getNode(key).getBoolean(defaultValue);
    }

    public boolean getBoolean(String key) {
        return getNode(key).getBoolean();
    }

    public int getInt(String key, int defaultValue) {
        return getNode(key).getInt(defaultValue);
    }

    public int getInt(String key) {
        return getNode(key).getInt();
    }

    public double getDouble(String key, double defaultValue) {
        return getNode(key).getDouble(defaultValue);
    }

    public double getDouble(String key) {
        return getNode(key).getDouble();
    }

    public List<String> getStringList(String key) {
        try {
            return getNode(key).getList(String.class);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>Retrieves a node using the Bukkit style</p>
     * Example, for "foo.bar" on config will be something like this:
     * <pre>foo:
     *   bar: "Hello"</pre>
     *
     * @param key: Key to be converted into the Bukkit style:
     * @return: Configurate style node.
     */
    public CommentedConfigurationNode getNode(String key) {
        return configurationNode.node(key.split("\\."));
    }
}