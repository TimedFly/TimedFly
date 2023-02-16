package me.jackint0sh.timedfly.flygui.inventories;

import me.jackint0sh.timedfly.flygui.FlyInventory;
import me.jackint0sh.timedfly.flygui.FlyItem;
import me.jackint0sh.timedfly.flygui.FlyItemCreator;
import me.jackint0sh.timedfly.flygui.Item;
import me.jackint0sh.timedfly.utilities.Languages;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class ChangeItemMenu {

    private static FlyInventory inventory = new FlyInventory(InventoryType.HOPPER, MessageUtil.color("&cChoose Item"));

    public static Inventory create(Player player, boolean b) {
        AtomicBoolean cancelled = new AtomicBoolean(false);
        Item pane = new Item(Material.SIGN)
                .setName(Languages.getString("item_editor.close.title"))
                .setLore(Languages.getStringArray("item_editor.close.lore"));

        Item cancel = new Item(Material.REDSTONE_BLOCK)
                .setName(Languages.getString("item_editor.cancel.title"))
                .setLore(Languages.getStringArray("item_editor.cancel.lore"))
                .onClick(event -> {
                    cancelled.set(true);
                    FlyItemCreator.clearState(FlyItemCreator.StateType.INNER_STATE, player);
                    FlyItemCreator.openMenu(player);
                });

        inventory.setItem(pane, 0);
        inventory.setItem(pane, 1);
        inventory.setItem(pane, 3);
        inventory.setItem(cancel, 4);

        inventory.onClose(event -> {
            if (cancelled.get()) return;

            Inventory inventory = event.getView().getTopInventory();
            ItemStack item = inventory.getItem(2);
            if (item == null || item.getType().equals(Material.AIR)) return;

            FlyItem flyItem = FlyItemCreator.getCurrentFlyItem(player);
            String materialBefore = flyItem.getMaterial();
            flyItem.setMaterial(item.getType().name());
            FlyItemCreator.setCurrentFlyItem(player, flyItem);

            MessageUtil.sendTranslation(player, "item_editor.change_item.changed", new String[][]{
                    new String[]{"[old_material]", materialBefore},
                    new String[]{"[new_material]", flyItem.getMaterial()}
            });

            FlyItemCreator.clearState(FlyItemCreator.StateType.INNER_STATE, player);
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugins()[0],
                    () -> FlyItemCreator.openMenu(player), 1);
        });
        if (b) player.openInventory(inventory.getInventory());
        return inventory.getInventory();
    }

    public static Inventory create(Player player) {
        return create(player, true);
    }

}
