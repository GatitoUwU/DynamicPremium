package im.thatneko.dynamicpremium.velocity.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import im.thatneko.dynamicpremium.velocity.DynamicPremium;
import im.thatneko.dynamicpremium.velocity.event.VelocityPreLoginEvent;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class PreConnectionListener {
    private final DynamicPremium dynamicPremium;

    @Subscribe(order = PostOrder.LAST)
    public void onPreLoginEvent(PreLoginEvent event) {
        if (!event.getResult().isAllowed()) {
            return;
        }
        if (event.getConnection() == null || event.getUsername() == null) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(
                    Component.text("Error while processing your connection...")
                            .color(NamedTextColor.RED)
            ));
            return;
        }
        dynamicPremium.getPreLoginHandler().handlePreLogin(new VelocityPreLoginEvent(event));
    }
}