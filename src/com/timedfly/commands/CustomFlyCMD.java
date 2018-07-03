package com.timedfly.commands;

import com.timedfly.NMS.NMS;
import com.timedfly.configurations.ConfigCache;
import com.timedfly.configurations.Languages;
import com.timedfly.utilities.FlyGUI;
import com.timedfly.utilities.Message;
import com.timedfly.utilities.Utilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CustomFlyCMD implements Listener {

    private Utilities utility;
    private Languages languages;
    private FlyGUI flyGUI;
    private NMS nms;

    public CustomFlyCMD(Utilities utility, Languages languages, FlyGUI flyGUI, NMS nms) {
        this.utility = utility;
        this.languages = languages;
        this.flyGUI = flyGUI;
        this.nms = nms;
    }

    @EventHandler
    public void openFlyMenu(PlayerCommandPreprocessEvent event) {
        if (!ConfigCache.isGuiEnable()) return;

        Player player = event.getPlayer();
        String message = event.getMessage().replace("/", "");
        String[] args = message.split(" ");
        String[] commands = ConfigCache.getOpenMenuCommand().split(";");

        if (event.isCancelled()) return;

        for (String command : commands)
            if (args[0].equalsIgnoreCase(command)) {
                FileConfiguration languageConfig = languages.getLanguageFile();
                if (!event.isCancelled()) event.setCancelled(true);

                if (!this.utility.isWorldEnabled(player.getWorld())) {
                    Message.sendDisabledWorld(player, languageConfig);
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
                    if (ConfigCache.isFlyModeIfHasPerm() && (player.hasPermission("timedfly.fly.onoff"))) {
                        if (player.getAllowFlight()) {
                            player.setAllowFlight(false);
                            player.setFlying(false);
                            Message.sendMessage(player, languageConfig.getString("Fly.Message.SetOff"));
                            continue;
                        }
                        if (!player.getAllowFlight()) {
                            player.setAllowFlight(true);
                            player.setFlying(true);
                            Message.sendMessage(player, languageConfig.getString("Fly.Message.SetOn"));
                            continue;
                        }
                    }
                    if (ConfigCache.isUsePermission() && !player.hasPermission(ConfigCache.getPermission()))
                        Message.sendNoPermission(player, languageConfig, nms);
                    else flyGUI.openGui(player);
                }
            }
    }
}