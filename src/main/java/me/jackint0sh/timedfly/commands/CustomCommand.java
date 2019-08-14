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

            if (PlayerManager.hasPermission(player, Permissions.SKIP_STORE)) {
                if (!player.getAllowFlight()) {
                    player.setAllowFlight(true);
                    MessageUtil.sendTranslation(player, "fly.on");
                } else {
                    player.setAllowFlight(false);
                    MessageUtil.sendTranslation(player, "fly.off");
                }
                playerManager.setManualFly(player.getAllowFlight());
                event.setCancelled(true);
                return;
            }

            if (!Config.getConfig("config").get().getBoolean("Gui.Enable")) return;

            final String perm = Config.getConfig("config").get().getString("CustomCommands.UsePermission.Permission");
            final boolean userPerm = Config.getConfig("config").get().getBoolean("CustomCommands.UsePermission.Enable");

            if (userPerm && !PlayerManager.hasPermission(player, perm)) {
                MessageUtil.sendNoPermission(player);
                event.setCancelled(true);
                return;
            }

            FlightStore.create(player);
            event.setCancelled(true);
        }
    }
}
