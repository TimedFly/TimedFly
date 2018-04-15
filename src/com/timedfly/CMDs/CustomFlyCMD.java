package com.timedfly.CMDs;

import com.timedfly.ConfigurationFiles.ConfigCache;
import com.timedfly.ConfigurationFiles.LangFiles;
import com.timedfly.Managers.MessageManager;
import com.timedfly.TimedFly;
import com.timedfly.Utilities.FlyGUI;
import com.timedfly.Utilities.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CustomFlyCMD
        implements Listener {
    private TimedFly plugin;
    private Utility utility;
    private ConfigCache configCache;
    private LangFiles lang = LangFiles.getInstance();

    public CustomFlyCMD(TimedFly plugin, ConfigCache configCache) {
        this.plugin = plugin;
        this.utility = plugin.getUtility();
        this.configCache = configCache;
    }

    @EventHandler
    public void openFlyMenu(PlayerCommandPreprocessEvent event) {
        FlyGUI gui = new FlyGUI(this.utility, this.configCache);
        Player player = event.getPlayer();
        String message = event.getMessage().replace("/", "");
        String[] args = message.split(" ");
        String[] commands = this.configCache.getOpenMenuCommand().split(";");
        if (event.isCancelled()) {
            return;
        }
        for (String command : commands)
            if (args[0].equalsIgnoreCase(command)) {
                if (!event.isCancelled()) {
                    event.setCancelled(true);
                }
                if (!this.utility.isWorldEnabled(player.getWorld())) {
                    this.utility.message(player, MessageManager.DISABLEDWORLD.toString());
                } else if (args.length == 2) {
                    switch (args[1]) {
                        case "stop":
                            player.performCommand("timedfly:tfly stop");
                            break;
                        case "resume":
                            player.performCommand("timedfly:tfly resume");
                            break;
                        default:
                            break;
                    }
                } else {
                    if ((this.configCache.isFlyModeIfHasPerm()) && (player.hasPermission("timedfly.fly.onoff"))) {
                        if (player.getAllowFlight()) {
                            player.setAllowFlight(false);
                            player.setFlying(false);
                            this.utility.message(player, this.lang.getLang().getString("Fly.Message.SetOff"));
                            continue;
                        }
                        if (!player.getAllowFlight()) {
                            player.setAllowFlight(true);
                            player.setFlying(true);
                            this.utility.message(player, this.lang.getLang().getString("Fly.Message.SetOn"));
                            continue;
                        }
                    }
                    if ((this.configCache.isUsePermission()) && (!player.hasPermission(this.configCache.getPermission()))) {
                        this.utility.message(player, MessageManager.NOPERM.toString());
                        this.plugin.getNMS().sendTitle(player, Utility.color(this.lang.getLang().getString("Other.NoPermission.Title")), 20, 40, 20);
                        this.plugin.getNMS().sendTitle(player, Utility.color(this.lang.getLang().getString("Other.NoPermission.SubTitle")), 20, 40, 20);
                    } else {
                        gui.flyGui(player);
                    }
                }
            }
    }
}