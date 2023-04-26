package im.thatneko.dynamicpremium.commons.database.type;

import im.thatneko.dynamicpremium.commons.config.Config;
import im.thatneko.dynamicpremium.commons.database.Database;
import im.thatneko.dynamicpremium.commons.database.DatabaseManager;
import im.thatneko.dynamicpremium.commons.utils.FileIO;
import lombok.SneakyThrows;

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
    public void loadDatabase(Config config, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        System.out.println("DynamicPremium > Loaded Flatfile database!");
    }

    @Override
    public boolean isPlayerPremium(String name) {
        return getFile(name).exists();
    }

    @Override
    public String getSpoofedUUID(String name) {
        File file = getSpoofedFile(name);
        if (file.exists()) {
            return FileIO.readFile(file);
        }
        return null;
    }

    @SneakyThrows
    @Override
    public void addPlayer(String name) {
        getFile(name).createNewFile();
    }

    @SneakyThrows
    @Override
    public void addSpoofedUUID(String name, String uuid) {
        File file = getFile(name);
        file.createNewFile();
        FileIO.writeFile(file, uuid);
    }

    @SneakyThrows
    @Override
    public void removeSpoofedUUID(String name) {
        getFile(name).delete();
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

    @Override
    public boolean wasPremiumChecked(String name) {
        return getCheckedFile(name).exists();
    }

    @SneakyThrows
    @Override
    public void addPremiumWasCheckedPlayer(String name) {
        getCheckedFile(name).createNewFile();
    }

    public File getCheckedFile(String name) {
        File premiumFolder = new File(databaseManager.getDataFolder(), "premiumWasCheckedUsers");
        premiumFolder.mkdirs();
        return new File(premiumFolder, name + ".yml");
    }

    public File getSpoofedFile(String name) {
        File premiumFolder = new File(databaseManager.getDataFolder(), "spoofedUsers");
        premiumFolder.mkdirs();
        return new File(premiumFolder, name + ".yml");
    }
}