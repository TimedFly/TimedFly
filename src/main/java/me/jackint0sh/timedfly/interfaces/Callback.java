package me.jackint0sh.timedfly.interfaces;

import org.jetbrains.annotations.Nullable;

public interface Callback<T> {

    void handle(@Nullable Exception error, @Nullable T result);

}
