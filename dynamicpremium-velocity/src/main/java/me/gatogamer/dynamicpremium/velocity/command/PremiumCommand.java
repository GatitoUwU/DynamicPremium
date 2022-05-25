package me.gatogamer.dynamicpremium.velocity.command;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import me.gatogamer.dynamicpremium.commons.cache.Cache;
import me.gatogamer.dynamicpremium.commons.cache.CacheManager;
import me.gatogamer.dynamicpremium.commons.database.Database;
import me.gatogamer.dynamicpremium.commons.database.DatabaseManager;
import me.gatogamer.dynamicpremium.commons.utils.NyaUtils;
import me.gatogamer.dynamicpremium.commons.utils.TimeUtils;
import me.gatogamer.dynamicpremium.commons.utils.UUIDUtils;
import me.gatogamer.dynamicpremium.velocity.DynamicPremium;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class PremiumCommand implements SimpleCommand {

    private final DynamicPremium dynamicPremium;

    @Override
    public void execute(Invocation invocation) {
        CommandSource commandSource = invocation.source();
        Database database = dynamicPremium.getDatabaseManager().getDatabase();
        Toml config = dynamicPremium.getMainSettings();

        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;
            NyaUtils.run(() -> {
                if (database.playerIsPremium(player.getUsername())) {
                    player.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(config.getString("Alert.Disabled")));
                    database.removePlayer(player.getUsername());
                } else {
                    Cache cache = dynamicPremium.getCacheManager().getOrCreateCache(player.getUsername());
                    if (!TimeUtils.elapsed(dynamicPremium.getMainSettings().getLong("PremiumCommandDelay"), cache.getLastUsage())) {
                        player.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(config.getString("Alert.Cooldown")));
                        return;
                    }
                    cache.setLastUsage(System.currentTimeMillis());
                    String mojangId = UUIDUtils.getOnlineUUID(player.getUsername());
                    if (mojangId != null) {
                        player.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(config.getString("Alert.Enabled")));
                        database.addPlayer(player.getUsername());
                    } else {
                        player.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(config.getString("Alert.NoPremium")));
                    }
                }
            });
        } else {
            commandSource.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(config.getString("Alert.PlayerOnly")));
        }
    }
}