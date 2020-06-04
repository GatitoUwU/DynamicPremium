package me.gatogamer.dynamicpremium.bungee.database;

import lombok.Getter;
import lombok.Setter;
import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import me.gatogamer.dynamicpremium.bungee.config.ConfigUtils;
import me.gatogamer.dynamicpremium.bungee.database.type.Flatfile;
import me.gatogamer.dynamicpremium.bungee.database.type.MySQL;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

@Getter
@Setter
public class DatabaseManager {

    private Database database;

    public DatabaseManager () {
        Configuration mainSettings = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
        if (mainSettings.getString("DatabaseType").equalsIgnoreCase("MYSQL")) {
            ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Loading MySQL database."));
            database = new MySQL();
        } else {
            ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium &8> &7Loading Flatfile database."));
            database = new Flatfile();
        }
        database.loadDatabase(this);
    }

    public String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }

}
