package me.jackint0sh.timedfly.managers;

import me.jackint0sh.timedfly.events.TimedFlyEndEvent;
import me.jackint0sh.timedfly.events.TimedFlyStartEvent;
import me.jackint0sh.timedfly.flygui.FlyInventory;
import me.jackint0sh.timedfly.flygui.inventories.FlightStore;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import me.jackint0sh.timedfly.utilities.Permissions;
import me.jackint0sh.timedfly.utilities.TimeParser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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
    private long timeLeft;
    private long initialTime;
    private long currentTimeLimit;
    private long limitCoolDown;
    private boolean hasTime;
    private boolean onFloor;
    private boolean timeRunning;
    private boolean timePaused;
    private boolean manualFly;
    private boolean fromPlugin;
    private String lastItemUsed;

    private PlayerManager(UUID playerUuid) {
        this(playerUuid, 0, 0, false, true, false);
    }

    private PlayerManager(UUID playerUuid, long timeLeft, long initialTime, boolean hasTime, boolean onFloor, boolean timeRunning) {
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
        if (this.player.isOnline()) this.player.setAllowFlight(true);
        if (!this.isOnFloor()) {
            this.timeRunning = true;
            if (this.player.isOnline()) this.player.setFlying(true);
        }
        TimerManager.startIfNot();
        Bukkit.getPluginManager().callEvent(new TimedFlyStartEvent(this));
    }

    public void stopTimer() {
        this.timeRunning = false;
        if (this.player.isOnline()) {
            this.player.setAllowFlight(false);
            this.player.setFlying(false);
            if (!this.player.isOnGround()) this.disableFallDamage();
        }
        Bukkit.getPluginManager().callEvent(new TimedFlyEndEvent(this));
    }

    public void pauseTimer() {
        this.stopTimer();
        this.timePaused = true;
    }

    public void resumeTimer() {
        this.startTimer();
        this.timePaused = false;
    }

    public PlayerManager setTimePaused(boolean b) {
        this.timePaused = b;
        return this;
    }

    public boolean hasPermission(Permissions permission) {
        return PlayerManager.hasPermission(this.player, permission);
    }

    public static boolean hasPermission(Player player, Permissions permission) {
        return player.isOp()
                || player.hasPermission(Permissions.ADMIN.getPermission())
                || player.hasPermission(permission.getPermission());
    }

    public static boolean hasPermission(Player player, String permission) {
        return player.isOp()
                || player.hasPermission(Permissions.ADMIN.getPermission())
                || player.hasPermission(permission);
    }

    public static boolean hasPermission(CommandSender sender, Permissions permission) {
        return sender.isOp()
                || sender.hasPermission(Permissions.ADMIN.getPermission())
                || sender.hasPermission(permission.getPermission());
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        return sender.isOp()
                || sender.hasPermission(Permissions.ADMIN.getPermission())
                || sender.hasPermission(permission);
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

    private static boolean hasPermissions(CommandSender sender, boolean and, Permissions... permissions) {
        if (sender.isOp() || sender.hasPermission(Permissions.ADMIN.getPermission())) return true;

        boolean hasPerm;
        if (and) {
            hasPerm = Arrays.stream(permissions).allMatch(permission -> PlayerManager.hasPermission(sender, permission));
        } else {
            hasPerm = Arrays.stream(permissions).anyMatch(permission -> PlayerManager.hasPermission(sender, permission));
        }
        return hasPerm;
    }

    public static boolean hasAllPermissions(Player player, Permissions... permissions) {
        return hasPermissions(player, true, permissions);
    }

    public static boolean hasAllPermissions(CommandSender sender, Permissions... permissions) {
        return hasPermissions(sender, true, permissions);
    }

    public static boolean hasAnyPermission(Player player, Permissions... permissions) {
        return hasPermissions(player, false, permissions);
    }

    public static boolean hasAnyPermission(CommandSender sender, Permissions... permissions) {
        return hasPermissions(sender, false, permissions);
    }

    public void enterAttackMode() {
        if (!Config.getConfig("config").get().getBoolean("StopTimerOn.Attack.Enable")) return;
        if (this.hasPermission(Permissions.BYPASS_ATTACK)) return;
        if (!this.isAttacking() && this.isTimeRunning()) {

            this.player.setAllowFlight(false);
            this.player.setFlying(false);
            this.setAttacking(true).setTimeRunning(false).disableFallDamage();

            MessageUtil.sendTranslation(this.player, "fly.time.attack_mode.flight_disabled");
        }

        if (attackTimer != null) attackTimer.cancel();

        try {
            attackTimer = Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugins()[0], () -> {
                if (!this.hasTime()) return;
                this.player.setAllowFlight(true);
                this.setAttacking(false).setTimeRunning(true);
                MessageUtil.sendTranslation(this.player, "fly.time.attack_mode.flight_enabled");
            }, TimeParser.parse(Config.getConfig("config").get().getString("StopTimerOn.Attack.Cooldown")));
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

    public static long getPlayersTimeLeft() {
        return playerCache.values().stream().mapToLong(PlayerManager::getTimeLeft).sum();
    }

    public Player getPlayer() {
        Player onlinePlayer = Bukkit.getPlayer(playerUuid);
        return onlinePlayer != null ? onlinePlayer : Bukkit.getOfflinePlayer(playerUuid).getPlayer();
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

    public long getTimeLeft() {
        return timeLeft;
    }

    public PlayerManager setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
        return this;
    }

    public PlayerManager setTime(long time) {
        this.timeLeft = time;
        this.initialTime = time;
        return this;
    }

    public PlayerManager decreaseTime() {
        this.timeLeft -= TimeParser.secondsToMs(1);
        return this;
    }

    public PlayerManager addTime(long time) {
        this.timeLeft += time;
        if (this.timeLeft > this.initialTime) this.initialTime = this.timeLeft;
        return this;
    }

    public PlayerManager decreaseTime(long by) {
        this.timeLeft -= by;
        return this;
    }

    public long getInitialTime() {
        return initialTime;
    }

    public PlayerManager setInitialTime(long initialTime) {
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

    public long getCurrentTimeLimit() {
        return this.currentTimeLimit;
    }

    public PlayerManager setCurrentTimeLimit(long l) {
        this.currentTimeLimit = l;
        return this;
    }

    public boolean resetCurrentTimeLimit() {
        if (this.limitCoolDown != 0 && System.currentTimeMillis() >= this.limitCoolDown) {
            this.currentTimeLimit = 0;
            this.limitCoolDown = 0;
            return true;
        }
        return false;
    }

    public boolean passedCurrentTimeLimit() {
        try {
            long limit = TimeParser.parse(Config.getConfig("config").get().getString("LimitMaxTime.Time"));
            return this.currentTimeLimit >= limit;
        } catch (TimeParser.TimeFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public PlayerManager addCurrentTimeLimit(long time) {
        this.currentTimeLimit += time;
        if (passedCurrentTimeLimit() && this.limitCoolDown == 0) {
            String cooldown = Config.getConfig("config").get().getString("LimitMaxTime.Cooldown");
            try {
                this.limitCoolDown = System.currentTimeMillis() + TimeParser.parse(cooldown);
            } catch (TimeParser.TimeFormatException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public long getLimitCooldown() {
        return this.limitCoolDown;
    }

    public String getLimitCooldownString() {
        return TimeParser.toReadableString(limitCoolDown - System.currentTimeMillis());
    }

    public PlayerManager setLimitCooldown(long l) {
        this.limitCoolDown = l;
        return this;
    }

    public PlayerManager updateStore() {
        if (!this.player.isOnline()) return this;
        FlyInventory flyInventory = FlyInventory.getFlyInventory(player.getOpenInventory().getTitle());
        if (flyInventory != null) flyInventory.setItems(FlightStore.createContents(player));
        return this;
    }

    public boolean isManualFly() {
        return manualFly;
    }

    public PlayerManager setManualFly(boolean manualFly) {
        this.manualFly = manualFly;
        return this;
    }

    public String getLastItemUsed() {
        return lastItemUsed;
    }

    public PlayerManager setLastItemUsed(String lastItemUsed) {
        this.lastItemUsed = lastItemUsed;
        return this;
    }

    public boolean isFromPlugin() {
        return fromPlugin;
    }

    public PlayerManager setFromPlugin(boolean fromPlugin) {
        this.fromPlugin = fromPlugin;
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
                ", currentTimeLimit=" + currentTimeLimit +
                ", limitCoolDown=" + limitCoolDown +
                ", hasTime=" + hasTime +
                ", onFloor=" + onFloor +
                ", timeRunning=" + timeRunning +
                ", timePaused=" + timePaused +
                ", manualFly=" + manualFly +
                ", fromPlugin=" + fromPlugin +
                '}';
    }
}