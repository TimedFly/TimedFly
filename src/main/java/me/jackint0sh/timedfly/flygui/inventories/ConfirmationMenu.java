package me.jackint0sh.timedfly.flygui.inventories;

import me.jackint0sh.timedfly.flygui.FlyInventory;
import me.jackint0sh.timedfly.flygui.FlyItem;
import me.jackint0sh.timedfly.flygui.FlyItemCreator;
import me.jackint0sh.timedfly.flygui.Item;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public class ConfirmationMenu {

    private static FlyInventory inventory = new FlyInventory(InventoryType.HOPPER, MessageUtil.color("&cConfirm"));

    public static Inventory create(Player player, boolean b) {
        Item yes = new Item(Material.EMERALD).setName("&aYES")
                .onClick(event -> handleConfirmation(player, true));
        Item no = new Item(Material.REDSTONE).setName("&aNO")
                .onClick(event -> handleConfirmation(player, false));

        inventory.setItem(yes, 1);
        inventory.setItem(no, 3);

        if (b) player.openInventory(inventory.getInventory());

        return inventory.getInventory();
    }

    public static Inventory create(Player player) {
        return create(player, true);
    }

    private static void handleConfirmation(Player player, boolean b) {
        if (!b) {
            if (FlyItemCreator.getState(player) == FlyItemCreator.State.DELETE_ITEM) {
                handleDelete(player);
                FlyItemCreator.setMainState(player, FlyItemCreator.State.MAIN_MENU);
            }
            FlyItemCreator.openMenu(player);
            return;
        }
        if (FlyItemCreator.getInnerState(player) == FlyItemCreator.InnerState.SAVE_ITEM) {
            handleSave(player);
            MessageUtil.sendMessage(player, "Successfully saved!");
            FlyItemCreator.removeCurrentFlyItem(player);
            FlyItemCreator.clearStates(player);
            player.closeInventory();
        }
    }

    private static void handleSave(Player player) {
        FlyItem flyItem = FlyItemCreator.getCurrentFlyItem(player);
        Config config = Config.getConfig("items");
        FileConfiguration configuration = Config.getConfig("items").get();
        String key = "Items." + flyItem.getKey();

        configuration.set(key + ".Name", flyItem.getName());
        configuration.set(key + ".Slot", flyItem.getSlot());
        configuration.set(key + ".Material", flyItem.getMaterial());
        configuration.set(key + ".Data", flyItem.getData());
        configuration.set(key + ".Amount", flyItem.getAmount());
        configuration.set(key + ".Lore", flyItem.getLore());
        configuration.set(key + ".Hide_Attributes", flyItem.isHideAttributes());
        configuration.set(key + ".Hide_Enchants", flyItem.isHideEnchants());
        configuration.set(key + ".Hide_Place_On", flyItem.isHidePlaceOn());
        configuration.set(key + ".Hide_Potion_Effects", flyItem.isHidePotionEffects());
        configuration.set(key + ".Hide_Unbreakable", flyItem.isHideUnbreakable());
        configuration.set(key + ".Glow", flyItem.isGlow());
        configuration.set(key + ".UsePermission", flyItem.isUsePerms());
        configuration.set(key + ".Permission", flyItem.getPermission());
        configuration.set(key + ".PermissionMessage", flyItem.getPermissionMessage());
        configuration.set(key + ".OnClick", flyItem.getOnClick());
        configuration.set(key + ".Price", flyItem.getPrice());
        configuration.set(key + ".Time", flyItem.getTime());
        configuration.set(key + ".Cooldown", flyItem.getCooldown());

        FlyItem.setConfigItem(flyItem);

        try {
            config.reload();
        } catch (IOException e) {
            MessageUtil.sendError(player, e);
        }
    }

    private static void handleDelete(Player player) {
        FlyItem flyItem = FlyItemCreator.getCurrentFlyItem(player);
        FileConfiguration config = Config.getConfig("items").get();

        config.set(flyItem.getKey(), null);
    }
}
