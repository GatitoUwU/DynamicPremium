package me.gatogamer.dynamicpremium.spigot.compat.plugins;

import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.LoginEvent;
import me.gatogamer.dynamicpremium.commons.cache.Cache;
import me.gatogamer.dynamicpremium.spigot.DynamicPremium;
import me.gatogamer.dynamicpremium.spigot.compat.Compatibility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AuthMeCompat implements Compatibility, Listener {
    @Override
    public void authPlayer(Player player) {
        AuthMeApi.getInstance().forceLogin(player);
        DynamicPremium.getInstance().getMessageAPI().sendMessage(false, false, player, DynamicPremium.getInstance().getConfig().getString("Authentified"));
    }

    @EventHandler
    public void playerAuthEvent(LoginEvent event) {
        Cache cache = DynamicPremium.getInstance().getCacheManager().getOrCreateCache(event.getPlayer().getName());
        cache.setAuthenticated(true);
    }
}