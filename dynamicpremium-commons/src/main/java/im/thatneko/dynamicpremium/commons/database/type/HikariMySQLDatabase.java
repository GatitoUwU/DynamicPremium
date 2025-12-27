package im.thatneko.dynamicpremium.commons.database.type;

import com.zaxxer.hikari.HikariDataSource;
import im.thatneko.dynamicpremium.commons.config.Config;
import im.thatneko.dynamicpremium.commons.database.DatabaseManager;
import im.thatneko.dynamicpremium.commons.database.IDatabase;
import im.thatneko.dynamicpremium.commons.database.LoginTristate;
import im.thatneko.dynamicpremium.commons.database.data.VerificationData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public class HikariMySQLDatabase implements IDatabase {
    private HikariDataSource hikari;

    @Override
    public void loadDatabase(Config config, DatabaseManager databaseManager) {
        this.hikari = new HikariDataSource();
        this.hikari.setJdbcUrl(
                String.format(
                        "jdbc:mysql://%s:%s/%s",
                        config.getString("mysql.host"),
                        config.getInt("mysql.port"),
                        config.getString("mysql.database")
                )
        );
        this.hikari.setDriverClassName("im.thatneko.dynamicpremium.libs.mysql.jdbc.Driver");

        this.hikari.addDataSourceProperty("verifyServerCertificate", "false");
        this.hikari.addDataSourceProperty("cachePrepStmts", "true");
        this.hikari.addDataSourceProperty("prepStmtCacheSize", "250");
        this.hikari.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        this.hikari.addDataSourceProperty("characterEncoding", "utf8");
        this.hikari.addDataSourceProperty("encoding", "UTF-8");
        this.hikari.addDataSourceProperty("useUnicode", "true");
        this.hikari.addDataSourceProperty("useSSL", "false");
        this.hikari.addDataSourceProperty("requireSSL", "false");

        this.hikari.setUsername(config.getString("mysql.username"));
        this.hikari.setPassword(config.getString("mysql.password"));
        this.hikari.setMaxLifetime(180000L);
        this.hikari.setIdleTimeout(60000L);
        this.hikari.setMinimumIdle(1);
        this.hikari.setMaximumPoolSize(16);

        config.getStringList("mysql.properties").forEach(s ->
                this.hikari.addDataSourceProperty(s.split("=")[0], s.split("=")[1])
        );

        try {
            update("CREATE TABLE IF NOT EXISTS PremiumUsers (PlayerName VARCHAR(100), Enabled VARCHAR(100))");
            update("CREATE TABLE IF NOT EXISTS CheckedUsers (PlayerName VARCHAR(100), Enabled VARCHAR(100))");
            update("CREATE TABLE IF NOT EXISTS VerifyingUsers (PlayerName VARCHAR(100), LoginTristate VARCHAR(100), TimeToLive BIGINT)");
            update("CREATE TABLE IF NOT EXISTS SpoofedUUIDs (PlayerName VARCHAR(100), SpoofedUUID VARCHAR(100))");
            quietUpdate("CREATE UNIQUE INDEX premiumIndex ON PremiumUsers (PlayerName, Enabled)");
            quietUpdate("CREATE UNIQUE INDEX checkedIndex ON CheckedUsers (PlayerName, Enabled)");
            quietUpdate("CREATE UNIQUE INDEX verifyIndex ON VerifyingUsers (PlayerName, LoginTristate, TimeToLive)");
            quietUpdate("CREATE UNIQUE INDEX uuidIndex ON SpoofedUUIDs (PlayerName, SpoofedUUID)");

            System.out.println("DynamicPremium > Connected to MySQL using HikariCP!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DynamicPremium > Oh no, I can't connect to MySQL!");
            System.out.println("DynamicPremium > Send this error to gatogamer#6666!");
        }
    }

    /**
     * Makes a request to MySQL to get if a player exist in database.
     *
     * @param name The player name.
     */
    @Override
    public boolean isPlayerPremium(String name) throws Exception {
        try (Connection connection = this.hikari.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT 1 FROM PremiumUsers WHERE PlayerName = ? LIMIT 1")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Makes a request to MySQL to get if a player exist in database.
     *
     * @param name The player name.
     */
    @Override
    public String getSpoofedUUID(String name) throws Exception {
        String sql = "SELECT SpoofedUUID FROM SpoofedUUIDs WHERE PlayerName = ? LIMIT 1";
        try (Connection connection = hikari.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String uuid = rs.getString("SpoofedUUID");
                    return (uuid != null && !uuid.isEmpty()) ? uuid : null;
                }
                return null;
            }
        }
    }

    /**
     * Creates a new user in MySQL.
     *
     * @param name The player name.
     */
    @Override
    public void addPlayer(String name) throws Exception {
        if (!isPlayerPremium(name)) {
            try (Connection connection = this.hikari.getConnection();
                 PreparedStatement stmt = connection.prepareStatement("INSERT INTO PremiumUsers (PlayerName, Enabled) VALUES (?, 'true')")) {
                stmt.setString(1, name);
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Deletes an user from MySQL.
     *
     * @param name: The name from user to delete on MySQL.
     */
    @Override
    public void removePlayer(String name) {
        try (Connection connection = this.hikari.getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM PremiumUsers WHERE PlayerName = ?")) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new user in MySQL.
     *
     * @param name The player name.
     */
    @Override
    public void addSpoofedUUID(String name, String uuid) {
        try {
            String spoofedRequest = getSpoofedUUID(name);
            if (spoofedRequest == null) {
                try (Connection connection = this.hikari.getConnection();
                     PreparedStatement stmt = connection.prepareStatement("INSERT INTO SpoofedUUIDs (PlayerName, SpoofedUUID) VALUES (?, ?)")) {
                    stmt.setString(1, name);
                    stmt.setString(2, uuid);
                    stmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes an user from MySQL.
     *
     * @param name: The name from user to delete on MySQL.
     */
    @Override
    public void removeSpoofedUUID(String name) {
        try (Connection connection = this.hikari.getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM SpoofedUUIDs WHERE PlayerName = ?")) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean wasPremiumChecked(String name) {
        try (Connection connection = this.hikari.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT 1 FROM CheckedUsers WHERE PlayerName = ? LIMIT 1")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void addPremiumWasCheckedPlayer(String name) {
        if (!wasPremiumChecked(name)) {
            try (Connection connection = this.hikari.getConnection();
                 PreparedStatement stmt = connection.prepareStatement("INSERT INTO CheckedUsers (PlayerName, Enabled) VALUES (?, 'true')")) {
                stmt.setString(1, name);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updatePlayerVerification(VerificationData verificationData) throws SQLException {
        removePlayerVerification(verificationData.getUsername());
        try (Connection connection = this.hikari.getConnection();
             PreparedStatement stmt = connection.prepareStatement("INSERT INTO VerifyingUsers (PlayerName, LoginTristate, TimeToLive) VALUES (?, ?, ?)")) {
            stmt.setString(1, verificationData.getUsername());
            stmt.setString(2, verificationData.getLoginTristate().name());
            stmt.setLong(3, verificationData.getEpoch());
            stmt.executeUpdate();
        }
    }

    @Override
    public void removePlayerVerification(String name) throws SQLException {
        try (Connection connection = this.hikari.getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM VerifyingUsers WHERE PlayerName = ?")) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }

    @Override
    public VerificationData getPlayerVerification(String name) throws SQLException {
        try (Connection connection = this.hikari.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM VerifyingUsers WHERE PlayerName = ?")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String loginTristate = rs.getString("LoginTristate");
                    long timeToLive = rs.getLong("TimeToLive");
                    return new VerificationData(name, timeToLive, LoginTristate.valueOf(loginTristate));
                } else {
                    return new VerificationData(name, -1, LoginTristate.NOTHING);
                }
            }
        }
    }

    public void update(String qry) {
        try (Connection connection = this.hikari.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(qry)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void quietUpdate(String qry) {
        try (Connection connection = hikari.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(qry)) {
                statement.executeUpdate();
            }
        } catch (Exception e) {
        }
    }
}
