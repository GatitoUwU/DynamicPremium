package me.gatogamer.dynamicpremium.shared.cache;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cache {

    // BungeeCord starts \\
    public long lastUsage;
    public Object event;
    public boolean premiumLauncher;
    // BungeeCord ends \\

    //Spigot starts \\
    private boolean authenticated;
    //Spigot ends \\



    public Cache() {
        lastUsage = 0;
        authenticated = false;
    }

}
