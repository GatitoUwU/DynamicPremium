package me.gatogamer.dynamicpremium.spigot.database.type;

import lombok.Getter;
import lombok.Setter;
import me.gatogamer.dynamicpremium.spigot.DynamicPremium;
import me.gatogamer.dynamicpremium.spigot.database.Database;
import me.gatogamer.dynamicpremium.spigot.database.DatabaseManager;
import org.bukkit.configuration.Configuration;

import java.sql.*;
import java.util.Properties;

@Getter
@Setter
public class MySQL implements Database {

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private Connection connection;

    @Override
    public void loadDatabase(DatabaseManager databaseManager) {
        Configuration mainSettings = DynamicPremium.getInstance().getConfig();
        setHost(mainSettings.getString("MySQL.Host"));
        setPort(mainSettings.getString("MySQL.Port"));
        setUsername(mainSettings.getString("MySQL.Username"));
        setPassword(mainSettings.getString("MySQL.Password"));
        setDatabase(mainSettings.getString("MySQL.Database"));

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", getUsername());
            properties.setProperty("password", getPassword());
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("verifyServerCertificate", "false");
            properties.setProperty("useSSL", "false");
            properties.setProperty("requireSSL", "false");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, properties);
            update("CREATE TABLE IF NOT EXISTS PremiumUsers (PlayerName VARCHAR(100), Enabled VARCHAR(100))");
            DynamicPremium.getInstance().getMessageAPI().sendMessage(false, true, "&7Connected to MySQL");
        } catch (Exception e) {
            e.printStackTrace();
            DynamicPremium.getInstance().getMessageAPI().sendMessage(false, true, "&cSend this error to gatogamer#1111!");
            DynamicPremium.getInstance().getMessageAPI().sendMessage(false, true, "&cOh no, I can't connect to MySQL!");
        }
    }

    @Override
    public boolean isPlayerPremium(String name) {
        return userExist(name);
    }

    @Override
    public void createPlayer(String name) {
        createUser(name);
    }

    @Override
    public void deletePlayer(String name) {
        deleteUser(name);
    }


    public void update(String qry) {
        try {
            connection.createStatement().executeUpdate(qry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getResult(String qry) {
        try {
            return connection.createStatement().executeQuery(qry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Closes an statement or result set.
     *
     * @param st: The thing than you'll close.
     * @param rs: The thing than you'll close.
     */
    public void closeResources(ResultSet rs, PreparedStatement st) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException sQLException) {
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException sQLException) {
            }
        }
    }

    /**
     * Closes an statement or result set.
     *
     * @param st: The thing than you'll close.
     * @param rs: The thing than you'll close.
     */
    public void close(PreparedStatement st, ResultSet rs) {
        try {
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (Exception exception) {
        }
    }

    /**
     * Executes an update.
     *
     * @param statement: The thing than you'll execute on MySQL.
     */
    public void executeUpdate(String statement) {
        try {
            PreparedStatement st = connection.prepareStatement(statement);
            st.executeUpdate();
            close(st, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes an update.
     *
     * @param statement: The thing than you'll execute on MySQL.
     */
    public void executeUpdate(PreparedStatement statement) {
        try {
            statement.executeUpdate();
            close(statement, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes a query.
     *
     * @param statement: The thing than you'll execute on MySQL.
     */
    public ResultSet executeQuery(String statement) {
        try {
            PreparedStatement st = connection.prepareStatement(statement);
            return st.executeQuery();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Executes a query.
     *
     * @param statement: The thing than you'll execute on MySQL.
     */
    public ResultSet executeQuery(PreparedStatement statement) {
        try {
            return statement.executeQuery();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Makes a query.
     *
     * @param query: The thing than you'll ask to MySQL.
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
     * @param name: The player name.
     */
    public boolean userExist(String name) {
        try {
            ResultSet rs = query("SELECT * FROM PremiumUsers WHERE PlayerName='" + name + "'");
            return (rs.next() && rs.getString("PlayerName") != null);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a new user in MySQL.
     *
     * @param name: The player name.
     */
    public void createUser(String name) {
        if (!userExist(name)) {
            update("INSERT INTO PremiumUsers (PlayerName, Enabled) VALUES ('" + name + "', 'true')");
        }
    }

    /**
     * Deletes an user from MySQL.
     *
     * @param name: The name from user to delete on MySQL.
     */
    public void deleteUser(String name) {
        update("DELETE FROM PremiumUsers WHERE PlayerName='" + name + "'");
    }
}
