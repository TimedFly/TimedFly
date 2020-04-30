package me.jackscode.timedfly.api.events;

import lombok.Getter;
import me.jackscode.timedfly.api.entity.TFPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimedFlyEndEvent extends Event {
    @Getter private final HandlerList handlers = new HandlerList();
    @Getter private final TFPlayer player;

    public TimedFlyEndEvent(TFPlayer player) {
        this.player = player;
    }
}
