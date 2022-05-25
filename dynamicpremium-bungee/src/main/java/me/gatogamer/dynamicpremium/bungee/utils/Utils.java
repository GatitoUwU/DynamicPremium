package me.gatogamer.dynamicpremium.bungee.utils;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.connection.InitialHandler;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@UtilityClass
public class Utils {
    public String colorize(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public void setUuid(PendingConnection pendingConnection, UUID uuid) {
        try {
            Field field = InitialHandler.class.getDeclaredField("uniqueId");
            field.setAccessible(true);
            field.set(pendingConnection, uuid);
        } catch (NoSuchFieldException | IllegalAccessException noSuchFieldException) {
            noSuchFieldException.printStackTrace();
        }
    }
}