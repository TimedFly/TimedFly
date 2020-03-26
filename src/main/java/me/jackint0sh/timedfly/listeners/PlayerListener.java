package me.jackint0sh.timedfly.listeners;

import me.jackint0sh.timedfly.database.DatabaseHandler;
import me.jackint0sh.timedfly.interfaces.AsyncDatabase;
import me.jackint0sh.timedfly.managers.PlayerManager;
import me.jackint0sh.timedfly.managers.UpdateManager;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import me.jackint0sh.timedfly.utilities.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerListener implements Listener {

    private UpdateManager updateManager;

    public PlayerListener(UpdateManager updateManager) {
        this.updateManager = updateManager;
    }

    @EventHandler
    public void onReSpawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
        if (playerManager == null || !playerManager.isFromPlugin()) return;

        if (playerManager.isTimeRunning()) {
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
        if (playerManager == null || !playerManager.isFromPlugin()) return;
        if (event.getNewGameMode() == GameMode.SURVIVAL || event.getNewGameMode() == GameMode.ADVENTURE) {
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
        if (playerManager == null || !playerManager.isFromPlugin()) return;
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
        if (playerManager == null || !playerManager.isFromPlugin()) return;

        if (player.isOnGround()) {
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

        if (playerManager == null || !playerManager.isFromPlugin()) return;

        if (playerManager.isOnFloor() && !playerManager.isManualFly()) {
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

        if (PlayerManager.hasAllPermissions(player, Permissions.GET_UPDATE)) {
            try {
                updateManager.checkForUpdate(player);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            database.select("*", "UUID", playerManager.getPlayerUuid().toString(), (e, r) -> {
                if (e != null) {
                    e.printStackTrace();
                    bool.set(false);

                    if (r == null || r.isEmpty()) {
                        database.insert(keys, values, (error, result) -> {
                            if (error != null) {
                                error.printStackTrace();
                                bool.set(false);
                            }
                        });
                    }
                    return;
                }

                playerManager.setTimeRunning((Integer) r.get("TimeRunning") != 0)
                        .setTimePaused((Integer) r.get("TimePaused") != 0);

                Object initialTime = r.get("InitialTime");
                Object limitCooldown = r.get("TimeLimitCooldownExpires");

                playerManager.setInitialTime(Long.parseLong(String.valueOf(initialTime)));

                playerManager.setLimitCooldown(Long.parseLong(String.valueOf(limitCooldown)));

                if (!playerManager.isTimeRunning()) {
                    Object timeLeft = r.get("TimeLeft");
                    playerManager.setTimeLeft(Long.parseLong(String.valueOf(timeLeft)));
                }

                if (!playerManager.resetCurrentTimeLimit()) {
                    Object limit = r.get("CurrentTimeLimit");
                    playerManager.setCurrentTimeLimit(Long.parseLong(String.valueOf(limit)));
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
        }
        return bool.get();
    }
}
