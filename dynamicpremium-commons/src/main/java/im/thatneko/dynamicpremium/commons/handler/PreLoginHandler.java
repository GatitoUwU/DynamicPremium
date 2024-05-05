package im.thatneko.dynamicpremium.commons.handler;

import im.thatneko.dynamicpremium.commons.BaseDynamicPremium;
import im.thatneko.dynamicpremium.commons.cache.Cache;
import im.thatneko.dynamicpremium.commons.config.Config;
import im.thatneko.dynamicpremium.commons.database.Database;
import im.thatneko.dynamicpremium.commons.event.DynamicPreLoginEvent;
import im.thatneko.dynamicpremium.commons.utils.GeyserUtils;
import im.thatneko.dynamicpremium.commons.utils.UUIDUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class PreLoginHandler {
    private final BaseDynamicPremium dynamicPremium;
    private final Pattern allowedNickCharacters = Pattern.compile("[a-zA-Z0-9_]*");

    public void handlePreLogin(DynamicPreLoginEvent event) {
        Config settingsConfig = this.dynamicPremium.getConfigManager().getSettingsConfig();

        Cache cache;
        // fixes the problem for the retards that have problems with floodgate
        if (settingsConfig.getBoolean("direct-login-for-geyser-users")) {
            String startWith = settingsConfig.getString("geyser-start-name-char");
            FloodgatePlayer floodgatePlayer = GeyserUtils.getGeyserUser(startWith + event.getUsername());
            if (floodgatePlayer != null) {
                cache = this.dynamicPremium.getCacheManager().getOrCreateCache(startWith + event.getUsername());
                cache.setUuid((UUID) null);
                cache.updateUsage();
                cache.setPremium(false);
                cache.setGeyserUser(true);
                event.markAsNoPremium();
                return;
            }
        }

        cache = this.dynamicPremium.getCacheManager().getOrCreateCache(event.getUsername());
        cache.setGeyserUser(false);
        cache.updateUsage();

        if (!this.allowedNickCharacters.matcher(event.getUsername()).matches()) {
            event.computeKick(
                    LegacyComponentSerializer.legacy('&').deserialize(
                            this.dynamicPremium.getConfigManager().getMessagesConfig().getString("kick.invalid-nick")
                    )
            );
            return;
        }

        if (cache.isOnVerification()) {
            cache.setPendingVerification(false);
            cache.setOnVerification(false);
            cache.setPremium(false);
            cache.setNotifyCannotBePremium(false);
        }

        if (cache.isPendingVerification()) {
            event.markAsPremium();
            cache.setOnVerification(true);
            cache.setPremium(true);
            event.lockEvent();
            event.decideIfAsync(() -> {
                recheckUuid(cache, event);
                event.unlockEvent();
            });
            return;
        }

        event.lockEvent();
        event.decideIfAsync(() -> {
            try {
                doPremiumVerification(event, cache);
            } catch (Exception e) {
                e.printStackTrace();
                event.computeKick(
                        Component.text("An error has occurred while processing your connection. (Login)")
                                .color(NamedTextColor.RED)
                );
            }
            event.unlockEvent();
        });
    }

    public void doPremiumVerification(DynamicPreLoginEvent event, Cache cache) {
        Database database = this.dynamicPremium.getDatabaseManager().getDatabase();
        if (database.isPlayerPremium(event.getUsername())) {
            event.markAsPremium();
            cache.setPremium(true);
        } else {
            cache.setPremium(false);
            event.markAsNoPremium();

            Config settings = this.dynamicPremium.getConfigManager().getSettingsConfig();
            String key = "check-if-player-is-premium-first-time";
            if (settings.getBoolean(key + ".enabled")) {
                if (!database.wasPremiumChecked(event.getUsername())) {
                    database.addPremiumWasCheckedPlayer(event.getUsername());
                    if (settings.getString(key + ".check-method", "CONNECTION").equalsIgnoreCase("MOJANG")) {
                        if (UUIDUtils.getOnlineUUID(event.getUsername()) != null) {
                            event.markAsPremium();
                            cache.setPremium(true);
                        }
                    } else {
                        event.markAsPremium();
                        cache.setPremium(true);
                        cache.setPendingVerification(false);
                        cache.setOnVerification(true);
                        System.out.println(event.getUsername() + " is being verified by CONNECTION method.");
                    }
                }
            }

            recheckUuid(cache, event);
        }
        recheckUuid(cache, event);
    }

    public void recheckUuid(Cache cache, DynamicPreLoginEvent event) {
        try {
            String spoofedUUID = this.dynamicPremium.getDatabaseManager().getDatabase().getSpoofedUUID(event.getUsername());
            if (spoofedUUID != null) {
                cache.setUuid(spoofedUUID);
                event.setUniqueId(UUID.fromString(spoofedUUID));
                return;
            }

            String uuidMode = this.dynamicPremium.getConfigManager().getSettingsConfig().getString("uuid-mode", "PREMIUM");
            UUID offlineUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + cache.getUsername())
                    .getBytes(StandardCharsets.UTF_8));

            if (uuidMode.equals("NO_PREMIUM")) {
                if (cache.isPremium()) {
                    cache.setUuid(offlineUuid);
                    event.setUniqueId(offlineUuid);
                } else {
                    cache.setUuid((UUID) null);
                }
            }
        } catch (Exception e) {
            event.computeKick(
                    Component.text("An error has occurred while processing your connection. (UUID)")
                            .color(NamedTextColor.RED)
            );
            e.printStackTrace();
        }
    }
}