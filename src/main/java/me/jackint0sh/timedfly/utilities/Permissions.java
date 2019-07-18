package me.jackint0sh.timedfly.utilities;

public enum  Permissions {

    ADMIN("timedfly.admin", "Get access to everything."),
    GET_UPDATE("timedfly.getupdate", "Get an update notification if available."),
    BYPASS_ATTACK("timedfly.bypass.attack", "Bypass restriction of toggle fly off for entering attack mode."),
    BYPASS_LIMIT("timedfly.bypass.attack", "Bypass the limit of time bought."),
    FLY_SET("timedfly.fly.set", "Set fly time to your self."),
    FLY_SET_OTHER("timedfly.fly.set.other", "Set fly time to another player."),
    FLY_ADD("timedfly.fly.add.other", "Add fly time to another player."),
    FLY_ADD_OTHER("timedfly.fly.add.other", "Add fly time to another player."),
    FLY_TOGGLE("timedfly.fly.toggle", "Resume or pause your fly time."),
    FLY_TOGGLE_OTHER("timedfly.fly.toggle.other", "Resume or pause another player's fly time."),
    SKIP_STORE("timedfly.fly.skipstore", "Toggle your flight on/off instead of opening the store."),
    SKIP_STORE_OTHER("timedfly.fly.skipstore.other", "Toggle another player's flight on/off instead of opening the store.");

    private String permission;
    private String description;

    Permissions(String permission, String description) {
        this.permission = permission;
        this.description = description;
    }

    public String getPermission() {
        return permission;
    }

    public String getDescription() {
        return description;
    }
}
