package me.gatogamer.dynamicpremium.bungee.database.type;

import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import me.gatogamer.dynamicpremium.bungee.config.ConfigUtils;
import me.gatogamer.dynamicpremium.bungee.database.Database;
import me.gatogamer.dynamicpremium.bungee.database.DatabaseManager;
import net.md_5.bungee.config.Configuration;

import java.util.List;

public class Flatfile implements Database {

    @Override
    public void loadDatabase(DatabaseManager databaseManager) {

    }

    @Override
    public boolean playerIsPremium(String name) {
        Configuration premiumUsers = ConfigUtils.getConfig(DynamicPremium.getInstance(), "PremiumUsers");
        return premiumUsers.getStringList("PremiumUsers").contains(name);
    }

    @Override
    public void addPlayer(String name) {
        Configuration premiums = ConfigUtils.getConfig(DynamicPremium.getInstance(), "PremiumUsers");
        List<String> premiumUsers = premiums.getStringList("PremiumUsers");
        premiumUsers.add(name);
        premiums.set("PremiumUsers", premiumUsers);
        DynamicPremium.getInstance().getConfigUtils().saveConfig(premiums, "PremiumUsers");
    }

    @Override
    public void removePlayer(String name) {
        Configuration premiums = ConfigUtils.getConfig(DynamicPremium.getInstance(), "PremiumUsers");
        List<String> premiumUsers = premiums.getStringList("PremiumUsers");
        premiumUsers.remove(name);
        premiums.set("PremiumUsers", premiumUsers);
        DynamicPremium.getInstance().getConfigUtils().saveConfig(premiums, "PremiumUsers");
    }
}
