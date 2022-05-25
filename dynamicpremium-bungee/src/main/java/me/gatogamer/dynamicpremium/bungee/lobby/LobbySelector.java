package me.gatogamer.dynamicpremium.bungee.lobby;

import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.connection.InitialHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@Getter
public class LobbySelector {

    private final Map<String, ForcedLobbies> servers = new ConcurrentHashMap<>();

    public ServerInfo getLobby(Configuration configuration, ProxiedPlayer player) {
        if (configuration.getBoolean("forced-lobbies-mode")) {
            return getApplicableForcedLobby(configuration, player);
        } else {
            return ProxyServer.getInstance().getServerInfo(configuration.getString("LobbyServer"));
        }
    }

    public String getForcedHostFor(ProxiedPlayer proxiedPlayer) {
        String forced = "default";

        try {
            forced = ((InitialHandler) proxiedPlayer.getPendingConnection()).getHandshake().getHost();
        } catch (Exception ignored) {
        }

        return forced.toLowerCase().replaceAll("\\.", "-");
    }

    private ServerInfo getApplicableForcedLobby(Configuration configuration, ProxiedPlayer proxiedPlayer) {
        String forced = getForcedHostFor(proxiedPlayer);

        if (!configuration.contains("LobbyServers."+forced)) {
            DynamicPremium.getInstance().getLogger().log(Level.SEVERE,
                    "DynamicPremium > Configuration doesn't have "+forced+ " has a forced host."
            );
            forced = "default";
        }

        Configuration section = configuration.getSection("LobbyServers." + forced);
        String mode = section.getString("balance-mode");

        ForcedLobbies forcedLobbies = getForcedLobbiesOrCreateThem(configuration, forced);

        return forcedLobbies.getServerByMode(mode);
    }

    public ForcedLobbies getForcedLobbiesOrCreateThem(Configuration configuration, String ip) {
        return servers.computeIfAbsent(ip, ignored -> {
            Configuration section = configuration.getSection("LobbyServers." + ip);

            Set<ServerInfo> servers = new ConcurrentSet<>();

            DynamicPremium.getInstance().getLogger().log(Level.INFO,
                    "DynamicPremium > Servers of " + ip + " are " + section.getStringList("servers")
            );

            section.getStringList("servers").forEach(server ->
                    servers.add(ProxyServer.getInstance().getServerInfo(server))
            );

            return new ForcedLobbies(ip, servers);
        });
    }
}