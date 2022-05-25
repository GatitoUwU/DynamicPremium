package me.gatogamer.dynamicpremium.bungee.utils;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public class ServerDataAsker {

    private static boolean asked = false;
    private static boolean redisBungee = false;

    public static boolean isUnderRedisBungee() {
        if (!asked) {
            try {
                Class.forName("com.imaginarycode.minecraft.redisbungee.RedisBungee");
                redisBungee = true;
            } catch (ClassNotFoundException e) {
            }
            asked = true;
        }
        return redisBungee;
    }

    public static int getPlayersForServer(String name) {
        return RedisBungee.getApi().getPlayersOnServer(name).size();
    }
}