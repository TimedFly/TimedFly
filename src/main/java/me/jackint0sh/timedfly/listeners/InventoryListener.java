package me.jackint0sh.timedfly.listeners;

import me.jackint0sh.timedfly.flygui.FlyInventory;
import me.jackint0sh.timedfly.flygui.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

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

}
