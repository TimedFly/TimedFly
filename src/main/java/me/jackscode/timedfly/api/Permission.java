package me.jackscode.timedfly.api;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Permission {

    private static final Map<String, Permission> permissionMap = new HashMap<>();
    @Getter private final String node;
    @Getter private final String description;

    Permission(String node, String description) {
        this.node = "timedfly." + node;
        this.description = description;
    }

    @Nullable
    public static Permission get(String permission) {
        return permissionMap.get(permission);
    }

    public static void add(String node, String description) {
        permissionMap.put(node, new Permission(node, description));
    }
}
