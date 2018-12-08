package com.github.entrypointkr.timedfly;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

public class SqlProcessor implements Runnable {
    private final Plugin plugin;
    private final BlockingDeque<Consumer<Connection>> queue = new LinkedBlockingDeque<>();
    private final CountDownLatch latch = new CountDownLatch(1);
    private final Connection connection;
    private boolean count = false;

    public SqlProcessor(Plugin plugin) {
        this.plugin = plugin;
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.plugin.getDataFolder() + "/SQLite.db");
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void queue(Consumer<Connection> runnable) {
        this.queue.add(runnable);
    }

    @Override
    public void run() {
        while (plugin.isEnabled()) {
            try {
                Consumer<Connection> runnable = queue.take();
                runnable.accept(connection);
            } catch (Exception ex) {
                // Ignore
            }
            if (count && !queue.isEmpty()) {
                latch.countDown();
            }
        }
    }

    public void release() throws InterruptedException, SQLException {
        count = true;
        latch.await();
        connection.close();
    }
}
