package me.gatogamer.dynamicpremium.spigot;

import lombok.Getter;
import lombok.Setter;
import me.gatogamer.dynamicpremium.shared.cache.Cache;
import me.gatogamer.dynamicpremium.shared.cache.CacheManager;
import me.gatogamer.dynamicpremium.shared.database.type.MySQL;
import me.gatogamer.dynamicpremium.spigot.compat.CompatibilityManager;
import me.gatogamer.dynamicpremium.shared.database.DatabaseManager;
import me.gatogamer.dynamicpremium.spigot.messages.MessageAPI;
import me.gatogamer.dynamicpremium.spigot.tasks.SessionCheckerTask;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter
public class DynamicPremium extends JavaPlugin {

    private static DynamicPremium instance;
    private CompatibilityManager compatibilityManager;
    private MessageAPI messageAPI;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        setMessageAPI(new MessageAPI());

        getMessageAPI().sendMessage(false, true, "&7Loading &cDynamicPremium &7by &cgatogamer#1111");

        getMessageAPI().sendMessage(false, true, "&7Loading compatibilities.");
        setCompatibilityManager(new CompatibilityManager());
        getMessageAPI().sendMessage(false, true, "&7Compatibilities loaded.");

        getMessageAPI().sendMessage(false, true, "&7Loading Database.");
        setDatabaseManager(new DatabaseManager("MYSQL"));
        if (databaseManager.getDatabase() instanceof MySQL) {
            MySQL mySQL = (MySQL) databaseManager.getDatabase();

            mySQL.setHost(getConfig().getString("MySQL.Host"));
            mySQL.setPort(getConfig().getString("MySQL.Port"));
            mySQL.setUsername(getConfig().getString("MySQL.Username"));
            mySQL.setPassword(getConfig().getString("MySQL.Password"));
            mySQL.setDatabase(getConfig().getString("MySQL.Database"));
        }
        databaseManager.getDatabase().loadDatabase(databaseManager);
        getMessageAPI().sendMessage(false, true, "&7Database loaded.");


        getMessageAPI().sendMessage(false, true, "&7Loading tasks.");
        new SessionCheckerTask().runTaskTimerAsynchronously(this, 0L, 20L);
        getMessageAPI().sendMessage(false, true, "&7Tasks loaded.");


        getMessageAPI().sendMessage(false, true, "&7Loading &cDynamicPremium &7successfully. (or I think that XD)");
    }

    public static DynamicPremium getInstance() {
        return instance;
    }

    public void loadListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
        getMessageAPI().sendMessage(false, true, "&7Loaded &c"+listener.getClass().getSimpleName());
    }

}
