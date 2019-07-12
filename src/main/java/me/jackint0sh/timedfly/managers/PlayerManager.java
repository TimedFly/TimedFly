package me.jackint0sh.timedfly.managers;

import me.jackint0sh.timedfly.flygui.FlyInventory;
import me.jackint0sh.timedfly.flygui.inventories.FlightStore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PlayerManager {

    private static Map<UUID, PlayerManager> playerCache = new ConcurrentHashMap<>();
    private Consumer<EntityDamageEvent> onDamage;
    private boolean damageTimerEnabled;
    private UUID playerUuid;
    private Player player;
    private int timeLeft;
    private int initialTime;
    private boolean hasTime;
    private boolean onFloor;
    private boolean timeRunning;
    private boolean timePaused;

    private PlayerManager(UUID playerUuid) {
        this(playerUuid, 0, 0, false, true, false);
    }

    private PlayerManager(UUID playerUuid, int timeLeft, int initialTime, boolean hasTime, boolean onFloor, boolean timeRunning) {
        this.playerUuid = playerUuid;
        this.timeLeft = timeLeft;
        this.initialTime = initialTime;
        this.hasTime = hasTime;
        this.onFloor = onFloor;
        this.timeRunning = timeRunning;
        this.player = Bukkit.getPlayer(playerUuid);
        if (this.player == null) this.player = (Player) Bukkit.getOfflinePlayer(playerUuid);
    }

    public void startTimer() {
        this.timeRunning = true;
        this.player.setAllowFlight(true);
        this.player.setFlying(true);
        TimerManager.startIfNot();
    }

    public void stopTimer() {
        this.timeRunning = false;
        this.player.setAllowFlight(false);
        this.player.setFlying(false);
        this.disableDamage(10);
    }

    public void pauseTimer() {
        this.stopTimer();
        this.timePaused = true;
    }

    public void resumeTimer() {
        this.timePaused = false;
        this.startTimer();
    }

    public void disableDamage(int seconds) {
        this.onDamage = event -> {
            this.damageTimerEnabled = true;
            Plugin plugin = Bukkit.getPluginManager().getPlugin("TimedFly");
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                this.onDamage = null;
                this.damageTimerEnabled = false;
            }, seconds * 20);
        };
    }

    public void callEvent(Event event) {
        if (event instanceof EntityDamageEvent) {
            if (this.onDamage != null) this.onDamage.accept((EntityDamageEvent) event);
        }
    }

    public boolean isDamageTimerEnabled() {
        return this.damageTimerEnabled;
    }

    public static void addPlayer(UUID uuid) {
        playerCache.put(uuid, new PlayerManager(uuid, 0, 0, false, true, false));
    }

    public static PlayerManager getCachedPlayer(UUID uuid) {
        if (playerCache.get(uuid) != null) return playerCache.get(uuid);
        else {
            PlayerManager playerManager = new PlayerManager(uuid);
            playerCache.put(uuid, playerManager);
            return playerManager;
        }
    }

    public static Map<UUID, PlayerManager> getPlayerCache() {
        return playerCache;
    }

    public static float getPlayersTimeLeft() {
        return playerCache.values().stream().mapToLong(PlayerManager::getTimeLeft).sum();
    }

    public Player getPlayer() {
        Player onlinePlayer = Bukkit.getPlayer(playerUuid);
        return onlinePlayer != null ? onlinePlayer : (Player) Bukkit.getOfflinePlayer(playerUuid);
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public PlayerManager setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
        return this;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public PlayerManager setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
        return this;
    }

    public PlayerManager setTime(int time) {
        this.timeLeft = time;
        this.initialTime = time;
        return this;
    }

    public PlayerManager decreaseTime() {
        this.timeLeft--;
        return this;
    }

    public PlayerManager addTime(int time) {
        this.timeLeft += time;
        if (this.timeLeft > this.initialTime) this.initialTime = this.timeLeft;
        return this;
    }

    public PlayerManager decreaseTime(int by) {
        this.timeLeft -= by;
        return this;
    }

    public int getInitialTime() {
        return initialTime;
    }

    public PlayerManager setInitialTime(int initialTime) {
        this.initialTime = initialTime;
        return this;
    }

    public boolean hasTime() {
        return hasTime;
    }

    public PlayerManager setHasTime(boolean hasTime) {
        this.hasTime = hasTime;
        return this;
    }

    public boolean isOnFloor() {
        return onFloor;
    }

    public PlayerManager setOnFloor(boolean onFloor) {
        this.onFloor = onFloor;
        return this;
    }

    public boolean isTimeRunning() {
        return timeRunning;
    }

    public PlayerManager setTimeRunning(boolean timeRunning) {
        this.timeRunning = timeRunning;
        return this;
    }

    public boolean isTimePaused() {
        return timePaused;
    }

    public void updateStore() {
        FlyInventory flyInventory = FlyInventory.getFlyInventory(player.getOpenInventory().getTitle());
        if (flyInventory != null) flyInventory.setItems(FlightStore.createContents(player));
    }
}