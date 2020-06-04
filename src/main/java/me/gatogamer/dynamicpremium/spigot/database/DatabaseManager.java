package me.gatogamer.dynamicpremium.spigot.database;

import lombok.Getter;
import lombok.Setter;
import me.gatogamer.dynamicpremium.spigot.DynamicPremium;
import me.gatogamer.dynamicpremium.spigot.database.type.MySQL;

@Getter
@Setter
public class DatabaseManager {

    private Database database;

    public DatabaseManager() {
        DynamicPremium.getInstance().getMessageAPI().sendMessage(false, true, "&7Loading MySQL.");
        database = new MySQL();
        database.loadDatabase(this);
    }

}
