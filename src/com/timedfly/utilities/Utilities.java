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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Utilities {
    private Map<UUID, PlayerManager> playerManagerMap = new HashMap<>();

    public Utilities() {
    }

    public boolean isWorldEnabled(World playerWorld) {
        List DISABLED_WORLDS;
        Iterator var3;
        String worlds;
        World world;
        if (ConfigCache.getWorldListType().equalsIgnoreCase("enabled")) {
            DISABLED_WORLDS = ConfigCache.getWorldListWorlds();
            var3 = DISABLED_WORLDS.iterator();

            do {
                if (!var3.hasNext()) {
                    return false;
                }

                worlds = (String) var3.next();
                world = Bukkit.getWorld(worlds);
            } while (playerWorld != world);

            return true;
        } else if (ConfigCache.getWorldListType().equalsIgnoreCase("disabled")) {
            DISABLED_WORLDS = ConfigCache.getWorldListWorlds();
            var3 = DISABLED_WORLDS.iterator();

            do {
                if (!var3.hasNext()) {
                    return true;
                }

                worlds = (String) var3.next();
                world = Bukkit.getWorld(worlds);
            } while (playerWorld != world);

            return false;
        } else {
            return ConfigCache.getWorldListType().equalsIgnoreCase("all");
        }
    }

    public void runCommands(Player player, List<String> strings) {

        for (String click : strings) {
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

            if (click.contains("[sound]") && ConfigCache.isSoundsEnabled()) {
                player.playSound(player.getLocation(), Sound.valueOf(click.replace("[sound] ", "")), 100.0F, 1.0F);
            }

            if (click.contains("[close]")) {
                player.closeInventory();
            }
        }

    }

    public PlayerManager getPlayerManager(UUID uuid) {
        PlayerManager playerManager = this.playerManagerMap.get(uuid);
        if (playerManager == null) {
            this.addPlayerManager(uuid, TimedFly.getPlugin(TimedFly.class));
            playerManager = playerManagerMap.get(uuid);
        }

        Objects.requireNonNull(playerManager).setPlayer(Bukkit.getPlayer(uuid));
        return playerManager;
    }

    private void addPlayerManager(UUID uuid, Plugin plugin, int initialTime, int timeLeft) {
        PlayerManager playerManager = new PlayerManager(plugin, uuid, initialTime, timeLeft);
        playerManager.setInServer(true);
        this.playerManagerMap.put(uuid, playerManager);
    }

    public void addPlayerManager(UUID uuid, Plugin plugin) {
        this.addPlayerManager(uuid, plugin, 0, 0);
    }

    public void addPlayerManager(UUID uuid, Player player, Plugin plugin) {
        PlayerManager playerManager = new PlayerManager(plugin, player, uuid, 0, 0);
        playerManager.setInServer(true);
        this.playerManagerMap.put(uuid, playerManager);
    }

    public long getPlayersTimeLeft() {
        long time = 0L;
        Iterator var3 = Bukkit.getOnlinePlayers().iterator();

        while (var3.hasNext()) {
            Player player = (Player) var3.next();
            PlayerManager playerManager = this.getPlayerManager(player.getUniqueId());
            if (playerManager.getTimeLeft() > 0) {
                time += (long) playerManager.getTimeLeft();
            }
        }

        return time;
    }

    public int getPlayers() {
        int players = 0;
        Iterator var2 = Bukkit.getOnlinePlayers().iterator();

        while (var2.hasNext()) {
            Player player = (Player) var2.next();
            PlayerManager playerManager = this.getPlayerManager(player.getUniqueId());
            if (playerManager.getTimeLeft() > 0) {
                ++players;
            }
        }

        return players;
    }
}
