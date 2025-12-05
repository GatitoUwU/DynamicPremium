package im.thatneko.dynamicpremium.commons.database;

import im.thatneko.dynamicpremium.commons.BaseDynamicPremium;
import im.thatneko.dynamicpremium.commons.database.data.VerificationData;
import im.thatneko.dynamicpremium.commons.utils.ReturnableCallback;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractSQLDatabase implements IDatabase {
    private boolean debugDatabaseExceptions;

    protected void fireUp() {
        update("CREATE TABLE IF NOT EXISTS PremiumUsers (PlayerName VARCHAR(100), Enabled VARCHAR(100))");
        update("CREATE TABLE IF NOT EXISTS CheckedUsers (PlayerName VARCHAR(100), Enabled VARCHAR(100))");
        update("CREATE TABLE IF NOT EXISTS VerifyingUsers (PlayerName VARCHAR(100), LoginTristate VARCHAR(100), TimeToLive BIGINT)");
        update("CREATE TABLE IF NOT EXISTS SpoofedUUIDs (PlayerName VARCHAR(100), SpoofedUUID VARCHAR(100))");
        quietUpdate("CREATE UNIQUE INDEX premiumIndex ON PremiumUsers (PlayerName, Enabled)");
        quietUpdate("CREATE UNIQUE INDEX checkedIndex ON CheckedUsers (PlayerName, Enabled)");
        quietUpdate("CREATE UNIQUE INDEX verifyIndex ON VerifyingUsers (PlayerName, LoginTristate, TimeToLive)");
        quietUpdate("CREATE UNIQUE INDEX uuidIndex ON SpoofedUUIDs (PlayerName, SpoofedUUID)");

        this.debugDatabaseExceptions = BaseDynamicPremium.getInstance().getConfigManager().getDatabaseConfig().getBoolean("debug-sql-exceptions", false);
    }

    /**
     * Makes a request to MySQL to get if a player exist in database.
     *
     * @param name The player name.
     */
    @Override
    public boolean isPlayerPremium(String name) {
        try {
            return query("SELECT * FROM PremiumUsers WHERE PlayerName='" + name + "'",
                    resultSet -> (resultSet.next() && resultSet.getString("PlayerName") != null));
        } catch (SQLException e) {
            if (this.debugDatabaseExceptions) {
                e.printStackTrace();
            }
            return false;
        }
    }


    /**
     * Creates a new user in MySQL.
     *
     * @param name The player name.
     */
    @Override
    public void addPlayer(String name) {
        if (!isPlayerPremium(name)) {
            update("INSERT INTO PremiumUsers (PlayerName, Enabled) VALUES ('" + name + "', 'true')");
        }
    }

    /**
     * Deletes an user from MySQL.
     *
     * @param name: The name from user to delete on MySQL.
     */
    @Override
    public void removePlayer(String name) {
        update("DELETE FROM PremiumUsers WHERE PlayerName='" + name + "'");
    }


    @Override
    public boolean wasPremiumChecked(String name) {
        try {
            return query("SELECT * FROM CheckedUsers WHERE PlayerName='" + name + "'",
                    resultSet -> (resultSet.next() && resultSet.getString("PlayerName") != null));
        } catch (SQLException e) {
            if (this.debugDatabaseExceptions) {
                e.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public void addPremiumWasCheckedPlayer(String name) {
        if (!wasPremiumChecked(name)) {
            update("INSERT INTO CheckedUsers (PlayerName, Enabled) VALUES ('" + name + "', 'true')");
        }
    }

    @Override
    public void updatePlayerVerification(VerificationData verificationData) {
        removePlayerVerification(verificationData.getUsername());
        update(String.format("INSERT INTO VerifyingUsers (PlayerName, LoginTristate, TimeToLive) VALUES ('%s', '%s', %d)",
                verificationData.getUsername(),
                verificationData.getLoginTristate().name(),
                verificationData.getEpoch()
        ));

    }

    @Override
    public void removePlayerVerification(String name) {
        update("DELETE FROM VerifyingUsers WHERE PlayerName='" + name + "'");
    }

    @Override
    public VerificationData getPlayerVerification(String name) {
        try {
            return query("SELECT * FROM VerifyingUsers WHERE PlayerName='" + name + "'", rs -> {
                if (rs.next()) {
                    String loginTristate = rs.getString("LoginTristate");
                    long timeToLive = rs.getLong("TimeToLive");
                    return new VerificationData(name, timeToLive, LoginTristate.valueOf(loginTristate));
                } else {
                    return new VerificationData(name, -1, LoginTristate.NOTHING);
                }
            });
        } catch (SQLException e) {
            if (this.debugDatabaseExceptions) {
                e.printStackTrace();
            }
            return new VerificationData(name, -1, LoginTristate.NOTHING);
        }
    }

    /**
     * Creates a new user in MySQL.
     *
     * @param name The player name.
     */
    @Override
    public void addSpoofedUUID(String name, String uuid) {
        String spoofedRequest = getSpoofedUUID(name);
        if (spoofedRequest == null) {
            update("INSERT INTO SpoofedUUIDs (PlayerName, SpoofedUUID) VALUES ('" + name + "', '" + uuid + "')");
        }
    }

    /**
     * Deletes an user from MySQL.
     *
     * @param name: The name from user to delete on MySQL.
     */
    @Override
    public void removeSpoofedUUID(String name) {
        update("DELETE FROM SpoofedUUIDs WHERE PlayerName='" + name + "'");
    }

    /**
     * Makes a request to MySQL to get if a player exist in database.
     *
     * @param name The player name.
     */
    @Override
    public String getSpoofedUUID(String name) {
        try {
            return query("SELECT * FROM SpoofedUUIDs WHERE PlayerName='" + name + "'",
                    resultSet -> {
                        if (resultSet.next()) {
                            return resultSet.getString("SpoofedUUID");
                        } else {
                            return null;
                        }
                    });
        } catch (SQLException e) {
            if (this.debugDatabaseExceptions) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public abstract void update(String s);
    public abstract void quietUpdate(String s);
    public abstract <R> R query(String s, ReturnableCallback<ResultSet, R> returnableCallback) throws SQLException;
}
