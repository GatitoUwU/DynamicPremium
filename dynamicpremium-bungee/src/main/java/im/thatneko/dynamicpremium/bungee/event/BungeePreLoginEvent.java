package im.thatneko.dynamicpremium.bungee.event;

import im.thatneko.dynamicpremium.bungee.DynamicPremium;
import im.thatneko.dynamicpremium.bungee.utils.UUIDUtils;
import im.thatneko.dynamicpremium.commons.event.DynamicPreLoginEvent;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.event.PreLoginEvent;

import java.util.UUID;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@RequiredArgsConstructor
public class BungeePreLoginEvent implements DynamicPreLoginEvent {
    private final DynamicPremium dynamicPremium;
    private final PreLoginEvent preLoginEvent;

    @Override
    public String getUsername() {
        return preLoginEvent.getConnection().getName();
    }

    @Override
    public void lockEvent() {
        preLoginEvent.registerIntent(dynamicPremium.getPlugin());
    }

    @Override
    public void unlockEvent() { // not needed on velowocity
        preLoginEvent.completeIntent(dynamicPremium.getPlugin());
    }

    @Override
    public boolean isEventAsync() {
        return false;
    }

    @Override
    public void markAsPremium() {
        preLoginEvent.getConnection().setOnlineMode(true);
    }

    @Override
    public void markAsNoPremium() {
        preLoginEvent.getConnection().setOnlineMode(false);
    }

    @Override
    public void computeKick(Component component) {
        preLoginEvent.setCancelled(true);
        preLoginEvent.setCancelReason(BungeeComponentSerializer.get().serialize(component));
    }

    @Override
    public void setUniqueId(UUID uuid) {
        UUIDUtils.setUuid(preLoginEvent.getConnection(), uuid);
    }
}