package me.jackscode.timedfly.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

@Getter public class TFPlayer extends Messenger {

    @Setter private int timeLeft;
    @Setter private int initialTime;
    @Setter private boolean running;
    @Setter private boolean hasTime;
    private final OfflinePlayer player;
    private final UUID uuid;

    public TFPlayer(UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getOfflinePlayer(uuid);
        this.tfPlayer = this;
    }

    public void startTimer() {
        this.sendMessage("Timer for player " + this.player.getName() + " started");
        // TODO: Timer
    }

    public void stopTimer() {
        this.sendMessage("Timer for player " + this.player.getName() + " stopped");
        // TODO: Timer
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
