package im.thatneko.dynamicpremium.velocity.tasks;

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
public class CacheTask implements Runnable{
    private final DynamicPremium dynamicPremium;

    @Override
    public void run() {
        this.dynamicPremium.getProxyServer().getAllPlayers().forEach(player -> {
            this.dynamicPremium.getCacheManager().getOrCreateCache(player.getUsername()).updateUsage();
        });
        this.dynamicPremium.getCacheManager().tick();
    }
}