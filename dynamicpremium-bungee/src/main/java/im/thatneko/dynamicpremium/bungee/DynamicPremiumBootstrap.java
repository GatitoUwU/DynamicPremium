package im.thatneko.dynamicpremium.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public final class DynamicPremiumBootstrap extends Plugin {
    @Override
    public void onEnable() {
        new DynamicPremium(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
