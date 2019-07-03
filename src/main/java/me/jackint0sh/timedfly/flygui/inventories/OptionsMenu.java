package me.jackint0sh.timedfly.flygui.inventories;

import me.jackint0sh.timedfly.flygui.FlyInventory;
import me.jackint0sh.timedfly.flygui.FlyItem;
import me.jackint0sh.timedfly.flygui.FlyItemCreator;
import me.jackint0sh.timedfly.flygui.Item;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OptionsMenu {
    private static FlyInventory inventory = new FlyInventory(5, MessageUtil.color("&cFlyItem Editor - Options"));

    public static Inventory create(Player player, boolean b) {
        FlyItem flyItem = FlyItemCreator.getCurrentFlyItem(player);

        Item glow = new Item(Material.GLOWSTONE)
                .setName("&bGlow")
                .toggle(flyItem.isGlow())
                .glow(true);
        createOption(player, 0, glow, event -> flyItem.setGlow(glow.toggled()));

        Item permOnly = new Item(Material.COMMAND_BLOCK)
                .setName("&bPerms Only")
                .toggle(flyItem.isUsePerms());
        createOption(player, 1, permOnly, event -> flyItem.setUsePerms(permOnly.toggled()));

        Item hideAtt = new Item(Material.DIAMOND_SWORD)
                .setName("&bHide Attributes")
                .toggle(flyItem.isHideAttributes())
                .addItemFlags(ItemFlag.values());
        createOption(player, 2, hideAtt, event -> flyItem.setHideAttributes(hideAtt.toggled()));

        Item hideEnch = new Item(Material.ENCHANTING_TABLE)
                .setName("&bHide Enchants")
                .toggle(flyItem.isHideEnchants());
        createOption(player, 3, hideEnch, event -> flyItem.setHideEnchants(hideEnch.toggled()));

        Item hidePlace = new Item(Material.GRASS_BLOCK)
                .setName("&bHide Place On")
                .toggle(flyItem.isHidePlaceOn());
        createOption(player, 4, hidePlace, event -> flyItem.setHidePlaceOn(hidePlace.toggled()));

        Item hidePotion = new Item(Material.POTION)
                .setName("&bHide Potion Effects")
                .toggle(flyItem.isHidePotionEffects())
                .addItemFlags(ItemFlag.values());
        createOption(player, 5, hidePotion, event -> flyItem.setHidePotionEffects(hidePotion.toggled()));

        Item hideUnbreak = new Item(Material.DIAMOND_PICKAXE)
                .setName("&bHide Unbreakable")
                .toggle(flyItem.isHideUnbreakable())
                .addItemFlags(ItemFlag.values());
        createOption(player, 6, hideUnbreak, event -> flyItem.setHideUnbreakable(hideUnbreak.toggled()));

        Item permission = new Item(Material.MAP)
                .setName("&bPermission")
                .setLore("&7", "&eChange permission", "&eto use this item.", "&7", "&fCurrent: &7" + flyItem.getPermission(), "&7")
                .onClick(event -> {
                    MessageUtil.sendMessage(player, "&aEnter the permission in the chat. &7Type &ecancel &7 to go back.");
                    FlyItemCreator.setOptionState(player, FlyItemCreator.OptionState.PERM);
                    player.closeInventory();
                });

        Item permissionMessage = new Item(Material.FEATHER)
                .setName("&bPermission Message")
                .setLore("&7", "&eChange the permission", "&emessage of this item.", "&7", "&fCurrent: &7" + flyItem.getPermissionMessage(), "&7")
                .onClick(event -> {
                    MessageUtil.sendMessage(player, "&aEnter the permission message in the chat. &7Type &ecancel &7 to go back.");
                    FlyItemCreator.setOptionState(player, FlyItemCreator.OptionState.PERM_MESSAGE);
                    player.closeInventory();
                });

        List<String> clickLore = new ArrayList<>(Arrays.asList("&7", "&eChange the click", "&ecommands of this item.", "&7", "&fCurrent: "));
        if (flyItem.getOnClick() != null && flyItem.getOnClick().size() > 0)
            clickLore.addAll(flyItem.getOnClick().stream().map(s -> "&7- " + s).collect(Collectors.toList()));
        else clickLore.add("&7  None");
        clickLore.add("&7");
        Item onClick = new Item(Material.BRICK)
                .setName("&bOn Click")
                .setLore(clickLore)
                .onClick(event -> {
                    MessageUtil.sendMessage(player, "&aEnter the line in the chat. &7Type &ecancel &7 to go back.");
                    MessageUtil.sendMessage(player, "&aPRO TIP: &7Use --at:<number here> to insert the line at the location provided.");
                    FlyItemCreator.setOptionState(player, FlyItemCreator.OptionState.ON_CLICK);
                    player.closeInventory();
                });

        Item amount = new Item(Material.ANVIL)
                .setName("&bAmount")
                .setLore("&7", "&eChange the amount", "&eof this item.", "&7", "&fCurrent: &7" + flyItem.getAmount(), "&7")
                .onClick(event -> {
                    MessageUtil.sendMessage(player, "&aEnter the amount in the chat. &7Type &ecancel &7 to go back.");
                    FlyItemCreator.setOptionState(player, FlyItemCreator.OptionState.AMOUNT);
                    player.closeInventory();
                });

        Item cooldown = new Item(Material.CLOCK)
                .setName("&bCooldown")
                .setLore("&7", "&eChange the cooldown", "&eof this item.", "&7", "&fCurrent: &7" + flyItem.getCooldown(), "&7")
                .onClick(event -> {
                    MessageUtil.sendMessage(player, "&aEnter the time in the chat. &7Type &ecancel &7 to go back.");
                    FlyItemCreator.setOptionState(player, FlyItemCreator.OptionState.COOLDOWN);
                    player.closeInventory();
                });

        Item slot = new Item(Material.CHEST)
                .setName("&bSlot")
                .setLore("&7", "&eChange the slot", "&eof this item.", "&7", "&fCurrent: &7" + flyItem.getSlot(), "&7")
                .onClick(event -> {
                    MessageUtil.sendMessage(player, "&aEnter the slot in the chat. &7Type &ecancel &7 to go back.");
                    FlyItemCreator.setOptionState(player, FlyItemCreator.OptionState.SLOT);
                    player.closeInventory();
                });

        Item back = new Item(Material.ARROW)
                .setName("&aBack")
                .setLore("&7", "&eClick here to go back", "&7")
                .onClick(event -> {
                    FlyItemCreator.clearState(FlyItemCreator.StateType.INNER_STATE, player);
                    FlyItemCreator.openMenu(player);
                });

        inventory.setItem(permission, 0, 3);
        inventory.setItem(permissionMessage, 1, 3);
        inventory.setItem(onClick, 2, 3);
        inventory.setItem(amount, 3, 3);
        inventory.setItem(cooldown, 4, 3);
        inventory.setItem(slot, 5, 3);
        inventory.setItem(back, inventory.getInventory().getSize() - 1);
        if (b) player.openInventory(inventory.getInventory());
        return inventory.getInventory();
    }

    public static Inventory create(Player player) {
        return create(player, true);
    }

    private static void createOption(Player player, int slot, Item item, Consumer<InventoryClickEvent> consumer) {
        Item toggle = new Item(item.toggled() ? Material.LIME_DYE : Material.GRAY_DYE)
                .setName(item.toggled() ? "&aEnabled" : "&cDisabled")
                .setLore("&7", "&eClick here to toggle", "&ethis option.", "&7");
        Consumer<InventoryClickEvent> clickEventConsumer = event -> {
            item.toggle(!item.toggled());
            toggle.setMaterial(item.toggled() ? Material.LIME_DYE : Material.GRAY_DYE)
                    .setName(item.toggled() ? "&aEnabled" : "&cDisabled");
            inventory.setItem(toggle, slot, 1);
        };

        Consumer<InventoryClickEvent> toggleConsumer = clickEventConsumer.andThen(consumer.andThen(event ->
                MessageUtil.sendMessage(player, item.getName() + " &7mode " + (item.toggled() ? "&aactivated!" : "&cdeactivated!"))
        ));

        toggle.onClick(toggleConsumer);
        item.onClick(toggleConsumer);

        inventory.setItem(item, slot);
        inventory.setItem(toggle, slot, 1);
    }
}
