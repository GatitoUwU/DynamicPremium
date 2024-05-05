package im.thatneko.dynamicpremium.bungee.utils;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.connection.InitialHandler;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@UtilityClass
public class UUIDUtils {
    private static Field uniqueId;
    private static Field offlineId;
    private static Field rewriteId;

    static {
        try {
            uniqueId = InitialHandler.class.getDeclaredField("uniqueId");
            uniqueId.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            uniqueId = null;
        }
        try {
            offlineId = InitialHandler.class.getDeclaredField("offlineId");
            offlineId.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            offlineId = null;
        }
        try {
            rewriteId = InitialHandler.class.getDeclaredField("rewriteId");
            rewriteId.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            rewriteId = null;
        }
    }

    public void setUuid(PendingConnection pendingConnection, UUID uuid) {
        setUUIDField(uniqueId, pendingConnection, uuid);
        setUUIDField(offlineId, pendingConnection, uuid);
        setUUIDField(rewriteId, pendingConnection, uuid);
    }

    public void setUUIDField(Field field, PendingConnection pendingConnection, UUID uuid) {
        if (uniqueId == null) {
            return;
        }
        try {
            uniqueId.set(pendingConnection, uuid);
        } catch (Exception ignored) {
        }
    }
}