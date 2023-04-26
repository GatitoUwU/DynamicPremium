package im.thatneko.dynamicpremium.commons.database.type;

import com.zaxxer.hikari.HikariDataSource;
import im.thatneko.dynamicpremium.commons.config.Config;
import im.thatneko.dynamicpremium.commons.database.Database;
import im.thatneko.dynamicpremium.commons.database.DatabaseManager;

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
public class HikariMySQLDatabase implements Database {

    private HikariDataSource hikari;

    @Override
    public void loadDatabase(Config config, DatabaseManager databaseManager) {
        hikari = new HikariDataSource();
        hikari.setJdbcUrl(
                String.format(
                        "jdbc:mysql://%s:%s/%s",
                        config.getString("mysql.host"),
                        config.getInt("mysql.port"),
                        config.getString("mysql.database")
                )
        );
        hikari.setDriverClassName("im.thatneko.dynamicpremium.libs.mysql.jdbc.Driver");

        hikari.addDataSourceProperty("verifyServerCertificate", "false");
        hikari.addDataSourceProperty("cachePrepStmts", "true");
        hikari.addDataSourceProperty("prepStmtCacheSize", "250");
        hikari.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikari.addDataSourceProperty("characterEncoding", "utf8");
        hikari.addDataSourceProperty("encoding", "UTF-8");
        hikari.addDataSourceProperty("useUnicode", "true");
        hikari.addDataSourceProperty("useSSL", "false");
        hikari.addDataSourceProperty("requireSSL", "false");

        hikari.setUsername(config.getString("mysql.username"));
        hikari.setPassword(config.getString("mysql.password"));
        hikari.setMaxLifetime(180000L);
        hikari.setIdleTimeout(60000L);
        hikari.setMinimumIdle(1);
        hikari.setMaximumPoolSize(16);

        config.getStringList("mysql.properties").forEach(s ->
                hikari.addDataSourceProperty(s.split("=")[0], s.split("=")[1])
        );

        try {
            update("CREATE TABLE IF NOT EXISTS PremiumUsers (PlayerName VARCHAR(100), Enabled VARCHAR(100))");
            update("CREATE TABLE IF NOT EXISTS CheckedUsers (PlayerName VARCHAR(100), Enabled VARCHAR(100))");
            update("CREATE TABLE IF NOT EXISTS SpoofedUUIDs (PlayerName VARCHAR(100), SpoofedUUID VARCHAR(100))");
            quietUpdate("CREATE UNIQUE INDEX premiumIndex ON PremiumUsers (PlayerName, Enabled)");
            quietUpdate("CREATE UNIQUE INDEX uuidIndex ON SpoofedUUIDs (PlayerName, Enabled)");
            quietUpdate("CREATE UNIQUE INDEX checkedIndex ON CheckedUsers (PlayerName, Enabled)");
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
    public boolean isPlayerPremium(String name) {
        try (Connection connection = hikari.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM PremiumUsers WHERE PlayerName='" + name + "'")) {
                ResultSet rs = stmt.executeQuery();
                boolean isPremium = (rs.next() && rs.getString("PlayerName") != null);
                //System.out.println("IsPremium: "+isPremium);
                rs.close();
                return isPremium;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Makes a request to MySQL to get if a player exist in database.
     *
     * @param name The player name.
     */
    @Override
    public String getSpoofedUUID(String name) {
        try (Connection connection = hikari.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM SpoofedUUIDs WHERE PlayerName='" + name + "'")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String uuid = rs.getString("SpoofedUUID");
                    if (!uuid.isEmpty()) {
                        return uuid;
                    }
                }
                return null;
            }
        } catch (Exception e) {
            return null;
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

    @Override
    public boolean wasPremiumChecked(String name) {
        try (Connection connection = hikari.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM CheckedUsers WHERE PlayerName='" + name + "'")) {
                ResultSet rs = stmt.executeQuery();
                boolean isPremium = (rs.next() && rs.getString("PlayerName") != null);
                //System.out.println("IsPremium: "+isPremium);
                rs.close();
                return isPremium;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void addPremiumWasCheckedPlayer(String name) {
        if (!wasPremiumChecked(name)) {
            update("INSERT INTO CheckedUsers (PlayerName, Enabled) VALUES ('" + name + "', 'true')");
        }
    }

    public void update(String qry) {
        try (Connection connection = hikari.getConnection()) {
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
