package im.thatneko.dynamicpremium.bungee.tasks;

import im.thatneko.dynamicpremium.bungee.DynamicPremium;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@RequiredArgsConstructor
public class CacheTask implements Runnable {
    private final DynamicPremium dynamicPremium;

    @Override
    public void run() {
        ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer ->
                dynamicPremium.getCacheManager().getOrCreateCache(proxiedPlayer.getName()).updateUsage()
        );
        dynamicPremium.getCacheManager().tick();
    }
}