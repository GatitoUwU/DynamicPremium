package im.thatneko.dynamicpremium.bungee.listeners;

import im.thatneko.dynamicpremium.bungee.DynamicPremium;
import im.thatneko.dynamicpremium.bungee.event.BungeePreLoginEvent;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class PreConnectionListener implements Listener {
    private final DynamicPremium dynamicPremium;

    @EventHandler(priority = -64)
    public void onPreLoginEvent(PreLoginEvent event) {
        if (event.getConnection() == null || event.isCancelled() || !event.getConnection().isConnected() || event.getConnection().getName() == null) {
            TextComponent textComponent = new TextComponent("Error while processing your connection...");
            textComponent.setColor(ChatColor.RED);

            event.setCancelled(true);
            event.setCancelReason(textComponent);
            return;
        }
        this.dynamicPremium.getPreLoginHandler().handlePreLogin(new BungeePreLoginEvent(this.dynamicPremium, event));
    }
}