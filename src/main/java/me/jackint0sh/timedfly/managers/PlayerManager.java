package me.jackint0sh.timedfly.managers;

import me.jackint0sh.timedfly.events.TimedFlyEndEvent;
import me.jackint0sh.timedfly.events.TimedFlyStartEvent;
import me.jackint0sh.timedfly.flygui.FlyInventory;
import me.jackint0sh.timedfly.flygui.inventories.FlightStore;
import me.jackint0sh.timedfly.utilities.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    private double flySpeedMultiplier;
    private boolean hasTime;
    private boolean onFloor;
    private boolean timeRunning;
    private boolean timePaused;
    private boolean manualFly;
    private boolean fromPlugin;
    private String lastItemUsed;
    private boolean inBlacklistedWorld;
    private List<World> worlds;
    private FileConfiguration config;

    private PlayerManager(UUID playerUuid) {
        this(playerUuid, 0, 0, false, true, false);
    }

    private PlayerManager(UUID playerUuid, long timeLeft, long initialTime, boolean hasTime, boolean onFloor,
            boolean timeRunning) {
        this.playerUuid = playerUuid;
        this.timeLeft = timeLeft;
        this.initialTime = initialTime;
        this.hasTime = hasTime;
        this.onFloor = onFloor;
        this.timeRunning = timeRunning;
        this.player = Bukkit.getPlayer(playerUuid);
        this.currentTimeLimit = 0;
        if (this.player == null)
            this.player = Bukkit.getOfflinePlayer(playerUuid).getPlayer();
            
        this.config = Config.getConfig("config").get();
        this.worlds = this.config
                .getStringList("World-List.Worlds")
                .stream()
                .map(Bukkit::getWorld)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        this.flySpeedMultiplier = config.getDouble("GlobalFlySpeedMultiplier");
    }

    public void startTimer() {
        if (this.player.isOnline())
            this.player.setAllowFlight(true);
        if (!this.isOnFloor()) {
            this.fromPlugin = true;
            this.timeRunning = true;
            this.player.setFlySpeed((float) (flySpeedMultiplier * 10) / 100);
            if (this.player.isOnline())
                this.player.setFlying(true);
        }
        TimerManager.startIfNot();
        Bukkit.getPluginManager().callEvent(new TimedFlyStartEvent(this));
    }

    public void stopTimer() {
        this.timeRunning = false;
        this.fromPlugin = false;
        if (this.player.isOnline()) {
            this.player.setAllowFlight(false);
            this.player.setFlying(false);
            this.player.setFlySpeed(0.1F);
            if (!this.player.isOnGround())
                this.disableFallDamage();
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
        if (player.isOp() || player.hasPermission(Permissions.ADMIN.getPermission()))
            return true;

        boolean hasPerm;
        if (and) {
            hasPerm = Arrays.stream(permissions)
                    .allMatch(permission -> PlayerManager.hasPermission(player, permission));
        } else {
            hasPerm = Arrays.stream(permissions)
                    .anyMatch(permission -> PlayerManager.hasPermission(player, permission));
        }
        return hasPerm;
    }

    private static boolean hasPermissions(CommandSender sender, boolean and, Permissions... permissions) {
        if (sender.isOp() || sender.hasPermission(Permissions.ADMIN.getPermission()))
            return true;

        boolean hasPerm;
        if (and) {
            hasPerm = Arrays.stream(permissions)
                    .allMatch(permission -> PlayerManager.hasPermission(sender, permission));
        } else {
            hasPerm = Arrays.stream(permissions)
                    .anyMatch(permission -> PlayerManager.hasPermission(sender, permission));
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
        if (!this.config.getBoolean("StopTimerOn.Attack.Enable"))
            return;
        if (this.hasPermission(Permissions.BYPASS_ATTACK) || this.inBlacklistedWorld)
            return;

        if (!this.isAttacking() && this.isTimeRunning()) {
            this.player.setAllowFlight(false);
            this.player.setFlying(false);

            this.setAttacking(true).setTimeRunning(false).disableFallDamage();

            MessageUtil.sendTranslation(this.player, "fly.time.attack_mode.flight_disabled");
        }

        if (attackTimer != null)
            attackTimer.cancel();

        try {
            long cooldown = TimeParser.parse(this.config.getString("StopTimerOn.Attack.Cooldown"));

            attackTimer = PluginTask.runLater(() -> {
                if (!hasTime() || this.inBlacklistedWorld)
                    return;
                setAttacking(false).setTimeRunning(true);
                player.setAllowFlight(true);
                MessageUtil.sendTranslation(player, "fly.time.attack_mode.flight_enabled");
            }, cooldown / 100);
        } catch (TimeParser.TimeFormatException e) {
            e.printStackTrace();
        }
    }

    public boolean handleWorldChange(@Nullable World from) {
        World to = player.getWorld();
        if (from != null && from.getName().equals(to.getName()))
            return true;
        String type = this.config.getString("World-List.Type");

        if (type == null || !hasTime())
            return true;
        else if (type.equals("all")) {
            if (!isAttacking())
                startTimer();
            this.inBlacklistedWorld = false;
            return true;
        }

        switch (type) {
            case "enable":
                if (this.worlds.stream().anyMatch(world -> to.getName().equals(world.getName()))) {
                    if (!isAttacking())
                        startTimer();
                    this.inBlacklistedWorld = false;
                    return true;
                } else {
                    MessageUtil.sendTranslation(player, "fly.time.world_blacklisted");
                    this.inBlacklistedWorld = true;
                    stopTimer();
                    return false;
                }
            case "disable":
                if (worlds.stream().anyMatch(world -> to.getName().equals(world.getName()))) {
                    MessageUtil.sendTranslation(player, "fly.time.world_blacklisted");
                    this.inBlacklistedWorld = true;
                    stopTimer();
                    return false;
                } else {
                    this.inBlacklistedWorld = false;
                    if (!isAttacking())
                        startTimer();
                    return true;
                }
        }
        return true;
    }

    public boolean isInBlacklistedWorld() {
        return inBlacklistedWorld;
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
        if (playerCache.get(uuid) != null)
            return playerCache.get(uuid);
        playerCache.put(uuid, new PlayerManager(uuid));
        return playerCache.get(uuid);
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
        if (this.timeLeft > this.initialTime)
            this.initialTime = this.timeLeft;
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
        return hasTime && timeRunning;
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
            long limit = TimeParser.parse(this.config.getString("LimitMaxTime.Time"));
            return this.currentTimeLimit >= limit;
        } catch (TimeParser.TimeFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public PlayerManager addCurrentTimeLimit(long time) {
        this.currentTimeLimit += time;
        if (passedCurrentTimeLimit() && this.limitCoolDown == 0) {
            String cooldown = this.config.getString("LimitMaxTime.Cooldown");
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
        if (!this.player.isOnline())
            return this;
        FlyInventory flyInventory = FlyInventory.getFlyInventory(player.getOpenInventory().getTitle());
        if (flyInventory != null)
            flyInventory.setItems(FlightStore.createContents(player));
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

    public double getFlySpeedMultiplier() {
        return this.flySpeedMultiplier;
    }

    public PlayerManager setFlySpeedMultiplier(double multiplier) {
        this.flySpeedMultiplier = multiplier;
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