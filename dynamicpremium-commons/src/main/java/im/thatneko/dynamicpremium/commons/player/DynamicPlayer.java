package im.thatneko.dynamicpremium.commons.player;

import net.kyori.adventure.text.Component;

import java.util.UUID;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public abstract class DynamicPlayer {
    public abstract void sendMessage(Component message);
    public abstract void kickPlayer(Component reason);
    public abstract boolean hasPermission(String permission);
    public abstract String getName();
    public abstract UUID getUniqueId();
    public abstract void sendToServer(String s);
    public abstract boolean isConsole();
}