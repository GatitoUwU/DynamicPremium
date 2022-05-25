package me.gatogamer.dynamicpremium.commons.database;

import me.gatogamer.dynamicpremium.commons.config.IConfigParser;

public interface Database {
    void loadDatabase(IConfigParser iConfigParser, DatabaseManager databaseManager);
    boolean playerIsPremium(String name);
    void addPlayer(String name);
    void removePlayer(String name);
}
