package im.thatneko.dynamicpremium.commons.command.impl;

import im.thatneko.dynamicpremium.commons.BaseDynamicPremium;
import im.thatneko.dynamicpremium.commons.command.Command;
import im.thatneko.dynamicpremium.commons.config.Config;
import im.thatneko.dynamicpremium.commons.player.DynamicPlayer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.concurrent.CompletableFuture;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public class PremiumAdminCommand extends Command {
    private final BaseDynamicPremium dynamicPremium;

    public PremiumAdminCommand(BaseDynamicPremium dynamicPremium) {
        super("premiumadmin");
        this.dynamicPremium = dynamicPremium;
    }

    @Override
    public void handle(DynamicPlayer dynamicPlayer, String[] args) {
        Config messagesConfig = dynamicPremium.getConfigManager().getMessagesConfig();
        if (!dynamicPlayer.hasPermission("dynamicpremium.admin")) {
            dynamicPlayer.sendMessage(LegacyComponentSerializer.legacy('&')
                    .deserialize(messagesConfig.getString("admin.no-permission"))
            );
            return;
        }

        if (args.length < 2) {
            dynamicPlayer.sendMessage(LegacyComponentSerializer.legacy('&')
                    .deserialize(messagesConfig.getString("admin.usage"))
            );
            return;
        }
        String subCommand = args[0];
        if (subCommand.equals("toggle")) {
            String name = args[1];
            CompletableFuture.runAsync(() -> {
                try {
                    boolean isPlayerPremium = this.dynamicPremium.getDatabaseManager().getDatabase().isPlayerPremium(name);
                    if (isPlayerPremium) {
                        this.dynamicPremium.getDatabaseManager().getDatabase().removePlayer(name);
                    } else {
                        this.dynamicPremium.getDatabaseManager().getDatabase().addPremiumWasCheckedPlayer(name);
                        this.dynamicPremium.getDatabaseManager().getDatabase().addPlayer(name);
                    }
                    dynamicPlayer.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(
                            messagesConfig.getString("admin.toggled." + (isPlayerPremium ? "disabled" : "enabled"))
                                    .replace("%player%", name)
                    ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
