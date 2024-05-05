package im.thatneko.dynamicpremium.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import im.thatneko.dynamicpremium.velocity.DynamicPremium;
import im.thatneko.dynamicpremium.velocity.player.VelocityDynamicPlayer;
import lombok.RequiredArgsConstructor;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class ConnectionListener {
    private final DynamicPremium dynamicPremium;

    @Subscribe
    public void onPreLoginEvent(PostLoginEvent event) {
        this.dynamicPremium.getPostLoginHandler().handlePostLogin(() -> new VelocityDynamicPlayer(this.dynamicPremium, event.getPlayer()));
    }
}