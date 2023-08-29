package me.jackscode.timedfly.api.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.jackscode.timedfly.api.Messenger;
import me.jackscode.timedfly.api.Permission;
import me.jackscode.timedfly.api.events.TimedFlyEndEvent;
import me.jackscode.timedfly.api.events.TimedFlyStartEvent;
import me.jackscode.timedfly.managers.TimerManager;
import me.jackscode.timedfly.utilities.TimeParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.UUID;

@Getter public class FlyPlayer extends Messenger {

    private static final IdentityHashMap<Player, FlyPlayer> players = new IdentityHashMap<>();
    @Getter(AccessLevel.NONE) @Setter private boolean hasTime;
    @Setter private long timeLeft;
    @Setter private long initialTime;
    @Setter private boolean timeRunning;
    @Setter private boolean timePaused;
    @Setter @Getter private boolean preventFallDamage;
    private final Player player;
    private final UUID uuid;

    public FlyPlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();

        this.setPlaceholders();
    }

    public void startTimer() {
        if (!this.hasTime()) {
            this.sendMessage("You have no time left");
            return;
        }
        this.player.setAllowFlight(true);
        this.player.setFlying(true);
        this.setTimeRunning(true);
        this.setHasTime(true);
        this.setPreventFallDamage(false);

        TimerManager.addPlayer(this);
        TimerManager.start();
        Bukkit.getPluginManager().callEvent(new TimedFlyStartEvent(this));
    }

    public void stopTimer() {
        if (!this.isTimeRunning()) {
            this.sendMessage("Timer is not running");
            return;
        }
        this.player.setAllowFlight(false);
        this.player.setFlying(false);
        this.setPreventFallDamage(true);
        this.setTimeRunning(false);
        this.setHasTime(false);

        TimerManager.removePlayer(this);
        Bukkit.getPluginManager().callEvent(new TimedFlyEndEvent(this));
    }

    public void decreaseTime() {
        this.timeLeft--;
    }

    public boolean hasTime() {
        this.hasTime = this.timeLeft > 0;
        return this.hasTime;
    }

    public void addTime(long time) {
        this.timeLeft += time;
    }

    public String timeLeftToString() {
        return TimeParser.toReadableString(TimeParser.secondsToMs(this.timeLeft));
    }

    @Override
    public boolean sendMessage(String... messages) {
        this.setPlaceholders();

        Player player = this.player.getPlayer();
        if (player == null) return false;

        String[] msgs = Arrays.stream(messages)
                .map(message -> {
                    message = "&7[&cTimedFly&7] &f" + message;
                    String colored = ChatColor.translateAlternateColorCodes('&', message);
                    return this.replacePlaceholders(colored);
                })
                .toArray(String[]::new);
        player.sendMessage(msgs);
        return true;
    }

    @Override
    public boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            Permission perm = Permission.get(permission);

            if (perm == null) {
                throw new InvalidParameterException("There is no permission with name: " + permission);
            }

            if (!getPlayer().hasPermission(perm.getNode())) {
                return false;
            }

        }
        return true;
    }

    private void setPlaceholders() {
        this.add("{player}", this.player.getName());
        this.add("{time_left}", this.timeLeftToString());
        this.add("{time_left_seconds}", this.getTimeLeft() + "");
        this.add("{time_left_minutes}", this.getTimeLeft() * 60 + "");
        this.add("{initial_time}", " PARSED ");
        this.add("{initial_time_seconds}", this.getInitialTime() + "");
        this.add("{initial_time_minutes}", this.getInitialTime() * 60 + "");
    }
    

    public static FlyPlayer getPlayer(Player player) {
        FlyPlayer flyPlayer = players.get(player);
        if (flyPlayer != null) return flyPlayer;
        flyPlayer = new FlyPlayer(player);
        players.put(player, flyPlayer);
        return flyPlayer;
    }
}
