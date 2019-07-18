package me.jackint0sh.timedfly.flygui.inventories;

import me.jackint0sh.timedfly.flygui.FlyInventory;
import me.jackint0sh.timedfly.flygui.FlyItem;
import me.jackint0sh.timedfly.flygui.FlyItemCreator;
import me.jackint0sh.timedfly.flygui.Item;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class MainMenu {

    private static FlyInventory inventory = new FlyInventory(InventoryType.HOPPER, MessageUtil.color("&cFlyItem Editor - Main Menu"));

    public static Inventory create(Player player, boolean b) {
        FlyItemCreator.setMainState(player, FlyItemCreator.State.MAIN_MENU);

        Item create = new Item(Material.ANVIL)
                .setName("&aCreate Item")
                .setLore("&7", "&eClick here to create a", "&enew Item.", "&7")
                .onClick(event -> {
                    FlyItemCreator.setMainState(player, FlyItemCreator.State.CREATE_ITEM);
                    FlyItemCreator.setCurrentFlyItem(player, new FlyItem().setMaterial(Material.BOOK.name()));
                    EditorMenu.create(player);
                });
        Item edit = new Item(Material.PAPER)
                .setName("&6Edit Item")
                .setLore("&7", "&eClick here to edit a", "&ecurrent Item.", "&7")
                .onClick(event -> {
                    FlyItemCreator.setMainState(player, FlyItemCreator.State.EDIT_ITEM);
                    FlightStore.createEdit(player);
                });
        Item delete = new Item(Material.BARRIER)
                .setName("&cDelete Item")
                .setLore("&7", "&eClick here to delete a", "&ecurrent Item.", "&7")
                .onClick(event -> {
                    FlyItemCreator.setMainState(player, FlyItemCreator.State.DELETE_ITEM);
                    FlightStore.createDelete(player);
                });

        inventory.setItem(create, 0);
        inventory.setItem(edit, 2);
        inventory.setItem(delete, 4);

        if (b) player.openInventory(inventory.getInventory());
        return inventory.getInventory();
    }

    public static Inventory create(Player player) {
        return create(player, true);
    }

}
