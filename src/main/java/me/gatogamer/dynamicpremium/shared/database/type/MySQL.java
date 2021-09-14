package me.gatogamer.dynamicpremium.shared.database.type;

import lombok.Getter;
import lombok.Setter;
import me.gatogamer.dynamicpremium.shared.database.Database;
import me.gatogamer.dynamicpremium.shared.database.DatabaseManager;
import net.md_5.bungee.api.ChatColor;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Getter
@Setter
public class MySQL implements Database {

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private Map<String, String> configMySQLProperties;
    private Connection connection;

    @Override
    public void loadDatabase(DatabaseManager databaseManager) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", getUsername());
            properties.setProperty("password", getPassword());
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("verifyServerCertificate", "false");
            properties.setProperty("useSSL", "false");
            properties.setProperty("requireSSL", "false");
            configMySQLProperties.forEach(properties::setProperty);
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, properties);
            update("CREATE TABLE IF NOT EXISTS PremiumUsers (PlayerName VARCHAR(100), Enabled VARCHAR(100))");
            System.out.println("DynamicPremium > Connected to MySQL!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DynamicPremium > Oh no, I can't connect to MySQL!");
            System.out.println("DynamicPremium > Send this error to gatogamer#1111!");
        }
    }

    @Override
    public boolean playerIsPremium(String name) {
        return userExist(name);
    }

    @Override
    public void addPlayer(String name) {
        createUser(name);
    }

    @Override
    public void removePlayer(String name) {
        deleteUser(name);
    }

    public void update(String qry) {
        try {
            connection.createStatement().executeUpdate(qry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes a query.
     *
     * @param query The thing than you'll ask to MySQL.
     */
    public ResultSet query(String query) throws SQLException {
        Statement stmt = connection.createStatement();
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
     * Makes a request to MySQL to get if a player exist in database.
     *
     * @param name The player name.
     */
    public boolean userExist(String name) {
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
    public void createUser(String name) {
        if (!userExist(name)) {
            update("INSERT INTO PremiumUsers (PlayerName, Enabled) VALUES ('" + name + "', 'true')");
        }
    }

    /**
     * Deletes an user from MySQL.
     * @param name: The name from user to delete on MySQL.
     */
    public void deleteUser(String name) {
        update("DELETE FROM PremiumUsers WHERE PlayerName='"+name+"'");
    }

    public String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}
