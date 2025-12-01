package im.thatneko.dynamicpremium.commons.database.type;

import im.thatneko.dynamicpremium.commons.config.Config;
import im.thatneko.dynamicpremium.commons.database.IDatabase;
import im.thatneko.dynamicpremium.commons.database.DatabaseManager;
import im.thatneko.dynamicpremium.commons.database.LoginTristate;
import im.thatneko.dynamicpremium.commons.database.data.VerificationData;
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
public class FlatfileDatabase implements IDatabase {
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
        File premiumFolder = new File(this.databaseManager.getDataFolder(), "premiumUsers");
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

    @Override
    public void updatePlayerVerification(VerificationData verificationData) {
        verificationData.flushToFile(getVerificationFile(verificationData.getUsername()));
    }

    @Override
    public void removePlayerVerification(String name) {
        getVerificationFile(name).delete();
    }

    @Override
    public VerificationData getPlayerVerification(String name) {
        File verificationFile = getVerificationFile(name);
        if (verificationFile.exists()) {
            return VerificationData.fromFile(verificationFile);
        }
        return new VerificationData(name, System.currentTimeMillis(), LoginTristate.NOTHING);
    }


    public File getVerificationFile(String name) {
        File verificationFolder = new File(this.databaseManager.getDataFolder(), "verificationUsers");
        verificationFolder.mkdirs();
        return new File(verificationFolder, name + ".yml");
    }

    public File getCheckedFile(String name) {
        File premiumFolder = new File(this.databaseManager.getDataFolder(), "premiumWasCheckedUsers");
        premiumFolder.mkdirs();
        return new File(premiumFolder, name + ".yml");
    }

    public File getSpoofedFile(String name) {
        File premiumFolder = new File(this.databaseManager.getDataFolder(), "spoofedUsers");
        premiumFolder.mkdirs();
        return new File(premiumFolder, name + ".yml");
    }
}