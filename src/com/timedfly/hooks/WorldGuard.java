package com.timedfly.hooks;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.timedfly.managers.PlayerManager;
import com.timedfly.utilities.Message;
import com.timedfly.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class WorldGuard implements Listener {

    private Utilities utilities;
    private final Flag MY_CUSTOM_FLAG = new StateFlag("timedfly-keep-fly", true);

    public WorldGuard(Utilities utilities) {
        this.utilities = utilities;
    }

    public void registerFlag() {
        WorldGuardPlugin worldGuardPlugin = getWorldGuard();

        if (worldGuardPlugin == null) return;

        FlagRegistry registry = worldGuardPlugin.getFlagRegistry();

        try {
            registry.register(MY_CUSTOM_FLAG);
        } catch (FlagConflictException e) {
            Message.sendConsoleMessage("&cThere was an error registering the flag &7" + MY_CUSTOM_FLAG);
            Message.sendConsoleMessage("&c" + e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void regionEnterEvent(RegionEnterEvent event) {
        if (!event.getRegion().getFlags().containsKey(MY_CUSTOM_FLAG) || !event.getRegion().getFlags().containsValue(MY_CUSTOM_FLAG)) {
            Player player = event.getPlayer();
            PlayerManager playerManager = utilities.getPlayerManager(player.getUniqueId());

            playerManager.stopTimedFly(false, true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void regionLeaveEvent(RegionLeaveEvent event) {
        Player player = event.getPlayer();
        PlayerManager playerManager = utilities.getPlayerManager(player.getUniqueId());

        if (!event.getRegion().getFlags().containsKey(MY_CUSTOM_FLAG) || !event.getRegion().getFlags().containsValue(MY_CUSTOM_FLAG)) {
            playerManager.startTimedFly();
        }
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

        if (!(plugin instanceof WorldGuardPlugin)) return null;

        return (WorldGuardPlugin) plugin;
    }
}
