package me.jackint0sh.timedfly.database;

import me.jackint0sh.timedfly.interfaces.AsyncDatabase;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatabaseHandler {

    private static AsyncDatabase database;
    private static Plugin plugin = Bukkit.getPluginManager().getPlugin("TimedFly");

    public static boolean initialize() {
        MessageUtil.sendConsoleMessage("&cConnecting to database...");

        FileConfiguration config = Config.getConfig("config").get();
        String type = config.getString("Database.Type");

        if (type == null) {
            MessageUtil.sendError("No Type found on database section on config file.");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return false;
        }

        AtomicBoolean valid = new AtomicBoolean(true);
        MessageUtil.sendConsoleMessage("&cUsing &e" + type + " &cas database.");

        switch (type.toLowerCase()) {
            case "sqlite":
                database = new SQLite(plugin).connect((error, result) -> {
                    if (error != null) {
                        error.printStackTrace();
                        Bukkit.getPluginManager().disablePlugin(plugin);
                        valid.set(false);
                        MessageUtil.sendError("&7Could not connected to database.");
                        return;
                    }
                    MessageUtil.sendConsoleMessage("&7Successfully connected to database.");
                });
                break;
            case "mysql":
                String host = config.getString("Database.Server.Host"),
                        username = config.getString("Database.Server.Username"),
                        password = config.getString("Database.Server.Password"),
                        db = config.getString("Database.Server.Database");

                int port = config.getInt("Database.Server.Port");
                database = new MySQL(plugin).connect(host, port, db, username, password, (error, result) -> {
                    if (error != null) {
                        error.printStackTrace();
                        Bukkit.getPluginManager().disablePlugin(plugin);
                        valid.set(false);
                        return;
                    }
                    MessageUtil.sendConsoleMessage("&7Successfully connected to database.");
                });
                break;
            case "mongo":
                // TODO: Connect to mongo database
                break;
            default:
                break;
        }

        if (database instanceof SQL) ((SQL) database).startProcess();
        database.createTable((e, r) -> {
            if (e != null) e.printStackTrace();
        });
        return valid.get();
    }

    public static AsyncDatabase getDatabase() {
        return database;
    }

    public static void close() {
        if (database instanceof SQL) {
            SQL sql = (SQL) database;
            sql.queue.add(connection -> {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
