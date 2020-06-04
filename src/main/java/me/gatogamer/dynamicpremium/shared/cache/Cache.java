package me.gatogamer.dynamicpremium.shared.cache;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cache {

    public long lastUsage = 0;

    //Spigot starts \\
    private boolean authenticated;
    //Spigot ends \\


    public Cache() {

    }

}
