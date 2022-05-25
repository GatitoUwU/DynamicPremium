package me.gatogamer.dynamicpremium.commons.cache;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class Cache {
    private final String username;
    private long lastConnection;

    // BungeeCord starts \\
    private long lastUsage = 0L;
    private boolean pendingVerification = false;
    private boolean onVerification = false;
    private boolean premium = false;
    // BungeeCord ends \\

    //Spigot starts \\
    private boolean authenticated = false;
    private boolean checkedIfPremium = false;
    //Spigot ends \\

    public Cache(String username) {
        this.username = username;
        this.lastConnection = System.currentTimeMillis();
    }

    public void updateUsage() {
        lastConnection = System.currentTimeMillis();
    }

    public boolean shouldPurge() {
        return System.currentTimeMillis() - lastConnection > 60000L;
    }
}