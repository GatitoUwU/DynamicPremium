package im.thatneko.dynamicpremium.commons.event;

import net.kyori.adventure.text.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public interface DynamicPreLoginEvent {
    String getUsername();
    Object getFloodgatePlayer();

    void lockEvent();
    void unlockEvent();
    boolean isEventAsync();

    void markAsPremium();
    void markAsNoPremium();

    void computeKick(Component component);

    void setUniqueId(UUID uuid);

    /**
     * Is the event is async the runnable will be run in the same thread, but if it's not async, it'll be
     * run in a CompletableFuture thread.
     * Why this? Simple, Velocity runs all events asynchronously, but Bungee doesn't, which means that we need
     * to decide if the event should be run with a pool or not.
     * @param runnable: Runnable to be run.
     */
    default void decideIfAsync(Runnable runnable) {
        if (isEventAsync()) {
            runnable.run();
        } else {
            CompletableFuture.runAsync(runnable);
        }
    }
}