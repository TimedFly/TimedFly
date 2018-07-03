package com.timedfly.managers;

import com.timedfly.TimedFly;
import com.timedfly.configurations.ConfigCache;
import com.timedfly.configurations.Languages;
import com.timedfly.hooks.Metrics;
import com.timedfly.hooks.aSkyblock;
import com.timedfly.utilities.Message;
import com.timedfly.utilities.Utilities;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HooksManager {

    public static void setupHooks(TimedFly plugin, Languages languages, Utilities utilities) {
        if (isPapiEnabled()) {
            new com.timedfly.hooks.PlaceholderAPI(plugin, languages, utilities).hook();
            Message.sendConsoleMessage("&7Hooking into PlaceholderAPI");
        }
        if (ConfigCache.isaSkyblockIntegration() && isASkyBlockEnabled()) {
            Bukkit.getPluginManager().registerEvents(new aSkyblock(utilities), plugin);
            Message.sendConsoleMessage("&7Hooking into aSkyblock");
        }
        /*if (isWorldGuardEnabled()) {
            WorldGuard worldGuard = new WorldGuard(utilities);

            Bukkit.getServer().getPluginManager().registerEvents(worldGuard, plugin);
            worldGuard.registerFlag();
            Message.sendConsoleMessage("&7Hooking into WorldGuard");
        }*/
        if (isTokenManagerEnabled()) {
            Message.sendConsoleMessage("&7Hooking into TokensManager");
        }

        Metrics metrics = new Metrics(plugin);
        metrics.addCustomChart(new Metrics.MultiLineChart("players_and_servers", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            return valueMap;
        }));
    }

    public static String setPlaceHolders(String text, Player player) {
        if (isPapiEnabled())
            return PlaceholderAPI.setPlaceholders(player, text);
        else return Message.color(text);
    }

    public static boolean isPapiEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public static boolean isWorldGuardEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
    }

    public static boolean isASkyBlockEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("ASkyBlock");
    }

    public static boolean isTokenManagerEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("TokenManager");
    }

}
