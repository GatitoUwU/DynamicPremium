package me.gatogamer.dynamicpremium.spigot;

import lombok.Getter;
import lombok.Setter;
import me.gatogamer.dynamicpremium.commons.cache.CacheManager;
import me.gatogamer.dynamicpremium.commons.database.DatabaseManager;
import me.gatogamer.dynamicpremium.commons.database.type.MySQLDatabase;
import me.gatogamer.dynamicpremium.spigot.compat.CompatibilityManager;
import me.gatogamer.dynamicpremium.spigot.config.SpigotConfigParser;
import me.gatogamer.dynamicpremium.spigot.listeners.ConnectionListener;
import me.gatogamer.dynamicpremium.spigot.messages.MessageAPI;
import me.gatogamer.dynamicpremium.spigot.tasks.SessionCheckerTask;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class DynamicPremium extends JavaPlugin {
    @Getter
    private static DynamicPremium instance;

    private CompatibilityManager compatibilityManager;
    private CacheManager cacheManager;
    private MessageAPI messageAPI;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        messageAPI = new MessageAPI();

        getMessageAPI().sendMessage(false, true, "&7Loading &cDynamicPremium &7by &cgatogamer#6666");

        getMessageAPI().sendMessage(false, true, "&7Loading compatibilities.");
        compatibilityManager = new CompatibilityManager();
        cacheManager = new CacheManager();
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);
        getMessageAPI().sendMessage(false, true, "&7Compatibilities loaded.");

        getMessageAPI().sendMessage(false, true, "&7Loading Database.");
        this.databaseManager = new DatabaseManager(new SpigotConfigParser(getConfig()), getDataFolder());
        getMessageAPI().sendMessage(false, true, "&7Database loaded.");


        getMessageAPI().sendMessage(false, true, "&7Loading tasks.");
        new SessionCheckerTask(this).runTaskTimerAsynchronously(this, 0L, 20L);
        getMessageAPI().sendMessage(false, true, "&7Tasks loaded.");

        getMessageAPI().sendMessage(false, true, "&7Loading &cDynamicPremium &7successfully. (or I think that XD)");
    }
}