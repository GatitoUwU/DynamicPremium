package me.gatogamer.dynamicpremium.spigot.compat;

import me.gatogamer.dynamicpremium.spigot.DynamicPremium;
import me.gatogamer.dynamicpremium.spigot.compat.plugins.AuthMeCompat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CompatibilityManager {

    public List<Compatibility> compatibilityList = new ArrayList<>();

    public CompatibilityManager() {
        DynamicPremium dynamicPremium = DynamicPremium.getInstance();

        if (Bukkit.getPluginManager().isPluginEnabled("AuthMe")) {
            dynamicPremium.getMessageAPI().sendMessage(false, true, "&7Loading &cAuthMe &7compatibility.");
            AuthMeCompat authMeCompat = new AuthMeCompat();
            Bukkit.getPluginManager().registerEvents(authMeCompat, DynamicPremium.getInstance());
            compatibilityList.add(authMeCompat);
        }
    }

    public void authPlayer(Player player) {
        if (compatibilityList.isEmpty()) {
            return;
        }
        for (Compatibility compatibility : compatibilityList) {
            compatibility.authPlayer(player);
        }
    }
}
