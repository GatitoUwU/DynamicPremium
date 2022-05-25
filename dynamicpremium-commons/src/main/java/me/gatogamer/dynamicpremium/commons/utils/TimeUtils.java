package me.gatogamer.dynamicpremium.commons.utils;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public class TimeUtils {
    public static boolean elapsed(long needed, long time) {
        return elapsed(time) >= needed;
    }
    public static long elapsed(long time) {
        return System.currentTimeMillis() - time;
    }
}
