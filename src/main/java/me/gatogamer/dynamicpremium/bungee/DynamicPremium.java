package me.gatogamer.dynamicpremium.bungee;

import lombok.Getter;
import lombok.Setter;
import me.gatogamer.dynamicpremium.bungee.commands.AdminCommand;
import me.gatogamer.dynamicpremium.bungee.commands.PremiumCommand;
import me.gatogamer.dynamicpremium.bungee.config.ConfigCreator;
import me.gatogamer.dynamicpremium.bungee.config.ConfigUtils;
import me.gatogamer.dynamicpremium.bungee.imports.FastLoginImport;
import me.gatogamer.dynamicpremium.bungee.imports.FileConfigurationImport;
import me.gatogamer.dynamicpremium.bungee.listeners.Listeners;
import me.gatogamer.dynamicpremium.shared.database.DatabaseManager;
import me.gatogamer.dynamicpremium.shared.database.type.MySQL;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public final class DynamicPremium extends Plugin {

    private ConfigUtils configUtils;
    private static DynamicPremium instance;
    private DatabaseManager databaseManager;
    private Listeners listeners;

    @Override
    public void onEnable() {
        setInstance(this);

        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Loading &cDynamicPremium &7by &cgatogamer#1111"));

        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Loading configurations using an API by &ciSnakeBuzz_"));
        setConfigUtils(new ConfigUtils());
        ConfigCreator.get().setupBungee(this, "Settings");
        ConfigCreator.get().setupBungee(this, "PremiumUsers");

        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Loading commands"));
        getProxy().getPluginManager().registerCommand(this, new PremiumCommand("premium"));
        getProxy().getPluginManager().registerCommand(this, new AdminCommand("premiumadmin"));
        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Commands loaded"));


        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Loading Listeners"));
        listeners = new Listeners();
        getProxy().getPluginManager().registerListener(this, listeners);
        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Listeners loaded"));

        Configuration mainSettings = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");

        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Loading database."));
        setDatabaseManager(new DatabaseManager(mainSettings.getString("DatabaseType")));
        if (databaseManager.getDatabase() instanceof MySQL) {
            MySQL mySQL = (MySQL) databaseManager.getDatabase();

            mySQL.setHost(mainSettings.getString("MySQL.Host"));
            mySQL.setPort(mainSettings.getString("MySQL.Port"));
            mySQL.setUsername(mainSettings.getString("MySQL.Username"));
            mySQL.setPassword(mainSettings.getString("MySQL.Password"));
            mySQL.setDatabase(mainSettings.getString("MySQL.Database"));
            mySQL.setConfigMySQLProperties(new ConcurrentHashMap<>());
            mainSettings.getStringList("MySQL.Properties").forEach(s ->
                    mySQL.getConfigMySQLProperties().put(s.split("=")[0], s.split("=")[1])
            );
        }
        databaseManager.getDatabase().loadDatabase(databaseManager);

        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Database loaded."));

        new FastLoginImport();
        new FileConfigurationImport();

        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7DynamicPremium has been loaded"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void setConfigUtils(ConfigUtils configUtils) {
        this.configUtils = configUtils;
    }

    public static void setInstance(DynamicPremium instance) {
        DynamicPremium.instance = instance;
    }

    public static DynamicPremium getInstance() {
        return instance;
    }

    public String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}
