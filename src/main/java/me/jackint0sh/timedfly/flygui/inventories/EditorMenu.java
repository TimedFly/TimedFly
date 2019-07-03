package me.jackint0sh.timedfly.flygui.inventories;

import me.jackint0sh.timedfly.flygui.FlyInventory;
import me.jackint0sh.timedfly.flygui.FlyItem;
import me.jackint0sh.timedfly.flygui.FlyItemCreator;
import me.jackint0sh.timedfly.flygui.Item;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EditorMenu {

    private static FlyInventory inventory = new FlyInventory(1, MessageUtil.color("&cFlyItem Editor - Creator"));

    public static Inventory create(Player player, boolean b) {
        FlyItem flyItem = FlyItemCreator.getCurrentFlyItem(player);
        Item item = new Item(flyItem);

        String name = !flyItem.getName().isEmpty() ? flyItem.getName() : "None";

        Item material = new Item(Material.GRASS_BLOCK)
                .onClick(event -> {
                    FlyItemCreator.setInnerState(player, FlyItemCreator.InnerState.CHANGE_ITEM);
                    ChangeItemMenu.create(player);
                })
                .setName("&aChange Item")
                .setLore("&7", "&eClick here to", "&echange the Item.", "&7", "&fCurrent: &7" + flyItem.getMaterial());
        Item title = new Item(Material.PAPER)
                .setName("&aChange Title")
                .setLore("&7", "&eClick here and type the", "&etitle in the chat", "&eto chage it.", "&7", "&fCurrent: &7" + name)
                .onClick(event -> {
                    MessageUtil.sendMessage(player, "&aEnter the name in the chat. &7Type &ecancel &7 to go back.");
                    FlyItemCreator.setInnerState(player, FlyItemCreator.InnerState.CHANGE_NAME);
                    player.closeInventory();
                });
        Item lore = new Item(Material.FEATHER).onLeftClick(event -> {
            MessageUtil.sendMessage(player, "&aEnter the line in the chat. &7Type &ecancel &7 to go back.");
            MessageUtil.sendMessage(player, "&aPRO TIP: &7Use --at:<number here> to insert the line at the location provided.");
            FlyItemCreator.setInnerState(player, FlyItemCreator.InnerState.ADD_LORE_LINE);
            player.closeInventory();
        }).onRightClick(event -> {
            item.removeLastLoreLine();
            update(player);
            flyItem.setLore(item.getLore());
        }).onShiftRightClick(event -> {
            item.removeLore();
            update(player);
            flyItem.setLore(item.getLore());
        }).onShiftLeftClick(event -> {
            MessageUtil.sendMessage(player, "&aEnter the line number you want to delete. &7Type &ecancel &7 to go back.");
            FlyItemCreator.setInnerState(player, FlyItemCreator.InnerState.REMOVE_LORE_LINE);
            player.closeInventory();
        });
        Item price = new Item(Material.GOLD_INGOT)
                .setName("&aChange Price")
                .setLore("&7", "&eClick here and type the", "&eprice in the chat.", "&7", "&fCurrent: &7" + flyItem.getPrice())
                .onClick(event -> {
                    MessageUtil.sendMessage(player, "&aEnter the price in the chat. &7Type &ecancel &7 to go back.");
                    FlyItemCreator.setInnerState(player, FlyItemCreator.InnerState.CHANGE_PRICE);
                    player.closeInventory();
                });
        Item time = new Item(Material.CLOCK)
                .setName("&aChange Time")
                .setLore("&7", "&eClick here and type the", "&etime in the chat.", "&7", "&fCurrent: &7" + flyItem.getTime())
                .onClick(event -> {
                    MessageUtil.sendMessage(player, "&aEnter the time in the chat. &7Type &ecancel &7 to go back.");
                    FlyItemCreator.setInnerState(player, FlyItemCreator.InnerState.CHANGE_TIME);
                    player.closeInventory();
                });
        Item options = new Item(Material.REDSTONE_TORCH)
                .setName("&aExtra Options")
                .setLore("&7", "&eClick here to edit", "&eextra options.", "&7")
                .onClick(event -> {
                    FlyItemCreator.setInnerState(player, FlyItemCreator.InnerState.CHANGE_OPTIONS);
                    OptionsMenu.create(player);
                });
        Item view = new Item(flyItem);
        Item save = new Item(Material.EMERALD_BLOCK)
                .setName("&aSave")
                .setLore("&7", "&eClick here to save this item.", "&7")
                .onClick(e -> {
                    FlyItemCreator.setInnerState(player, FlyItemCreator.InnerState.SAVE_ITEM);
                    ConfirmationMenu.create(player);
                });
        Item back = new Item(Material.ARROW)
                .setName("&aBack")
                .setLore("&7", "&eClick here to cancel and go back", "&7")
                .onClick(event -> {
                    FlyItemCreator.clearState(FlyItemCreator.StateType.INNER_STATE, player);
                    FlyItem.removeConfigItem(flyItem.getKey());
                    FlyItemCreator.removeCurrentFlyItem(player);
                    FlyItemCreator.setMainState(player, FlyItemCreator.State.MAIN_MENU);
                    FlyItemCreator.openMenu(player);
                });

        List<String> currentLore = new ArrayList<>();
        List<String> itemLore = new ArrayList<>(Arrays.asList("&7", "&eLeft-Click: &7Add line.", "&eRight-Click: &7Remove last line.",
                "&eShift-Right-Click: &7Remove All lines", "&eShift-Left-Click: &7Remove desire line", "&7",
                "&fCurrent: ", "&7None"));
        if (item.getLore().size() > 0) {
            itemLore.remove(itemLore.size() - 1);
            currentLore = item.getLore().stream().map(string -> "&7- " + string).collect(Collectors.toList());
            itemLore.addAll(currentLore);
        }
        lore.setName("&aChange Lore").setLore(itemLore);

        List<String> viewLore = new ArrayList<>(Arrays.asList("&7",
                "&fTitle: &7" + flyItem.getName(),
                "&fMaterial: &7" + flyItem.getMaterial(),
                "&fData: &7" + flyItem.getData(),
                "&fAmount: &7" + flyItem.getAmount(),
                "&fCooldown: &7" + flyItem.getCooldown(),
                "&fPrice: &7" + flyItem.getPrice(),
                "&fTime: &7" + flyItem.getTime(),
                "&fGlow: &7" + boolToString(flyItem.isGlow()),
                "&fHide Attributes: &7" + boolToString(flyItem.isHideAttributes()),
                "&fHide Enchants: &7" + boolToString(flyItem.isHideEnchants()),
                "&fHide Place On: &7" + boolToString(flyItem.isHidePlaceOn()),
                "&fHide Potion Effects: &7" + boolToString(flyItem.isHidePotionEffects()),
                "&fHide Unbreakable: &7" + boolToString(flyItem.isHideUnbreakable()),
                "&fPermission Only: &7" + boolToString(flyItem.isUsePerms()),
                "&fPermission: &7" + flyItem.getPermission(),
                "&fPermission Message: &7" + flyItem.getPermissionMessage(),
                "&fOn Click: &7",
                "&7  None",
                "&fLore: &7",
                "&7  None",
                "&7"));

        if (item.getLore().size() > 0) {
            viewLore.remove(viewLore.size() - 2);
            viewLore.addAll(viewLore.size() - 1, currentLore);
        }
        view.setName("&aPreview").setLore(viewLore).glow(flyItem.isGlow());

        flyItem.setLore(item.getLore());

        inventory.setItems(material, title, lore, price, time, options, view, save, back);

        if (b) player.openInventory(inventory.getInventory());
        return inventory.getInventory();
    }

    public static Inventory create(Player player) {
        return create(player, true);
    }

    private static void update(Player player) {
        create(player, false);
        player.getOpenInventory().getTopInventory().setContents(inventory.getInventory().getContents());
    }

    private static String boolToString(boolean b) {
        return b ? "Yes" : "No";
    }
}
