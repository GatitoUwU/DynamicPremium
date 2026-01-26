package im.thatneko.dynamicpremium.velocity.event;

import com.velocitypowered.api.event.connection.PreLoginEvent;
import im.thatneko.dynamicpremium.commons.event.DynamicPreLoginEvent;
import im.thatneko.dynamicpremium.velocity.utils.GeyserUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

import java.util.UUID;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@RequiredArgsConstructor
public class VelocityPreLoginEvent implements DynamicPreLoginEvent {
    private final PreLoginEvent preLoginEvent;

    @Override
    public String getUsername() {
        return this.preLoginEvent.getUsername();
    }

    @Override
    public Object getFloodgatePlayer() {
        return GeyserUtils.getFloodgatePlayer(this.preLoginEvent.getConnection());
    }

    @Override
    public void lockEvent() { // not needed on velowocity
    }

    @Override
    public void unlockEvent() { // not needed on velowocity
    }

    @Override
    public boolean isEventAsync() {
        return true;
    }

    @Override
    public void markAsPremium() {
        this.preLoginEvent.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
    }

    @Override
    public void markAsNoPremium() {
        this.preLoginEvent.setResult(PreLoginEvent.PreLoginComponentResult.forceOfflineMode());
    }

    @Override
    public void computeKick(Component component) {
        this.preLoginEvent.setResult(PreLoginEvent.PreLoginComponentResult.denied(component));
    }

    @Override
    public void setUniqueId(UUID uuid) { // this is handled on profile event.
    }
}