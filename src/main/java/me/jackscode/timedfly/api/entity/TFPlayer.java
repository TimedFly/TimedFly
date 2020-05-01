package me.jackscode.timedfly.api.entity;

import lombok.Getter;
import lombok.Setter;
import me.jackscode.timedfly.api.Messenger;
import me.jackscode.timedfly.api.events.TimedFlyEndEvent;
import me.jackscode.timedfly.api.events.TimedFlyStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

@Getter public class TFPlayer extends Messenger {

    @Setter private int timeLeft;
    @Setter private int initialTime;
    @Setter private boolean running;
    @Setter private boolean hasTime;
    private final Player player;
    private final UUID uuid;

    public TFPlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.tfPlayer = this;
    }

    public void startTimer() {
        this.sendMessage("Timer for player " + this.player.getName() + " started");
        Bukkit.getPluginManager().callEvent(new TimedFlyStartEvent(this));
    }

    public void stopTimer() {
        this.sendMessage("Timer for player " + this.player.getName() + " stopped");
        Bukkit.getPluginManager().callEvent(new TimedFlyEndEvent(this));
    }

    public void decreaseTime() {
        this.timeLeft--;
    }

    @Override
    public boolean sendMessage(String message) {
        Player player = this.player.getPlayer();
        if (player == null) return false;

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        return true;
    }

    @Override
    public boolean sendMessage(String[] messages) {
        Player player = this.player.getPlayer();
        if (player == null) return false;

        String[] msgs = Arrays.stream(messages)
                .map(message -> ChatColor.translateAlternateColorCodes('&', message))
                .toArray(String[]::new);

        player.sendMessage(msgs);
        return true;
    }

}
