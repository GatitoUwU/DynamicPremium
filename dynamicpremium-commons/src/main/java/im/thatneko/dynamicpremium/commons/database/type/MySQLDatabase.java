package im.thatneko.dynamicpremium.commons.database.type;

import im.thatneko.dynamicpremium.commons.config.Config;
import im.thatneko.dynamicpremium.commons.database.AbstractSQLDatabase;
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
public class MySQLDatabase extends AbstractSQLDatabase {
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
            fireUp();
            System.out.println("DynamicPremium > Connected to MySQL!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DynamicPremium > Oh no, I can't connect to MySQL!");
            System.out.println("DynamicPremium > Send this error to gatogamer#6666!");
        }
    }




    @Override
    public void update(String qry) {
        try {
            this.connection.createStatement().executeUpdate(qry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
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
}
