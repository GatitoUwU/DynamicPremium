package im.thatneko.dynamicpremium.commons.database.type;

import im.thatneko.dynamicpremium.commons.config.Config;
import im.thatneko.dynamicpremium.commons.database.Database;
import im.thatneko.dynamicpremium.commons.database.DatabaseManager;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@Getter
@Setter
public class MySQLDatabase implements Database {
    private Connection connection;

    @Override
    public void loadDatabase(Config config, DatabaseManager databaseManager) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", config.getString("mysql.username"));
            properties.setProperty("password", config.getString("mysql.password"));
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("verifyServerCertificate", "false");
            properties.setProperty("useSSL", "false");
            properties.setProperty("requireSSL", "false");

            config.getStringList("mysql.properties").forEach(s ->
                    properties.setProperty(s.split("=")[0], s.split("=")[1])
            );

            this.connection = DriverManager.getConnection(
                    String.format(
                            "jdbc:mysql://%s:%s/%s",
                            config.getString("mysql.host"),
                            config.getInt("mysql.port"),
                            config.getString("mysql.database")
                    ),
                    properties
            );
            update("CREATE TABLE IF NOT EXISTS PremiumUsers (PlayerName VARCHAR(100), Enabled VARCHAR(100))");
            update("CREATE TABLE IF NOT EXISTS CheckedUsers (PlayerName VARCHAR(100), Enabled VARCHAR(100))");
            update("CREATE TABLE IF NOT EXISTS SpoofedUUIDs (PlayerName VARCHAR(100), SpoofedUUID VARCHAR(100))");
            quietUpdate("CREATE UNIQUE INDEX premiumIndex ON PremiumUsers (PlayerName, Enabled)");
            quietUpdate("CREATE UNIQUE INDEX checkedIndex ON CheckedUsers (PlayerName, Enabled)");
            quietUpdate("CREATE UNIQUE INDEX uuidIndex ON SpoofedUUIDs (PlayerName, SpoofedUUID)");
            System.out.println("DynamicPremium > Connected to MySQL!");
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
        try {
            ResultSet rs = query("SELECT * FROM PremiumUsers WHERE PlayerName='" + name + "'");
            return (rs.next() && rs.getString("PlayerName") != null);
        } catch (SQLException e) {
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
            ResultSet rs = query("SELECT * FROM CheckedUsers WHERE PlayerName='" + name + "'");
            return (rs.next() && rs.getString("PlayerName") != null);
        } catch (SQLException e) {
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
        try {
            this.connection.createStatement().executeUpdate(qry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void quietUpdate(String qry) {
        try {
            this.connection.createStatement().executeUpdate(qry);
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    /**
     * Makes a query.
     *
     * @param query The thing than you'll ask to MySQL.
     */
    public ResultSet query(String query) throws SQLException {
        Statement stmt = this.connection.createStatement();
        try {
            stmt.executeQuery(query);
            return stmt.getResultSet();
        } catch (Exception e) {
            System.out.println("Ha ocurrido un error I/O información:");
            System.out.println("---------------------------------------------------------------");
            e.printStackTrace();
            System.out.println("---------------------------------------------------------------");
            return null;
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
        try (ResultSet rs = query("SELECT * FROM SpoofedUUIDs WHERE PlayerName='" + name + "'")) {
            if (rs.next()) {
                return rs.getString("SpoofedUUID");
            }
            return null;
        } catch (SQLException e) {
            return null;
        }
    }
}
