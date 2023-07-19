package im.thatneko.dynamicpremium.commons.utils;

import lombok.experimental.UtilityClass;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@UtilityClass
public class GeyserUtils {
    public FloodgatePlayer getGeyserUser(String username) {
        for (FloodgatePlayer player : FloodgateApi.getInstance().getPlayers()) {
            if (username.equals(player.getCorrectUsername())) {
                return player;
            }
        }
        return null;
    }
}