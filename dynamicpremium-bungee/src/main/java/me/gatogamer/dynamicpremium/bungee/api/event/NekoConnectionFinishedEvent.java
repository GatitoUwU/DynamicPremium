package me.gatogamer.dynamicpremium.bungee.api.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class NekoConnectionFinishedEvent extends Event {
    private final ProxiedPlayer player;
    private final String forcedHost;
    private final boolean premium;
}