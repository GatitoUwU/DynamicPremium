package me.gatogamer.dynamicpremium.bungee.database;

public interface Database {
    void loadDatabase(DatabaseManager databaseManager);
    boolean playerIsPremium(String name);
    void addPlayer(String name);
    void removePlayer(String name);
}
