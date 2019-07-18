package me.jackint0sh.timedfly.flygui.inventories;

import me.jackint0sh.timedfly.flygui.FlyInventory;
import me.jackint0sh.timedfly.flygui.FlyItem;
import me.jackint0sh.timedfly.flygui.FlyItemCreator;
import me.jackint0sh.timedfly.flygui.Item;
import me.jackint0sh.timedfly.managers.PlayerManager;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import me.jackint0sh.timedfly.utilities.TimeParser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightStore {

    public static void create(Player player) throws NullPointerException {
        FlyInventory inventory = new FlyInventory(6, MessageUtil.color("&cTimedFly Store"));
        inventory.setItems(createContents(player));
        player.openInventory(inventory.getInventory());
    }

    public static void createEdit(Player player) throws NullPointerException {
        FlyInventory inventory = new FlyInventory(6, MessageUtil.color("&cTimedFly Store - Edit Item"));
        inventory.setItems(createContents(player, 2));
        player.openInventory(inventory.onClose(event -> {
            if (FlyItemCreator.getState(player) != FlyItemCreator.State.EDITING_ITEM) {
                FlyItemCreator.clearStates(player);
                FlyItemCreator.setMainState(player, FlyItemCreator.State.MAIN_MENU);
            }
        }).getInventory());
    }

    public static void createDelete(Player player) {
        FlyInventory inventory = new FlyInventory(6, MessageUtil.color("&cTimedFly Store - Delete Item"));
        inventory.setItems(createContents(player, 3));
        player.openInventory(inventory.onClose(event -> {
            if (FlyItemCreator.getInnerState(player) != FlyItemCreator.InnerState.CONFIRM_DELETE) {
                FlyItemCreator.clearStates(player);
                FlyItemCreator.setMainState(player, FlyItemCreator.State.MAIN_MENU);
            }
        }).getInventory());
    }

    public static Item[] createContents(Player player, int type) {
        List<Item> items = new ArrayList<>();
        FileConfiguration config = Config.getConfig("items").get();
        ConfigurationSection configSection = config.getConfigurationSection("Items");

        if (configSection == null) throw new NullPointerException("The config file should not be empty!");

        FlyItem.getConfigItems().values().forEach(item -> items.add(new Item(item)
                .setName(MessageUtil.replacePlaceholders(player, item.getName()))
                .setLore(MessageUtil.replacePlaceholders(player, item.getLore())
                        .stream().map(string -> string.replace("[time]", item.getTime()))
                        .collect(Collectors.toList()))
                .onClick(event -> {
                    if (type == 1) {
                        PlayerManager playerManager = PlayerManager.getCachedPlayer(event.getWhoClicked().getUniqueId());
                        try {
                            int time = TimeParser.toTicks(item.getTime());
                            playerManager.addTime(time).startTimer();
                            MessageUtil.sendMessage(player, "Time: " + time);
                        } catch (Exception e) {
                            MessageUtil.sendError(player, e);
                        }
                    } else if (type == 2) {
                        FlyItemCreator.setMainState(player, FlyItemCreator.State.EDITING_ITEM);
                        FlyItemCreator.setCurrentFlyItem(player, item);
                        EditorMenu.create(player);
                    } else if (type == 3) {
                        FlyItemCreator.setInnerState(player, FlyItemCreator.InnerState.CONFIRM_DELETE);
                        FlyItemCreator.setCurrentFlyItem(player, item);
                        ConfirmationMenu.create(player);
                    }
                })));

        return items.toArray(new Item[0]);
    }

    public static Item[] createContents(Player player) {
        return createContents(player, 1);
    }
}
