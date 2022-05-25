package me.gatogamer.dynamicpremium.commons.database.type;

import lombok.SneakyThrows;
import me.gatogamer.dynamicpremium.commons.config.IConfigParser;
import me.gatogamer.dynamicpremium.commons.database.Database;
import me.gatogamer.dynamicpremium.commons.database.DatabaseManager;

import java.io.File;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public class FlatfileDatabase implements Database {
    private DatabaseManager databaseManager;

    @Override
    public void loadDatabase(IConfigParser iConfigParser, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        System.out.println("DynamicPremium > Loaded Flatfile database!");
    }

    @Override
    public boolean playerIsPremium(String name) {
        return getFile(name).exists();
    }

    @SneakyThrows
    @Override
    public void addPlayer(String name) {
        getFile(name).createNewFile();
    }

    @Override
    public void removePlayer(String name) {
        getFile(name).delete();
    }

    public File getFile(String name) {
        File premiumFolder = new File(databaseManager.getDataFolder(), "premiumUsers");
        premiumFolder.mkdirs();
        return new File(premiumFolder, name + ".yml");
    }
}