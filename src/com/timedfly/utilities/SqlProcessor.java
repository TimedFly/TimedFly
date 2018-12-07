package com.timedfly.utilities;

import com.timedfly.configurations.ConfigCache;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class SqlProcessor implements Runnable {
    private final Plugin plugin;
    private final BlockingDeque<Consumer<Connection>> queue = new LinkedBlockingDeque<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Connection connection;

    public SqlProcessor(Plugin plugin) throws Exception {
        this.plugin = plugin;
        this.connection = setupConnection(plugin);
        queueAsync(this::createTable);
    }

    public static Connection setupConnection(Plugin plugin) throws Exception {
        String host = ConfigCache.getMysqlHost();
        String port = ConfigCache.getMysqlPort();
        String database = ConfigCache.getMysqlDB();
        String username = ConfigCache.getMysqlUser();
        String password = ConfigCache.getMysqlPasss();

        if (ConfigCache.getSqlType().equalsIgnoreCase("sqlite")) {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/SQLite.db";
            Connection connection = DriverManager.getConnection(url);
            Message.sendConsoleMessage("&7Successfully connected to &cSQLite &7database.");
            return connection;
        } else if (ConfigCache.getSqlType().equalsIgnoreCase("mysql")) {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database
                    + "?autoReconnect=true", username, password);
            Message.sendConsoleMessage("&7Successfully connected to &cMySQL &7database.");
            return connection;
        } else {
            Message.sendConsoleMessage("&cCould not connect to any database. Please use sqlite or mysql in your MySQL file.");
            throw new IllegalStateException();
        }
    }

    private void createTable(Connection connection) {
        String sql = "CREATE TABLE IF NOT EXISTS timedfly_fly_time ("
                + "	UUID text,"
                + "	NAME text ,"
                + "	TIMELEFT int,"
                + "	INITIALTIME int,"
                + " TimeStopped boolean"
                + ");";
        try {
            if (!connection.isClosed()) {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.execute();
                preparedStatement.close();
            }
        } catch (SQLException e) {
            Message.sendConsoleMessage("&cSQL ERROR: " + e.getMessage());
            Message.sendConsoleMessage("&cSQL ERROR: Could not create table &6timedfly_fly_time &creport this to the developer");
        }
    }

    public void queueAsync(Consumer<Connection> runnable) {
        this.queue.add(runnable);
    }

    public void release() throws SQLException {
        lock.lock();
        connection.close();
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void run() {
        while (plugin.isEnabled()) {
            try {
                Consumer<Connection> runnable = queue.take();
                lock.lock();
                runnable.accept(connection);
            } catch (Exception ex) {
                // Ignore
            } finally {
                lock.unlock();
            }
        }
    }
}
