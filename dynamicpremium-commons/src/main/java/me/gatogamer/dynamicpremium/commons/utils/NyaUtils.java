package me.gatogamer.dynamicpremium.commons.utils;

import java.util.concurrent.CompletableFuture;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public class NyaUtils {
    /**
     * Creates a new thread and executes a Runnable.
     *
     * @param runnable: The runnable that will be ran.
     */
    public static void run(Runnable runnable) {
        CompletableFuture.runAsync(runnable);
    }
}