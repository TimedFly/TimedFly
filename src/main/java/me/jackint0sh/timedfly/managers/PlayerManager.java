package me.jackint0sh.timedfly.managers;

import me.jackint0sh.timedfly.flygui.FlyInventory;
import me.jackint0sh.timedfly.flygui.inventories.FlightStore;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import me.jackint0sh.timedfly.utilities.Permissions;
import me.jackint0sh.timedfly.utilities.TimeParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private static Map<UUID, PlayerManager> playerCache = new ConcurrentHashMap<>();
    private BukkitTask attackTimer;
    private boolean fallDamage;
    private boolean attacking;
    private UUID playerUuid;
    private Player player;
    private int timeLeft;
    private int initialTime;
    private int currentTimeLimit;
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
        this.currentTimeLimit = 0;
        if (this.player == null) this.player = Bukkit.getOfflinePlayer(playerUuid).getPlayer();
    }

    public void startTimer() {
        this.player.setAllowFlight(true);
        if (!this.isOnFloor()) {
            this.timeRunning = true;
            this.player.setFlying(true);
        }
        TimerManager.startIfNot();
    }

    public void stopTimer() {
        this.timeRunning = false;
        this.player.setAllowFlight(false);
        this.player.setFlying(false);
        if (!this.player.isOnGround()) this.disableFallDamage();
    }

    public void pauseTimer() {
        this.stopTimer();
        this.timePaused = true;
    }

    public void resumeTimer() {
        this.startTimer();
        this.timePaused = false;
    }

    public boolean hasPermission(Permissions permission) {
        return PlayerManager.hasPermission(this.player, permission);
    }

    public static boolean hasPermission(Player player, Permissions permission) {
        return player.isOp()
                || player.hasPermission(Permissions.ADMIN.getPermission())
                || player.hasPermission(permission.getPermission());
    }

    private static boolean hasPermissions(Player player, boolean and, Permissions... permissions) {
        if (player.isOp() || player.hasPermission(Permissions.ADMIN.getPermission())) return true;

        boolean hasPerm;
        if (and) {
            hasPerm = Arrays.stream(permissions).allMatch(permission -> PlayerManager.hasPermission(player, permission));
        } else {
            hasPerm = Arrays.stream(permissions).anyMatch(permission -> PlayerManager.hasPermission(player, permission));
        }
        return hasPerm;
    }

    public static boolean hasAllPermissions(Player player, Permissions... permissions) {
        return hasPermissions(player, true, permissions);
    }

    public static boolean hasAnyPermission(Player player, Permissions... permissions) {
        return hasPermissions(player, false, permissions);
    }

    public void enterAttackMode() {
        if (!Config.getConfig("config").get().getBoolean("StopTimerOn.Attack.Enable")) return;
        if (this.hasPermission(Permissions.BYPASS_ATTACK)) return;
        if (!this.isAttacking() && this.isTimeRunning()) {

            this.player.setAllowFlight(false);
            this.player.setFlying(false);
            this.setAttacking(true).disableFallDamage();

            MessageUtil.sendMessage(this.player, "Entering attack mode. Flight disabled!");
        }

        if (attackTimer != null) attackTimer.cancel();

        try {
            attackTimer = Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugins()[0], () -> {
                if (!this.hasTime()) return;
                this.player.setAllowFlight(true);
                this.setAttacking(false);
                MessageUtil.sendMessage(this.player, "Exiting attack mode. Flight re-enabled!");
            }, TimeParser.toTicks(Config.getConfig("config").get().getString("StopTimerOn.Attack.Cooldown")));
        } catch (TimeParser.TimeFormatException e) {
            MessageUtil.sendError(player, e);
        }
    }

    public void disableFallDamage() {
        this.fallDamage = false;
    }

    public void enableFallDamage() {
        this.fallDamage = true;
    }

    public boolean isFallDamageEnabled() {
        return this.fallDamage;
    }

    public boolean isAttacking() {
        return this.attacking;
    }

    public PlayerManager setAttacking(boolean attacking) {
        this.attacking = attacking;
        return this;
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


    public PlayerManager setPlayer(Player player) {
        this.player = player;
        this.playerUuid = player.getUniqueId();
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
        return hasTime = timeLeft > 0;
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

    public int getCurrentTimeLimit() {
        return currentTimeLimit;
    }

    public PlayerManager resetCurrentTimeLimit() {
        this.currentTimeLimit = 0;
        return this;
    }

    public boolean passedCurrentTimeLimit(int time) {
        return this.currentTimeLimit >= time ;
    }

    public PlayerManager addCurrentTimeLimit(int time) {
        this.currentTimeLimit += time ;
        return this;
    }

    public PlayerManager updateStore() {
        FlyInventory flyInventory = FlyInventory.getFlyInventory(player.getOpenInventory().getTitle());
        if (flyInventory != null) flyInventory.setItems(FlightStore.createContents(player));
        return this;
    }

    @Override
    public String toString() {
        return "PlayerManager{" +
                "attackTimer=" + attackTimer +
                ", fallDamage=" + fallDamage +
                ", attacking=" + attacking +
                ", playerUuid=" + playerUuid +
                ", player=" + player +
                ", timeLeft=" + timeLeft +
                ", initialTime=" + initialTime +
                ", hasTime=" + hasTime +
                ", onFloor=" + onFloor +
                ", timeRunning=" + timeRunning +
                ", timePaused=" + timePaused +
                '}';
    }
}