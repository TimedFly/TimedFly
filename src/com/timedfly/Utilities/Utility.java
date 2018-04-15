package com.timedfly.Utilities;

import com.timedfly.ConfigurationFiles.ConfigCache;
import com.timedfly.ConfigurationFiles.LangFiles;
import com.timedfly.Managers.MySQLManager;
import com.timedfly.Managers.TimeFormat;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Utility {

    private ConfigCache configCache;
    private Map<UUID, PlayerCache> playerCacheMap = new HashMap<>();

    public Utility(ConfigCache configCache) {
        this.configCache = configCache;
    }

    public boolean isWorldEnabled(World playerWorld) {

        if (configCache.getWorldListType().equalsIgnoreCase("enabled")) {
            List<String> ENABLED_WORLDS = configCache.getWorldListWorlds();
            for (String worlds : ENABLED_WORLDS) {
                World world = Bukkit.getWorld(worlds);
                if (playerWorld == world)
                    return true;
            }
            return false;
        } else if (configCache.getWorldListType().equalsIgnoreCase("disabled")) {
            List<String> DISABLED_WORLDS = configCache.getWorldListWorlds();
            for (String worlds : DISABLED_WORLDS) {
                World world = Bukkit.getWorld(worlds);
                if (playerWorld == world)
                    return false;
            }
            return true;
        } else return configCache.getWorldListType().equalsIgnoreCase("all");
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void message(Player sender, String string) {
        String prefix = ChatColor.translateAlternateColorCodes('&', configCache.getPrefix());
        if (prefix.equalsIgnoreCase("none")) {
            sender.sendMessage(color(string));
            return;
        }
        sender.sendMessage(prefix + color(string));
    }

    public void message(CommandSender sender, String string) {
        String prefix = ChatColor.translateAlternateColorCodes('&', configCache.getPrefix());
        if (prefix.equalsIgnoreCase("none")) {
            sender.sendMessage(color(string));
        } else {
            sender.sendMessage(prefix + color(string));
        }
    }

    public void clickEvent(Player player, List<String> string) {
        for (String click : string) {
            if (click.contains("[message]")) {
                if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    String msgNoPAPI = ChatColor.translateAlternateColorCodes('&', click.replace("[message] ", ""));
                    String msgPAPI = PlaceholderAPI.setPlaceholders(player, msgNoPAPI);
                    player.sendMessage(msgPAPI);
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', click.replace("[message] ", "")));
                }
            }
            if (click.contains("[player]")) {
                Bukkit.dispatchCommand(player, click.replace("[player] ", "").replace("%player%", player.getName()));
            }
            if (click.contains("[console]")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), click.replace("[console] ", "").replace("%player%", player.getName()));
            }
            if (click.contains("[sound]")) {
                if (configCache.isSoundsEnabled()) {
                    player.playSound(player.getLocation(), Sound.valueOf(click.replace("[sound] ", "")), 100, 1);
                }
            }
            if (click.contains("[close]")) {
                player.closeInventory();
            }
        }
    }

    public boolean isTokenManagerEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("TokenManager");
    }

    public boolean isPapiEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public Map<UUID, PlayerCache> getPlayerCacheMap() {
        return playerCacheMap;
    }

    public void addPlayerCache(Player player, MySQLManager sqlManager) {
        int initialTime = sqlManager.getInitialTime(player);

        String title = color(LangFiles.getInstance().getLang().getString("Fly.BossBar")
                .replace("%timeleft%", TimeFormat.format(initialTime)));

        getPlayerCacheMap().put(player.getUniqueId(), new PlayerCache(sqlManager, player,
                new BossBarManager(player, title, configCache.getBossBarTimerColor(), configCache.getBossBarTimerStyle(), initialTime)));
    }

    public PlayerCache getPlayerCache(Player player) {
        return getPlayerCacheMap().get(player.getUniqueId());
    }

    public Long timeToSeconds(String timeFormat) {
        String[] timeBefore = timeFormat.split(" ");
        long time = 0;

        for (String timeString : timeBefore) {
            if (timeString.contains("s")) {
                time += Long.parseLong(timeString.replaceAll("[a-zA-Z]", ""));
            }
            if (timeString.contains("m")) {
                time += Long.parseLong(timeString.replaceAll("[a-zA-Z]", "")) * 60;
            }
            if (timeString.contains("h")) {
                time += Long.parseLong(timeString.replaceAll("[a-zA-Z]", "")) * 3600;
            }
            if (timeString.contains("d")) {
                time += Long.parseLong(timeString.replaceAll("[a-zA-Z]", "")) * 86400;
            }
        }
        return time;
    }

}
