package me.gatogamer.dynamicpremium.shared.utils;

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
        Thread thread = new Thread(runnable);
        thread.setName("RedisBungee - NyaUtils Worker Thread");
        thread.start();
    }
}