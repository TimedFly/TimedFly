package com.minestom.Utilities;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Scheduler extends BukkitRunnable {

    private final JavaPlugin plugin;
    
    private int counter;

    public Scheduler(JavaPlugin plugin, int counter) {
        this.plugin = plugin;
        if (counter < 1) {
            throw new IllegalArgumentException("counter must be greater than 1");
        } else {
            this.counter = counter;
        }
    }

    @Override
    public void run() {
        // What you want to schedule goes here
        if (counter > 0) { 
            plugin.getServer().broadcastMessage("Commence greeting in " + counter--);
        } else {
            plugin.getServer().broadcastMessage("Welcome to Bukkit! Remember to read the documentation!");
            this.cancel();
        }
    }

}