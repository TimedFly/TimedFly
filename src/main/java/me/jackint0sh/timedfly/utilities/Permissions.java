package me.jackint0sh.timedfly.utilities;

public enum  Permissions {

    ADMIN("timedfly.admin", "Get access to everything."),
    RELOAD("timedfly.reload", "Reload the plugin."),
    GET_UPDATE("timedfly.getupdate", "Get an update notification if available."),
    BYPASS_ATTACK("timedfly.bypass.attack", "Bypass restriction of toggle fly off for entering attack mode."),
    BYPASS_LIMIT("timedfly.bypass.attack", "Bypass the limit of time bought."),
    FLY_SET("timedfly.fly.set", "Set fly time to your self or another player."),
    FLY_SET_SELF("timedfly.fly.set.self", "Set fly time to your self."),
    FLY_SET_OTHERS("timedfly.fly.set.others", "Set fly time to another player."),
    FLY_ADD("timedfly.fly.add", "Add fly time to your self or another player."),
    FLY_ADD_SELF("timedfly.fly.add.self", "Add fly time to your self."),
    FLY_ADD_OTHERS("timedfly.fly.add.others", "Add fly time to another player."),
    FLY_TOGGLE("timedfly.fly.toggle", "Resume or pause your or another player's fly time."),
    FLY_TOGGLE_SELF("timedfly.fly.toggle.self", "Resume or pause your fly time."),
    FLY_TOGGLE_OTHERS("timedfly.fly.toggle.others", "Resume or pause another player's fly time."),
    SKIP_STORE("timedfly.fly.skipstore", "Toggle your flight on/off instead of opening the store."),
    SKIP_STORE_OTHERS("timedfly.fly.skipstore.others", "Toggle another player's flight on/off instead of opening the store."),
    CREATOR_OPEN("timedfly.creator.open", "Open the Fly Items editor GUI."),
    CREATOR_CREATE("timedfly.creator.create", "Create new Fly Items."),
    CREATOR_EDIT("timedfly.creator.edit", "Edit available Fly Items."),
    CREATOR_DELETE("timedfly.creator.delete", "Delete available Fly Items."),
    CREATOR_ALL("timedfly.creator.all", "Create, edit, or delete Fly Items.");

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
