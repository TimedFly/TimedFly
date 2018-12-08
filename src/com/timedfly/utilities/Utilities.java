package com.timedfly.utilities;

import com.timedfly.TimedFly;
import com.timedfly.configurations.ConfigCache;
import com.timedfly.managers.PlayerManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class Utilities {

    private Map<UUID, PlayerManager> playerManagerMap = new HashMap<>();

    public boolean isWorldEnabled(World playerWorld) {
        if (ConfigCache.getWorldListType().equalsIgnoreCase("enabled")) {
            List<String> ENABLED_WORLDS = ConfigCache.getWorldListWorlds();
            for (String worlds : ENABLED_WORLDS) {
                World world = Bukkit.getWorld(worlds);
                if (playerWorld == world)
                    return true;
            }
            return false;
        } else if (ConfigCache.getWorldListType().equalsIgnoreCase("disabled")) {
            List<String> DISABLED_WORLDS = ConfigCache.getWorldListWorlds();
            for (String worlds : DISABLED_WORLDS) {
                World world = Bukkit.getWorld(worlds);
                if (playerWorld == world)
                    return false;
            }
            return true;
        } else return ConfigCache.getWorldListType().equalsIgnoreCase("all");
    }

    public void runCommands(Player player, List<String> string) {
        for (String click : string) {
            if (click.contains("[message]")) {
                if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    String msgNoPAPI = Message.color(click.replace("[message] ", ""));
                    String msgPAPI = PlaceholderAPI.setPlaceholders(player, msgNoPAPI);
                    player.sendMessage(msgPAPI);
                } else {
                    player.sendMessage(Message.color(click.replace("[message] ", "")));
                }
            }
            if (click.contains("[player]")) {
                Bukkit.dispatchCommand(player, click.replace("[player] ", "").replace("%player%", player.getName()));
            }
            if (click.contains("[console]")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), click.replace("[console] ", "").replace("%player%", player.getName()));
            }
            if (click.contains("[sound]")) {
                if (ConfigCache.isSoundsEnabled()) {
                    player.playSound(player.getLocation(), Sound.valueOf(click.replace("[sound] ", "")), 100, 1);
                }
            }
            if (click.contains("[close]")) {
                player.closeInventory();
            }
        }
    }

    public PlayerManager getPlayerManager(UUID uuid) {
        PlayerManager playerManager = this.playerManagerMap.get(uuid);
        if (playerManager == null) addPlayerManager(uuid, TimedFly.getPlugin(TimedFly.class));
        Objects.requireNonNull(playerManager).setPlayer(Bukkit.getPlayer(uuid));
        return playerManager;
    }

    private void addPlayerManager(UUID uuid, Plugin plugin, int initialTime, int timeLeft) {
        PlayerManager playerManager = new PlayerManager(plugin, uuid, initialTime, timeLeft);
        playerManager.setInServer(true);
        this.playerManagerMap.put(uuid, playerManager);
    }

    public void addPlayerManager(UUID uuid, Plugin plugin) {
        addPlayerManager(uuid, plugin, 0, 0);
    }

    public void addPlayerManager(UUID uuid, Player player, Plugin plugin) {
        PlayerManager playerManager = new PlayerManager(plugin, player, uuid, 0, 0);
        playerManager.setInServer(true);
        this.playerManagerMap.put(uuid, playerManager);
    }

    public long getPlayersTimeLeft() {
        long time = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerManager playerManager = getPlayerManager(player.getUniqueId());
            if (playerManager.getTimeLeft() > 0) time += playerManager.getTimeLeft();
        }

        return time;
    }

    public int getPlayers() {
        int players = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerManager playerManager = getPlayerManager(player.getUniqueId());
            if (playerManager.getTimeLeft() > 0) players++;
        }

        return players;
    }
}
