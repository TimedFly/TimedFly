package me.jackint0sh.timedfly.listeners;

import me.jackint0sh.timedfly.database.DatabaseHandler;
import me.jackint0sh.timedfly.interfaces.AsyncDatabase;
import me.jackint0sh.timedfly.managers.PlayerManager;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerListener implements Listener {

    @EventHandler
    public void onReSpawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());

        if (playerManager != null && playerManager.isTimeRunning()) {
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugins()[0], () -> {
                playerManager.setOnFloor(true).startTimer();
            }, 2);
        }
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

            PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
            if (playerManager != null && !playerManager.isFallDamageEnabled() && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                playerManager.enableFallDamage();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
        if (playerManager != null && (event.getNewGameMode() == GameMode.SURVIVAL || event.getNewGameMode() == GameMode.ADVENTURE)) {
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugins()[0], () -> {
                if (playerManager.getTimeLeft() > 0 && !playerManager.isTimePaused()) {
                    playerManager.setOnFloor(true).startTimer();
                }
            }, 2);
        }
    }

    @EventHandler
    private void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
        if (playerManager == null) return;
        if (playerManager.hasTime() && playerManager.isTimeRunning()) {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onGround(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        if (!Config.getConfig("config").get().getBoolean("StopTimerOn.Ground")) return;

        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());

        if (player.isOnGround() && playerManager != null) {
            playerManager.setOnFloor(true);
            if (playerManager.hasTime() && !playerManager.isAttacking()) {
                if (!player.getAllowFlight()) player.setAllowFlight(true);
                if (playerManager.isTimeRunning()) playerManager.setTimeRunning(false);
            }
        }
    }

    @EventHandler
    public void onFlightToggle(PlayerToggleFlightEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());

        if (playerManager != null && playerManager.isOnFloor() && !playerManager.isManualFly()) {
            if (!playerManager.hasTime()) {
                event.setCancelled(true);
                playerManager.stopTimer();
                return;
            }
            playerManager.setOnFloor(false);
            playerManager.startTimer();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());

        if (playerManager != null) {
            if (!handlePlayerQuery(playerManager, false)) {
                MessageUtil.sendError("Could not handle player's data.");
            }
            playerManager.setPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());

        if (playerManager != null) {
            if (Config.getConfig("config").get().getBoolean("StopTimerOn.Leave")) {
                playerManager.setTimeRunning(false);
            }
            if (!handlePlayerQuery(playerManager, true)) {
                MessageUtil.sendError("Could not update player's data.");
            }
            if (!playerManager.isTimeRunning()) PlayerManager.getPlayerCache().remove(player.getUniqueId());
        }
    }

    public static boolean handlePlayerQuery(PlayerManager playerManager, boolean update) {
        AtomicBoolean bool = new AtomicBoolean(true);
        String[] keys = {
                "UUID", "Name", "TimeLeft", "InitialTime", "CurrentTimeLimit",
                "TimeLimitCooldownExpires", "TimeRunning", "TimePaused", "SaveDate"
        };
        Object[] values = {
                playerManager.getPlayerUuid().toString(), playerManager.getPlayer().getName(), playerManager.getTimeLeft(),
                playerManager.getInitialTime(), playerManager.getCurrentTimeLimit(),
                playerManager.getLimitCooldown(), playerManager.isTimeRunning(), playerManager.isTimePaused(), "CURRENT_TIMESTAMP"
        };

        AsyncDatabase database = DatabaseHandler.getDatabase();
        if (update) {
            database.update(keys, values, (error, result) -> {
                if (error != null) {
                    error.printStackTrace();
                    bool.set(false);
                }
            });
        } else {
            database.insert(keys, values, (error, result) -> {
                if (error != null) {
                    error.printStackTrace();
                    bool.set(false);
                }
                database.select("*", "UUID", playerManager.getPlayerUuid().toString(), (e, r) -> {
                    if (e != null) {
                        e.printStackTrace();
                        bool.set(false);
                        return;
                    }
                    playerManager.setTimeRunning((Integer) r.get("TimeRunning") != 0)
                            .setTimePaused((Integer) r.get("TimePaused") != 0);

                    Object initialTime = r.get("InitialTime");
                    Object limitCooldown = r.get("TimeLimitCooldownExpires");

                    if (initialTime instanceof Long) playerManager.setInitialTime((Long) initialTime);
                    else if (initialTime instanceof Integer) playerManager.setInitialTime((Integer) initialTime);

                    if (limitCooldown instanceof Long) playerManager.setLimitCooldown((Long) limitCooldown);
                    else if (limitCooldown instanceof Integer) playerManager.setLimitCooldown((Integer) limitCooldown);

                    if (!playerManager.isTimeRunning()) {
                        Object timeLeft = r.get("TimeLeft");
                        if (timeLeft instanceof Long) playerManager.setTimeLeft((Long) timeLeft);
                        else if (timeLeft instanceof Integer) playerManager.setTimeLeft((Integer) timeLeft);
                    }

                    if (!playerManager.resetCurrentTimeLimit()) {
                        Object limit = r.get("CurrentTimeLimit");
                        if (limit instanceof Long) playerManager.setCurrentTimeLimit((Long) limit);
                        else if (limit instanceof Integer) playerManager.setCurrentTimeLimit((Integer) limit);
                    }

                    Player player = playerManager.getPlayer();
                    if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
                    playerManager.setOnFloor(player.isOnGround());
                    if (!playerManager.isTimePaused() && playerManager.hasTime()) {
                        if (Config.getConfig("config").get().getBoolean("JoinFlying.Enable")) {
                            int height = Config.getConfig("config").get().getInt("JoinFlying.Height");
                            player.teleport(player.getLocation().add(0, height, 0));
                            playerManager.setOnFloor(false);
                        }
                        playerManager.startTimer();
                    } else if (!playerManager.hasTime() && playerManager.isTimeRunning()) playerManager.stopTimer();
                });
            });
        }
        return bool.get();
    }
}
