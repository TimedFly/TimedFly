package me.jackscode.timedfly.api.events;

import lombok.Getter;
import me.jackscode.timedfly.api.entity.FlyPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TimedFlyEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter private final FlyPlayer player;

    public TimedFlyEndEvent(FlyPlayer player) {
        this.player = player;
    }

    @Override public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
