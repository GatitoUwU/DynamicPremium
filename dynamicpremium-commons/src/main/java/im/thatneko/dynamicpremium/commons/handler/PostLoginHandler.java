package im.thatneko.dynamicpremium.commons.handler;

import im.thatneko.dynamicpremium.commons.BaseDynamicPremium;
import im.thatneko.dynamicpremium.commons.cache.Cache;
import im.thatneko.dynamicpremium.commons.config.Config;
import im.thatneko.dynamicpremium.commons.database.LoginTristate;
import im.thatneko.dynamicpremium.commons.database.data.VerificationData;
import im.thatneko.dynamicpremium.commons.event.DynamicPostLoginEvent;
import im.thatneko.dynamicpremium.commons.player.DynamicPlayer;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.concurrent.CompletableFuture;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class PostLoginHandler {
    private final BaseDynamicPremium dynamicPremium;

    public void handlePostLogin(DynamicPostLoginEvent event) {
        DynamicPlayer dynamicPlayer = event.getDynamicPlayer();
        Cache cache = this.dynamicPremium.getCacheManager().getOrCreateCache(dynamicPlayer.getName());
        cache.updateUsage();

        Config messages = this.dynamicPremium.getConfigManager().getMessagesConfig();
        VerificationData verificationData = cache.getVerificationData();
        LoginTristate trostate = verificationData.getLoginTristate();
        if (trostate.isOnVerification()) {
            verificationData.setLoginTristate(LoginTristate.NOTHING);
            try {
                this.dynamicPremium.getDatabaseManager().getDatabase().updatePlayerVerification(verificationData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(dynamicPlayer.getName() + " was verified, notifying them.");
            dynamicPlayer.sendMessage(
                    LegacyComponentSerializer.legacy('&').deserialize(messages.getString("premium-command.enabled"))
            );
            CompletableFuture.runAsync(() -> {
                try {
                    this.dynamicPremium.getDatabaseManager().getDatabase().addPlayer(dynamicPlayer.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        if (cache.isNotifyCannotBePremium()) {
            cache.setNotifyCannotBePremium(false);
            System.out.println(dynamicPlayer.getName() + " wasn't able to be verified, notifying them.");
            dynamicPlayer.sendMessage(
                    LegacyComponentSerializer.legacy('&').deserialize(messages.getString("premium-command.no-premium"))
            );
        }
    }
}