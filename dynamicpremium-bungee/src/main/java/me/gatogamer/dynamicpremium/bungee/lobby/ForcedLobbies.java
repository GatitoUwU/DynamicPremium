package me.gatogamer.dynamicpremium.bungee.lobby;

import lombok.RequiredArgsConstructor;
import me.gatogamer.dynamicpremium.bungee.DynamicPremium;
import me.gatogamer.dynamicpremium.bungee.utils.ServerDataAsker;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
@RequiredArgsConstructor
public class ForcedLobbies {
    private final String name;
    private final Set<ServerInfo> servers;
    private final Map<ServerInfo, Integer> players = new ConcurrentHashMap<>();

    public ServerInfo getServerByMode(String mode) {
        if (mode.equalsIgnoreCase("RANDOM")) {
            return servers.stream().findAny().orElse(null);
        }
        if (mode.equalsIgnoreCase("LOWEST")) {
            return servers.stream().min(Comparator.comparingInt(this::getUsersForServerInfo)).orElse(null);
        }
        if (mode.equalsIgnoreCase("FILL")) {
            return servers.stream().filter(serverInfo ->
                    getMaxPlayersFor(serverInfo) > getUsersForServerInfo(serverInfo)
            ).max(Comparator.comparingInt(this::getUsersForServerInfo)).orElse(null);
        }
        return null;
    }

    public int getUsersForServerInfo(ServerInfo serverInfo) {
        return ServerDataAsker.isUnderRedisBungee() ?
                ServerDataAsker.getPlayersForServer(serverInfo.getName()) : serverInfo.getPlayers().size();
    }

    public int getMaxPlayersFor(ServerInfo serverInfo) {
        return players.computeIfAbsent(serverInfo, serverInfo1 -> {
            BungeeCord.getInstance().getScheduler().runAsync(DynamicPremium.getInstance(), () -> { // make the request async, to prevent lag.
                serverInfo1.ping((serverPing, error) -> players.put(serverInfo, serverPing.getPlayers().getMax()));
            });

            return serverInfo1.getPlayers().size() + 1; // returns online + 1, just ignoring players.
        });
    }
}