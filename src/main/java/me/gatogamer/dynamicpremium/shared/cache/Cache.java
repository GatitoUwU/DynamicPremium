package me.gatogamer.dynamicpremium.shared.cache;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cache {

    public long lastUsage;

    //Spigot starts \\
    private boolean authenticated;
    //Spigot ends \\


    public Cache() {
        lastUsage = 0;
        authenticated = false;
    }

}
