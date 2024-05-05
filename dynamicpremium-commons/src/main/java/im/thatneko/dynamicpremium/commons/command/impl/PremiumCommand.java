package im.thatneko.dynamicpremium.commons.command.impl;

import im.thatneko.dynamicpremium.commons.BaseDynamicPremium;
import im.thatneko.dynamicpremium.commons.cache.Cache;
import im.thatneko.dynamicpremium.commons.command.Command;
import im.thatneko.dynamicpremium.commons.player.DynamicPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public class PremiumCommand extends Command {
    private final BaseDynamicPremium dynamicPremium;

    public PremiumCommand(BaseDynamicPremium dynamicPremium) {
        super("premium");
        this.dynamicPremium = dynamicPremium;
    }

    @Override
    public void handle(DynamicPlayer dynamicPlayer, String[] args) {
        if (dynamicPlayer.isConsole()) {
            dynamicPlayer.sendMessage(Component.text("Nope.").color(NamedTextColor.RED));
            return;
        }

        Cache cache = this.dynamicPremium.getCacheManager().getOrCreateCache(dynamicPlayer.getName());
        if (cache.isPremium()) {
            if (this.dynamicPremium.getConfigManager().getSettingsConfig().getBoolean("allow-disable-premium")) {
                cache.setPremium(false);
                dynamicPlayer.kickPlayer(LegacyComponentSerializer.legacy('&').deserialize(
                        this.dynamicPremium.getConfigManager().getMessagesConfig().getString("kick.premium-disabled")
                ));
                this.dynamicPremium.getDatabaseManager().getDatabase().removePlayer(dynamicPlayer.getName());
            } else {
                dynamicPlayer.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(
                        this.dynamicPremium.getConfigManager().getMessagesConfig().getString(
                                "premium-command.config-disabled"
                        )
                ));
            }
            return;
        }
        dynamicPlayer.kickPlayer(LegacyComponentSerializer.legacy('&').deserialize(
                this.dynamicPremium.getConfigManager().getMessagesConfig().getString("kick.checking")
        ));
        cache.setPendingVerification(true);
        cache.setOnVerification(false);
        cache.setPremium(true);
    }
}