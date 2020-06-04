package me.gatogamer.dynamicpremium.spigot.database;

public interface Database {
    void loadDatabase(DatabaseManager databaseManager);
    boolean isPlayerPremium(String name);
    void createPlayer(String name);
    void deletePlayer(String name);
}
