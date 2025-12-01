package im.thatneko.dynamicpremium.commons.database;

import im.thatneko.dynamicpremium.commons.config.Config;
import im.thatneko.dynamicpremium.commons.database.data.VerificationData;

public interface IDatabase {
    void loadDatabase(Config config, DatabaseManager databaseManager);

    boolean isPlayerPremium(String name);

    String getSpoofedUUID(String name);

    void addPlayer(String name);
    void removePlayer(String name);

    void addSpoofedUUID(String name, String uuid);
    void removeSpoofedUUID(String name);

    boolean wasPremiumChecked(String name);
    void addPremiumWasCheckedPlayer(String name);

    void updatePlayerVerification(VerificationData verificationData);
    void removePlayerVerification(String name);
    VerificationData getPlayerVerification(String name);
}
