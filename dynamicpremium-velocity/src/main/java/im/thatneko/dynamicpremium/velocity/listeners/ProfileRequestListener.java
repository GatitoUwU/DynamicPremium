package im.thatneko.dynamicpremium.velocity.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import im.thatneko.dynamicpremium.commons.cache.Cache;
import im.thatneko.dynamicpremium.velocity.DynamicPremium;
import lombok.RequiredArgsConstructor;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@RequiredArgsConstructor
public class ProfileRequestListener {
    private final DynamicPremium dynamicPremium;

    @Subscribe(order = PostOrder.FIRST)
    public void onGameProfileEvent(GameProfileRequestEvent event) {
        String username = event.getUsername();

        Cache cache = dynamicPremium.getCacheManager().getOrCreateCache(username);
        cache.updateUsage();
        if (cache.getUuid() != null) {
            event.setGameProfile(event.getGameProfile().withId(cache.getUuid()));
        }
    }
}