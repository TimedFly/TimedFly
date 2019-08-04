package me.jackint0sh.timedfly.commands;

import me.jackint0sh.timedfly.flygui.inventories.FlightStore;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.MessageUtil;
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

        String[] message = event.getMessage().split(" ");
        String command = message[0].substring(1);
        // String[] args = Arrays.copyOfRange(message, 1, message.length);
        List<String> commands = Config.getConfig("config").get().getStringList("CustomCommands.Commands");

        if (commands.stream().anyMatch(command::equals)) {
            final boolean userPerm = Config.getConfig("config").get().getBoolean("CustomCommands.UsePermission.Enable");
            final String perm = Config.getConfig("config").get().getString("CustomCommands.UsePermission.Permission");
            final Player player = event.getPlayer();

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
