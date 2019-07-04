package me.jackint0sh.timedfly.listeners;

import me.jackint0sh.timedfly.flygui.FlyItem;
import me.jackint0sh.timedfly.flygui.FlyItemCreator;
import me.jackint0sh.timedfly.flygui.Item;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import me.jackint0sh.timedfly.utilities.TimeParser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        String message = event.getMessage();
        boolean valid = true;

        FlyItemCreator.State state = FlyItemCreator.getState(player);

        if (state != null) {
            if (state == FlyItemCreator.State.CREATE_ITEM || state == FlyItemCreator.State.EDITING_ITEM) {
                FlyItemCreator.InnerState innerState = FlyItemCreator.getInnerState(player);
                if (innerState != null) {
                    FlyItem flyItem = FlyItemCreator.getCurrentFlyItem(player);
                    if (flyItem != null) {
                        Item item = new Item(flyItem);
                        if (!message.equalsIgnoreCase("cancel")) {
                            switch (innerState) {
                                case CHANGE_NAME:
                                    flyItem.setName(message);
                                    MessageUtil.sendMessage(player, "&aName successfully changed to: " + message);
                                    break;
                                case CHANGE_PRICE:
                                    try {
                                        flyItem.setPrice(Integer.parseInt(message));
                                        MessageUtil.sendMessage(player, "&aPrice successfully changed to: " + message);
                                    } catch (NumberFormatException e) {
                                        MessageUtil.sendMessage(player, "Please provide a valid number.");
                                        valid = false;
                                    }
                                    break;
                                case CHANGE_TIME:
                                    if (TimeParser.isParsable(message)) {
                                        flyItem.setTime(message);
                                        MessageUtil.sendMessage(player, "&aTime successfully changed to: " + message);
                                    } else {
                                        MessageUtil.sendMessage(player, "&cIncorrect time provided!");
                                        valid = false;
                                    }
                                    break;
                                case ADD_LORE_LINE:
                                    String at = "--at:";
                                    String msg = "&7" + message;
                                    int line = -1;
                                    if (msg.contains(at)) {
                                        String[] messageArr = msg.split(" ");
                                        for (String s : messageArr) {
                                            if (s.startsWith(at)) {
                                                try {
                                                    line = Integer.parseInt(s.replace(at, ""));
                                                    msg = msg.replace(s, "").trim();
                                                } catch (NumberFormatException e) {
                                                    MessageUtil.sendMessage(player, "Please provide a valid number.");
                                                    valid = false;
                                                }
                                            }
                                        }
                                    }
                                    if (valid) {
                                        if (line == -1) item.addLoreLine(msg);
                                        else item.insertLoreLine(line, msg);
                                        MessageUtil.sendMessage(player, "&aLore line successfully added: " + message);
                                    }
                                    break;
                                case REMOVE_LORE_LINE:
                                    try {
                                        int loc = Integer.parseInt(message);
                                        if (loc > flyItem.getLore().size()) {
                                            MessageUtil.sendMessage(player, "The provided line could not be found.");
                                            valid = false;
                                        } else {
                                            item.removeLoreLine(loc);
                                            MessageUtil.sendMessage(player, "&aLore line successfully removed");
                                        }
                                    } catch (NumberFormatException e) {
                                        MessageUtil.sendMessage(player, "Please provide a valid number.");
                                        valid = false;
                                    }
                                    break;
                                case CHANGE_OPTIONS:
                                    switch (FlyItemCreator.getOptionState(player)) {
                                        case SLOT:
                                            try {
                                                flyItem.setSlot(Integer.parseInt(message));
                                                MessageUtil.sendMessage(player, "&aSlot successfully changed to: " + message);
                                            } catch (NumberFormatException e) {
                                                MessageUtil.sendMessage(player, "Please provide a valid number.");
                                                valid = false;
                                            }
                                            break;
                                        case AMOUNT:
                                            try {
                                                flyItem.setAmount(Integer.parseInt(message));
                                                MessageUtil.sendMessage(player, "&aAmount successfully changed to: " + message);
                                            } catch (NumberFormatException e) {
                                                MessageUtil.sendMessage(player, "Please provide a valid number.");
                                                valid = false;
                                            }
                                            break;
                                        case COOLDOWN:
                                            if (TimeParser.isParsable(message)) {
                                                flyItem.setCooldown(message);
                                                MessageUtil.sendMessage(player, "&aCooldown successfully changed to: " + message);
                                            } else {
                                                MessageUtil.sendMessage(player, "&cIncorrect time provided!");
                                                valid = false;
                                            }
                                            break;
                                        case PERM:
                                            flyItem.setPermission(message);
                                            MessageUtil.sendMessage(player, "&aPermission successfully changed to: " + message);
                                            break;
                                        case PERM_MESSAGE:
                                            flyItem.setPermissionMessage(message);
                                            MessageUtil.sendMessage(player, "&aPermission message successfully changed to: " + message);
                                            break;
                                    }
                                    break;
                            }
                        } else MessageUtil.sendMessage(player, "&aSuccessfully canceled");
                        if (valid) {
                            flyItem.setLore(item.getLore());
                            if (FlyItemCreator.getInnerState(player) != FlyItemCreator.InnerState.CHANGE_OPTIONS)
                                FlyItemCreator.clearState(FlyItemCreator.StateType.INNER_STATE, player);
                            FlyItemCreator.openMenu(player);
                        }
                        event.setCancelled(true);
                    }
                }
            }
        }

    }

}
