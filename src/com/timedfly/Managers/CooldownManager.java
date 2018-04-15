package com.timedfly.Managers;


import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {

    /*
        Thanks to LCastr0 from making this cooldown method,
        he saved my life. His Thread https://bukkit.org/threads/290459/
     */

    private static HashMap<String, CooldownManager> cooldowns = new HashMap<>();
    private final int timeInSeconds;
    private final UUID uuid;
    private final String cooldownName;
    private long start;

    public CooldownManager(UUID uuid, String cooldownName, int timeInSeconds) {
        this.uuid = uuid;
        this.cooldownName = cooldownName;
        this.timeInSeconds = timeInSeconds;
    }

    public static boolean isInCooldown(UUID uuid, String cooldownName) {
        if (getTimeLeft(uuid, cooldownName) >= 1) {
            return true;
        } else {
            stop(uuid, cooldownName);
            return false;
        }
    }

    private static void stop(UUID uuid, String cooldownName) {
        CooldownManager.cooldowns.remove(uuid + cooldownName);
    }

    private static CooldownManager getCooldown(UUID uuid, String cooldownName) {
        return cooldowns.get(uuid.toString() + cooldownName);
    }

    public static int getTimeLeft(UUID uuid, String cooldownName) {
        CooldownManager cooldown = getCooldown(uuid, cooldownName);
        int f = -1;
        if (cooldown != null) {
            long now = System.currentTimeMillis();
            long cooldownTime = cooldown.start;
            int totalTime = cooldown.timeInSeconds;
            int r = (int) (now - cooldownTime) / 1000;
            f = (r - totalTime) * (-1);
        }
        return f;
    }

    public void start() {
        this.start = System.currentTimeMillis();
        cooldowns.put(this.uuid.toString() + this.cooldownName, this);
    }
}
