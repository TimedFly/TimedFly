package me.jackint0sh.timedfly.database;

import me.jackint0sh.timedfly.interfaces.AsyncDatabase;
import me.jackint0sh.timedfly.interfaces.Callback;
import org.bukkit.plugin.Plugin;

import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends SQL {

    private Plugin plugin;

    public SQLite(Plugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public AsyncDatabase connect(Callback<String> callback) {
        return connect(null, 0, null, null, null, callback);
    }

    @Override
    public AsyncDatabase connect(String host, int port, String database, String user, String password, Callback<String> callback) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            callback.handle(e, null);
            return this;
        }

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/SQLite.db");
            callback.handle(null, null);
        } catch (SQLException e) {
            callback.handle(e, null);
        }
        return this;
    }
}
