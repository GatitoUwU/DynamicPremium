package im.thatneko.dynamicpremium.commons.database;

import im.thatneko.dynamicpremium.commons.config.Config;

public interface Database {
    void loadDatabase(Config config, DatabaseManager databaseManager);

    boolean isPlayerPremium(String name);

    String getSpoofedUUID(String name);

    void addPlayer(String name);

    void removePlayer(String name);

    void addSpoofedUUID(String name, String uuid);

    void removeSpoofedUUID(String name);

    boolean wasPremiumChecked(String name);

    void addPremiumWasCheckedPlayer(String name);
}
