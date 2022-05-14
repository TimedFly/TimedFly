package me.jackint0sh.timedfly.listeners;

import me.jackint0sh.timedfly.flygui.FlyInventory;
import me.jackint0sh.timedfly.flygui.FlyItem;
import me.jackint0sh.timedfly.flygui.Item;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.function.Consumer;

public class InventoryListener implements Listener {

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        FlyInventory flyInventory = FlyInventory.getFlyInventory(event.getView().getTitle());
        if (flyInventory != null && event.getClickedInventory() != null && event.getClickedInventory().equals(flyInventory.getInventory())) {
            Item flyItem = flyInventory.getItem(event.getSlot());
            if (flyItem != null) {
                flyItem.callEvent(event);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        FlyInventory flyInventory = FlyInventory.getFlyInventory(event.getView().getTitle());
        if (flyInventory != null && event.getInventory().equals(flyInventory.getInventory())) {
            flyInventory.callEvent(event);
        }
    }

    public static void runCommands(FlyItem item, String type, HumanEntity player) {
        String CONSOLE = "[console] ", PLAYER = "[player] ", SOUND = "[sound] ";
        Consumer<String> consumer = command -> {
            if (command.startsWith(CONSOLE)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(CONSOLE.length()));
            } else if (command.startsWith(PLAYER)) {
                Bukkit.dispatchCommand(player, command.substring(PLAYER.length()));
            } else if (command.startsWith(SOUND)) {
                player.getWorld().playSound(player.getLocation(), command.substring(SOUND.length()), 5, 0);
            }
        };
        switch (type.toLowerCase()) {
            case "onClick":
                if (item.isEnableOnClick()) {
                    item.getOnClick().forEach(consumer);
                }
                break;
            case "onFlyDisable":
                if (item.isEnableOnFlyDisable()) {
                    item.getOnFlyDisable().forEach(consumer);
                }
                break;
        }
    }
}
