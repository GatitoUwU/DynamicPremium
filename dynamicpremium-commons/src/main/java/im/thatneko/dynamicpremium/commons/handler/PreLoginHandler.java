package im.thatneko.dynamicpremium.commons.handler;

import im.thatneko.dynamicpremium.commons.BaseDynamicPremium;
import im.thatneko.dynamicpremium.commons.cache.Cache;
import im.thatneko.dynamicpremium.commons.config.Config;
import im.thatneko.dynamicpremium.commons.database.IDatabase;
import im.thatneko.dynamicpremium.commons.database.LoginTristate;
import im.thatneko.dynamicpremium.commons.database.data.VerificationData;
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
        try {
            handlePreLogin0(event);
        } catch (Exception e) {
            e.printStackTrace();
            event.computeKick(
                    Component.text("An error has occurred while processing your connection. (Generic)")
                            .color(NamedTextColor.RED)
            );
        }
    }

    private void handlePreLogin0(DynamicPreLoginEvent event) throws Exception {
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

        if (!this.allowedNickCharacters.matcher(event.getUsername()).matches()) {
            event.computeKick(
                    LegacyComponentSerializer.legacy('&').deserialize(
                            this.dynamicPremium.getConfigManager().getMessagesConfig().getString("kick.invalid-nick")
                    )
            );
            return;
        }

        IDatabase database = this.dynamicPremium.getDatabaseManager().getDatabase();
        cache = this.dynamicPremium.getCacheManager().getOrCreateCache(event.getUsername());
        cache.setGeyserUser(false);
        cache.updateUsage();
        VerificationData verificationData = database.getPlayerVerification(event.getUsername());
        LoginTristate trostate = verificationData.getLoginTristate();
        cache.setVerificationData(verificationData);

        if (trostate.isOnVerification()) {
            verificationData.setLoginTristate(LoginTristate.NOTHING);
            database.updatePlayerVerification(verificationData);
            cache.setPremium(false);
            cache.setNotifyCannotBePremium(true);
        }

        // player may be cracked in this state, so we set online state and "onVerification" to true, this means
        // the player may get kicked, and with the check above, it'll cause the player to be set as cracked.
        // (because no valid mojang online session)
        if (trostate.isPendingVerification()) {
            event.markAsPremium();
            verificationData.setLoginTristate(LoginTristate.PHASE_2);
            database.updatePlayerVerification(verificationData);
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
                doPremiumVerification(event, cache, verificationData);
            } catch (Exception e) {
                e.printStackTrace();
                event.computeKick(
                        LegacyComponentSerializer.legacy('&').deserialize(
                                this.dynamicPremium.getConfigManager().getMessagesConfig().getString("kick.database-problem")
                        )
                );
            } finally {
                event.unlockEvent();
            }
        });
    }

    public void doPremiumVerification(DynamicPreLoginEvent event, Cache cache, VerificationData verificationData) throws Exception {
        IDatabase database = this.dynamicPremium.getDatabaseManager().getDatabase();
        if (database.isPlayerPremium(event.getUsername())) {
            event.markAsPremium();
            cache.setPremium(true);
        } else {
            event.markAsNoPremium();
            cache.setPremium(false);

            Config settings = this.dynamicPremium.getConfigManager().getSettingsConfig();
            String key = "check-if-player-is-premium-first-time";
            if (settings.getBoolean(key + ".enabled")) {
                if (!database.wasPremiumChecked(event.getUsername())) {
                    database.addPremiumWasCheckedPlayer(event.getUsername());
                    if (settings.getString(key + ".check-method", "CONNECTION").equalsIgnoreCase("MOJANG")) {
                        if (UUIDUtils.getOnlineUUID(event.getUsername()) != null) {
                            event.markAsPremium();
                            cache.setPremium(true);

                            verificationData.setLoginTristate(LoginTristate.NOTHING);
                            database.updatePlayerVerification(verificationData);
                            database.addPlayer(event.getUsername());
                        }
                    } else {
                        event.markAsPremium();
                        cache.setPremium(true);
                        // we set phase 2 as we already have the login (meaning there was no /premium).
                        // so in case this fails, the phase will be 2, meaning if the player gets disconnected
                        // because of a login error (aka, failed to authenticate with session servers),
                        // the player will be able to rejoin as cracked without problems.
                        verificationData.setLoginTristate(LoginTristate.PHASE_2);
                        database.updatePlayerVerification(verificationData);

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