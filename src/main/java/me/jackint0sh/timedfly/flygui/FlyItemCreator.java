package me.jackint0sh.timedfly.flygui;

import me.jackint0sh.timedfly.flygui.inventories.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class FlyItemCreator {

    private Player player;
    private static Map<Player, FlyItem> currentFlyItem = new HashMap<>();
    private static Map<Player, FlyItemCreator.State> mainState = new HashMap<>();
    private static Map<Player, FlyItemCreator.InnerState> innerState = new HashMap<>();
    private static Map<Player, FlyItemCreator.OptionState> optionState = new HashMap<>();

    public FlyItemCreator() {
        this(null);
    }

    public FlyItemCreator(Player player) {
        this.player = player;
    }

    public void openInventory(MenuType menuType) {
        switch (menuType) {
            case MAIN:
                MainMenu.create(player);
                break;
            case EDITOR:
                EditorMenu.create(player);
                break;
            case DELETE:
            case CONFIRM:
            case OPTIONS:
                break;
        }
    }

    public static void openMenu(Player player) {
        State state = getState(player);
        InnerState innerState = getInnerState(player);
        OptionState optionState = getOptionState(player);
        if (state != null) {
            switch (state) {
                case EDIT_ITEM:
                    if (innerState != null) {
                        if (innerState == InnerState.CHANGE_OPTIONS && optionState != null) OptionsMenu.create(player);
                        else if (innerState == InnerState.CHANGE_ITEM) ChangeItemMenu.create(player);
                        else EditorMenu.create(player);
                    } else FlightStore.createEdit(player);
                    break;
                case CREATE_ITEM:
                case EDITING_ITEM:
                    if (innerState != null) {
                        if (innerState == InnerState.CHANGE_OPTIONS) OptionsMenu.create(player);
                        else if (innerState == InnerState.CHANGE_ITEM) ChangeItemMenu.create(player);
                        else EditorMenu.create(player);
                    } else EditorMenu.create(player);
                    break;
                case DELETE_ITEM:
                    ConfirmationMenu.create(player);
                    break;
                case MAIN_MENU:
                    MainMenu.create(player);
                    break;
            }
        } else MainMenu.create(player);
    }

    public static void removeCurrentFlyItem(Player player) {
        currentFlyItem.remove(player);
    }

    public static void setCurrentFlyItem(Player player, FlyItem flyItem) {
        currentFlyItem.put(player, flyItem);
    }

    public static void setInnerState(Player player, InnerState state) {
        innerState.put(player, state);
    }

    public static void setMainState(Player player, State state) {
        mainState.put(player, state);
    }

    public static void setOptionState(Player player, OptionState state) {
        optionState.put(player, state);
    }

    public static FlyItem getCurrentFlyItem(Player player) {
        return currentFlyItem.get(player);
    }

    public static void clearStates(Player player) {
        if (getState(player) != null) mainState.remove(player);
        if (getInnerState(player) != null) innerState.remove(player);
        if (getOptionState(player) != null) optionState.remove(player);
    }

    public static void clearState(StateType stateType, Player player) {
        switch (stateType) {
            case INNER_STATE:
                innerState.remove(player);
                break;
            case MAIN_STATE:
                mainState.remove(player);
                break;
            case OPTION_STATE:
                optionState.remove(player);
                break;
        }
    }

    public static State getState(Player player) {
        return mainState.get(player);
    }

    public static InnerState getInnerState(Player player) {
        return innerState.get(player);
    }

    public static OptionState getOptionState(Player player) {
        return optionState.get(player);
    }

    public enum StateType {
        INNER_STATE, MAIN_STATE, OPTION_STATE
    }

    public enum State {
        CREATE_ITEM, EDIT_ITEM, DELETE_ITEM, MAIN_MENU, EDITING_ITEM
    }

    public enum InnerState {
        CHANGE_NAME, CHANGE_ITEM, CHANGE_PRICE, CHANGE_TIME, CHANGE_OPTIONS, ADD_LORE_LINE, REMOVE_LORE_LINE, SAVE_ITEM
    }

    public enum OptionState {
        COOLDOWN, PERM, PERM_MESSAGE, SLOT, AMOUNT, ON_CLICK
    }

    public enum MenuType {
        MAIN, EDITOR, DELETE, CONFIRM, OPTIONS
    }
}
