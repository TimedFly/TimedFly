package me.jackint0sh.timedfly.commands;

import me.jackint0sh.timedfly.flygui.inventories.FlightStore;
import me.jackint0sh.timedfly.managers.PlayerManager;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import me.jackint0sh.timedfly.utilities.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CustomCommand implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) return;
        if (!Config.getConfig("config").get().getBoolean("CustomCommands.Enable")) return;

        String[] message = event.getMessage().split(" ");
        String command = message[0].substring(1);
        // String[] args = Arrays.copyOfRange(message, 1, message.length);
        List<String> commands = Config.getConfig("config").get().getStringList("CustomCommands.Commands");

        if (commands.stream().anyMatch(command::equals)) {
            final Player player = event.getPlayer();
            final PlayerManager playerManager = PlayerManager.getCachedPlayer(event.getPlayer().getUniqueId());
            final boolean userPerm = Config.getConfig("config").get().getBoolean("CustomCommands.UsePermission.Enable");
            final String perm = Config.getConfig("config").get().getString("CustomCommands.UsePermission.Permission");

            if (PlayerManager.hasPermission(player, Permissions.SKIP_STORE)) {
                if (!player.getAllowFlight()) {
                    player.setAllowFlight(true);
                    MessageUtil.sendMessage(player, "&7Flight &aenabled.");
                } else {
                    player.setAllowFlight(false);
                    MessageUtil.sendMessage(player, "&7Flight &cdisabled.");
                }
                playerManager.setManualFly(player.getAllowFlight());
                event.setCancelled(true);
                return;
            }

            if (userPerm && !player.hasPermission(perm)) {
                MessageUtil.sendNoPermission(player);
                event.setCancelled(true);
                return;
            }

            FlightStore.create(player);
            event.setCancelled(true);
        }
    }

}
