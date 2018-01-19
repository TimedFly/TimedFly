package com.minestom.CMDs;

import com.minestom.ConfigurationFiles.LangFiles;
import com.minestom.Managers.MessageManager;
import com.minestom.TimedFly;
import com.minestom.Utilities.GUI.FlyGUI;
import com.minestom.Utilities.Utility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CustomFlyCMD implements Listener {

    private TimedFly plugin = TimedFly.getInstance();
    private Utility utility = new Utility(plugin);
    private LangFiles lang = LangFiles.getInstance();

    @EventHandler
    public void openFlyMenu(PlayerCommandPreprocessEvent event) {
        FlyGUI gui = new FlyGUI();
        Player player = event.getPlayer();
        String message = event.getMessage().replace("/", "");
        String[] args = message.split(" ");
        String[] commands = plugin.getConfig().getString("OpenMenuCommand").split(";");
        if (event.isCancelled()) {
            return;
        }
        for (String command : commands) {
            if (args[0].equalsIgnoreCase(command)) {
                if (!event.isCancelled()) {
                    event.setCancelled(true);
                }
                if (!utility.isWorldEnabled(player, player.getWorld())) {
                    utility.message(player, MessageManager.DISABLEDWORLD.toString());
                    continue;
                }
                if (args.length == 2) {
                    switch (args[1]) {
                        case "stop":
                            player.performCommand("timedfly:tfly stop");
                            continue;
                        case "resume":
                            player.performCommand("timedfly:tfly resume");
                            continue;
                        default:
                            continue;
                    }
                }
                if (plugin.getConfig().getBoolean("FlyModeIfHasPerm") && player.hasPermission("timedfly.fly.onof")) {
                    if (player.getAllowFlight()) {
                        player.setAllowFlight(false);
                        player.setFlying(false);
                        utility.message(player, lang.getLang().getString("Fly.Message.SetOff"));
                        continue;
                    } else if (!player.getAllowFlight()) {
                        player.setAllowFlight(true);
                        player.setFlying(true);
                        utility.message(player, lang.getLang().getString("Fly.Message.SetOn"));
                        continue;
                    }
                }
                if (plugin.getConfig().getBoolean("UsePermission.Use") && !player.hasPermission(plugin.getConfig().getString("UsePermission.Permission"))) {
                    utility.message(player, MessageManager.NOPERM.toString());
                    plugin.getNMS().sendTitle(player, utility.color(lang.getLang().getString("Other.NoPermission.Title")), 20, 40, 20);
                    plugin.getNMS().sendTitle(player, utility.color(lang.getLang().getString("Other.NoPermission.SubTitle")), 20, 40, 20);
                    continue;
                }
                gui.flyGui(player);
            }
        }
    }
}
