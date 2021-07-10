package me.gatogamer.dynamicpremium.shared.database;

import lombok.Getter;
import lombok.Setter;
import me.gatogamer.dynamicpremium.bungee.database.type.Flatfile;
import me.gatogamer.dynamicpremium.shared.database.type.MySQL;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;

@Getter
@Setter
public class DatabaseManager {

    private Database database;

    public DatabaseManager (String databaseType) {
        if (databaseType.equalsIgnoreCase("MYSQL")) {
            System.out.println("DynamicPremium > Loading MySQL database.");
            database = new MySQL();
        } else {
            ProxyServer.getInstance().getConsole().sendMessage(c("DynamicPremium > Loading Flatfile database."));
            database = new Flatfile();
        }
    }

    public String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }

}
