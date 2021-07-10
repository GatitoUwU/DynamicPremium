package me.gatogamer.dynamicpremium.velocity.command;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import me.gatogamer.dynamicpremium.shared.cache.Cache;
import me.gatogamer.dynamicpremium.shared.cache.CacheManager;
import me.gatogamer.dynamicpremium.shared.database.Database;
import me.gatogamer.dynamicpremium.shared.utils.NyaUtils;
import me.gatogamer.dynamicpremium.shared.utils.TimeUtils;
import me.gatogamer.dynamicpremium.shared.utils.UUIDUtils;
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

    private final Toml mainSettings;
    private final Database database;

    @Override
    public void execute(Invocation invocation) {
        CommandSource commandSource = invocation.source();

        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;
            NyaUtils.run(() -> {
                if (database.playerIsPremium(player.getUsername())) {
                    player.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(mainSettings.getString("Alert.Disabled")));
                    database.removePlayer(player.getUsername());
                } else {
                    Cache cache = CacheManager.getCacheOrGetNew(player.getUsername());
                    if (!TimeUtils.elapsed(mainSettings.getLong("PremiumCommandDelay"), cache.lastUsage)) {
                        player.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(mainSettings.getString("Alert.Cooldown")));
                        return;
                    }
                    cache.lastUsage = System.currentTimeMillis();
                    String mojangId = UUIDUtils.getOnlineUUID(player.getUsername());
                    if (mojangId != null) {
                        player.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(mainSettings.getString("Alert.Enabled")));
                        database.addPlayer(player.getUsername());
                    } else {
                        player.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(mainSettings.getString("Alert.NoPremium")));
                    }
                }
            });
        } else {
            commandSource.sendMessage(LegacyComponentSerializer.legacy('&').deserialize(mainSettings.getString("Alert.PlayerOnly")));
        }
    }
}
