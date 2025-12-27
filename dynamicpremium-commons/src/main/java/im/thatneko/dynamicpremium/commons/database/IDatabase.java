package im.thatneko.dynamicpremium.commons.database;

import im.thatneko.dynamicpremium.commons.config.Config;
import im.thatneko.dynamicpremium.commons.database.data.VerificationData;

public interface IDatabase {
    void loadDatabase(Config config, DatabaseManager databaseManager);

    boolean isPlayerPremium(String name) throws Exception;
    String getSpoofedUUID(String name) throws Exception;

    void addPlayer(String name) throws Exception;
    void removePlayer(String name) throws Exception;

    void addSpoofedUUID(String name, String uuid) throws Exception;
    void removeSpoofedUUID(String name) throws Exception;

    boolean wasPremiumChecked(String name) throws Exception;
    void addPremiumWasCheckedPlayer(String name) throws Exception;

    void updatePlayerVerification(VerificationData verificationData) throws Exception;
    void removePlayerVerification(String name) throws Exception;
    VerificationData getPlayerVerification(String name) throws Exception;
}
