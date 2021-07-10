package me.gatogamer.dynamicpremium.bungee.imports;

import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import me.gatogamer.dynamicpremium.bungee.config.ConfigUtils;
import me.gatogamer.dynamicpremium.shared.database.Database;
import net.md_5.bungee.config.Configuration;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public class FileConfigurationImport {

    public FileConfigurationImport() {
        Configuration mainSettings = ConfigUtils.getConfig(DynamicPremium.getInstance(), "Settings");
        if (!mainSettings.getBoolean("Import.File.enabled")) return;

        Database database = DynamicPremium.getInstance().getDatabaseManager().getDatabase();

        mainSettings.getStringList("PremiumUsers").forEach(s -> {
            if (!database.playerIsPremium(s)) {
                database.addPlayer(s);
            }
        });
    }
}
