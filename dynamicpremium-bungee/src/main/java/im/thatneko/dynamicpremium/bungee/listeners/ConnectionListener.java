package im.thatneko.dynamicpremium.bungee.listeners;

import im.thatneko.dynamicpremium.bungee.DynamicPremium;
import im.thatneko.dynamicpremium.bungee.player.BungeeDynamicPlayer;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.event.PostLoginEvent;
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
public class ConnectionListener implements Listener {
    private final DynamicPremium dynamicPremium;

    @EventHandler
    public void onPreLoginEvent(PostLoginEvent event) {
        dynamicPremium.getPostLoginHandler().handlePostLogin(() -> new BungeeDynamicPlayer(dynamicPremium, event.getPlayer()));
    }
}