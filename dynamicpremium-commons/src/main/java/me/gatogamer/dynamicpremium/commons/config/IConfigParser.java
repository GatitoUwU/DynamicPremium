package me.gatogamer.dynamicpremium.commons.config;

import java.util.List;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public interface IConfigParser {
    String getString(String key);
    boolean getBoolean(String key);
    int getInt(String key);
    List<String> getStringList(String key);
}