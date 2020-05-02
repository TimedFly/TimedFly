package me.jackscode.timedfly.api.events;

import lombok.Getter;
import me.jackscode.timedfly.api.entity.TFPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TimedFlyEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter private final TFPlayer player;

    public TimedFlyEndEvent(TFPlayer player) {
        this.player = player;
    }

    @Override public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
