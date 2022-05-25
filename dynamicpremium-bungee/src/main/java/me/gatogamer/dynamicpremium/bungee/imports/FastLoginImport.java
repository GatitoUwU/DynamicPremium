package me.gatogamer.dynamicpremium.bungee.imports;

import lombok.Getter;
import lombok.Setter;
import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import me.gatogamer.dynamicpremium.bungee.config.ConfigUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

import java.sql.*;
import java.util.Properties;

@Getter
@Setter
public class FastLoginImport {

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    private Connection connection;

    public FastLoginImport() {
        Configuration mainSettings = DynamicPremium.getInstance().getMainSettings();
        if (!mainSettings.getBoolean("Import.FastLogin.enabled")) return;
        setHost(mainSettings.getString("Import.FastLogin.Host"));
        setPort(mainSettings.getString("Import.FastLogin.Port"));
        setUsername(mainSettings.getString("Import.FastLogin.Username"));
        setPassword(mainSettings.getString("Import.FastLogin.Password"));
        setDatabase(mainSettings.getString("Import.FastLogin.Database"));

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
            ProxyServer.getInstance().getConsole().sendMessage(c("&cFastLogin Import -> &7Connected to MySQL"));
            Statement statement = connection.createStatement();
            try {
                statement.executeQuery("SELECT * FROM premium;");
                ResultSet rs = statement.getResultSet();
                while (rs.next()) {
                    try {
                        String name = rs.getString("Name");
                        ProxyServer.getInstance().getConsole().sendMessage(c( "&cFastLogin Import -> &7Checking if "+name+ " is premium."));
                        if (rs.getInt("Premium") == 1) {
                            ProxyServer.getInstance().getConsole().sendMessage(c("&cFastLogin Import -> &7Yes, "+name+ " is premium."));
                            DynamicPremium.getInstance().getDatabaseManager().getDatabase().addPlayer(name);
                            ProxyServer.getInstance().getConsole().sendMessage(c("&cFastLogin Import -> "+name+ " has been added."));
                        } else {
                            ProxyServer.getInstance().getConsole().sendMessage(c("&cFastLogin Import -> &7Nop, "+name+ " isn't premium."));
                        }
                    } catch (Exception e) {}
                }
            } catch (Exception e) {
                System.out.println("Ha ocurrido un error I/O información:");
                System.out.println("---------------------------------------------------------------");
                e.printStackTrace();
                System.out.println("---------------------------------------------------------------");
            }

        } catch (Exception e) {
            ProxyServer.getInstance().getConsole().sendMessage(c( "&cFastLogin Import -> &7Error while connecting with MySQL"));
            e.printStackTrace();
        }
    }

    public String c (String c) { return ChatColor.translateAlternateColorCodes('&', c);}

}
