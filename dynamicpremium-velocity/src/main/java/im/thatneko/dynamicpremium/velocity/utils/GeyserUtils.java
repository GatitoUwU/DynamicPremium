package im.thatneko.dynamicpremium.velocity.utils;

import com.google.common.cache.Cache;
import com.google.common.collect.ListMultimap;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.InboundConnection;
import im.thatneko.dynamicpremium.velocity.DynamicPremium;
import lombok.experimental.UtilityClass;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Level;

/**
 * Some parts of this code have been taken from
 * the FastLogin project.
 */
@UtilityClass
public class GeyserUtils {
    public FloodgatePlayer getFloodgatePlayer(InboundConnection connection) {
        try {
            Object floodgateEventHandler = getFloodgateHandler();
            if (floodgateEventHandler == null) {
                return null;
            }

            Field playerCacheField = floodgateEventHandler.getClass().getDeclaredField("playerCache");
            playerCacheField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Cache<InboundConnection, FloodgatePlayer> playerCache =
                    (Cache<InboundConnection, FloodgatePlayer>) playerCacheField.get(floodgateEventHandler);

            return playerCache.getIfPresent(connection);
        } catch (Exception ex) {
            DynamicPremium.getInstance().getLogger().log(Level.SEVERE, "Failed to fetch current floodgate player", ex);
        }

        return null;
    }

    private Object getFloodgateHandler()
            throws NoSuchFieldException, IllegalAccessException {
        EventManager eventManager = DynamicPremium.getInstance().getProxyServer().getEventManager();
        Field handlerField = eventManager.getClass().getDeclaredField("handlersByType");
        handlerField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ListMultimap<Class<?>, ?> handlersByType = (ListMultimap<Class<?>, ?>) handlerField.get(eventManager);

        List<?> loginEventRegistrations = handlersByType.get(PreLoginEvent.class);
        Field pluginField = loginEventRegistrations.get(0).getClass().getDeclaredField("plugin");
        pluginField.setAccessible(true);

        Object floodgateRegistration = null;
        for (Object handler : loginEventRegistrations) {
            PluginContainer eventHandlerPlugin = (PluginContainer) pluginField.get(handler);
            String eventHandlerPluginName = eventHandlerPlugin.getInstance().get().getClass().getName();
            if (eventHandlerPluginName.equals("org.geysermc.floodgate.VelocityPlugin")) {
                floodgateRegistration = handler;
                break;
            }
        }

        if (floodgateRegistration == null) {
            return null;
        }

        Field eventHandlerField = floodgateRegistration.getClass().getDeclaredField("instance");
        eventHandlerField.setAccessible(true);
        return eventHandlerField.get(floodgateRegistration);
    }
}