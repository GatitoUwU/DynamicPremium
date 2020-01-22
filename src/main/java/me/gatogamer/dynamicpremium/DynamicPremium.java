package me.gatogamer.dynamicpremium;

import me.gatogamer.dynamicpremium.commands.PremiumCommand;
import me.gatogamer.dynamicpremium.config.ConfigCreator;
import me.gatogamer.dynamicpremium.config.ConfigUtils;
import me.gatogamer.dynamicpremium.listeners.Listeners;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public final class DynamicPremium extends Plugin {

    private ConfigUtils configUtils;
    private static DynamicPremium instance;

    @Override
    public void onEnable() {
        setInstance(this);

        ProxyServer.getInstance().getConsole().sendMessage(c("&cLoading DynamicPremium by gatogamer#1111"));

        ProxyServer.getInstance().getConsole().sendMessage(c("&cLoading configs by iSnakeBuzz_"));
        ConfigCreator.get().setupBungee(this, "PremiumUsers");
        ConfigCreator.get().setupBungee(this, "Settings");

        ProxyServer.getInstance().getConsole().sendMessage(c("&cLoading Commands"));
        getProxy().getPluginManager().registerCommand(this, new PremiumCommand("premium"));
        ProxyServer.getInstance().getConsole().sendMessage(c("&cCommands loaded"));

        setConfigUtils(new ConfigUtils());

        ProxyServer.getInstance().getConsole().sendMessage(c("&cLoading Listeners"));
        getProxy().getPluginManager().registerListener(this, new Listeners());
        ProxyServer.getInstance().getConsole().sendMessage(c("&cListeners loaded"));
        ProxyServer.getInstance().getConsole().sendMessage(c("&cDynamicPremium has been loaded"));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static boolean isPlayerPremiumEnabled (String name) {
        Configuration premiumUsers = DynamicPremium.getInstance().getConfigUtils().getConfig(instance, "PremiumUsers");
        return premiumUsers.getStringList("PremiumUsers").contains(name);
    }

    public void setConfigUtils(ConfigUtils configUtils) {
        this.configUtils = configUtils;
    }

    public ConfigUtils getConfigUtils() {
        return configUtils;
    }

    public static void setInstance(DynamicPremium instance) {
        DynamicPremium.instance = instance;
    }

    public static DynamicPremium getInstance() {
        return instance;
    }

    public String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }
}
