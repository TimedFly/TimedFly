package me.jackint0sh.timedfly.hooks;

import me.jackint0sh.timedfly.hooks.currencies.PlayerPoints;
import me.jackint0sh.timedfly.hooks.currencies.Vault;
import me.jackint0sh.timedfly.managers.CurrencyManager;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Hooks {

    public static boolean isPluginEnabled(String pluginName) {
        PluginManager manager = Bukkit.getPluginManager();
        return manager.isPluginEnabled(pluginName)&& manager.getPlugin(pluginName) != null;
    }

    public static Plugin getPlugin(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName);
    }

    public static void hookVault() {
        if (isPluginEnabled("Vault")) {
            Economy econ = null;
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) econ = rsp.getProvider();
            if (econ == null) {
                MessageUtil.sendError("Something went wrong while hooking to Vault!");
            } else {
                CurrencyManager.addCurrency(new Vault(econ));
                MessageUtil.sendConsoleMessage("&cHooked to Vault!");
            }
        }
    }

    public static void hookPapi(Plugin plugin) {
        if (isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPI(plugin).register();
            MessageUtil.sendConsoleMessage("&cHooked to PlaceholderAPI!");
        }
    }

    public static void hookPlayerPoints() {
        if (isPluginEnabled("PlayerPoints")) {
            CurrencyManager.addCurrency(new PlayerPoints());
            MessageUtil.sendConsoleMessage("&cHooked to PlayerPoints!");
        }
    }

}
