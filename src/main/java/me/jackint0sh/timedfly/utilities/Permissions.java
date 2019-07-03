package me.jackint0sh.timedfly.utilities;

public enum  Permissions {

    ADMIN("timedfly.admin", "Get access to everything.");

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
