package me.gatogamer.dynamicpremium.bungee;

import lombok.Getter;
import lombok.Setter;
import me.gatogamer.dynamicpremium.bungee.commands.PremiumCommand;
import me.gatogamer.dynamicpremium.bungee.config.ConfigCreator;
import me.gatogamer.dynamicpremium.bungee.config.ConfigUtils;
import me.gatogamer.dynamicpremium.bungee.database.DatabaseManager;
import me.gatogamer.dynamicpremium.bungee.listeners.Listeners;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

@Getter
@Setter
public final class DynamicPremium extends Plugin {

    private ConfigUtils configUtils;
    private static DynamicPremium instance;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        setInstance(this);

        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Loading &cDynamicPremium &7by &cgatogamer#1111"));

        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Loading configurations by &ciSnakeBuzz_"));
        setConfigUtils(new ConfigUtils());
        ConfigCreator.get().setupBungee(this, "Settings");
        ConfigCreator.get().setupBungee(this, "PremiumUsers");

        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Loading commands"));
        getProxy().getPluginManager().registerCommand(this, new PremiumCommand("premium"));
        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Commands loaded"));


        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Loading Listeners"));
        getProxy().getPluginManager().registerListener(this, new Listeners());
        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Listeners loaded"));

        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Loading database."));
        setDatabaseManager(new DatabaseManager());
        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Database loaded."));

        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7DynamicPremium has been loaded"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static boolean playerHasPremiumEnabled (String name) {
        return getInstance().getDatabaseManager().getDatabase().playerIsPremium(name);
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
