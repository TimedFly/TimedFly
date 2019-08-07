package me.jackint0sh.timedfly.interfaces;

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

public interface Callback<T> {

    void handle(@Nullable Exception error, @Nullable T result);

}
