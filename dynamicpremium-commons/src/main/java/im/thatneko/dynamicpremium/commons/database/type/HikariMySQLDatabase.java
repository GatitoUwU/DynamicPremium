package im.thatneko.dynamicpremium.commons.database.type;

import com.zaxxer.hikari.HikariDataSource;
import im.thatneko.dynamicpremium.commons.config.Config;
import im.thatneko.dynamicpremium.commons.database.AbstractSQLDatabase;
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
public class HikariMySQLDatabase extends AbstractSQLDatabase {
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

    @Override
    public void update(String qry) {
        try (Connection connection = this.hikari.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(qry)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void quietUpdate(String qry) {
        try (Connection connection = hikari.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(qry)) {
                statement.executeUpdate();
            }
        } catch (Exception e) {
        }
    }


    @Override
    public ResultSet query(String s) throws SQLException {
        try (Connection connection = this.hikari.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(s)) {
                return stmt.executeQuery();
            }
        }
    }
}
