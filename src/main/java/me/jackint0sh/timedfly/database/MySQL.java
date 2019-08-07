package me.jackint0sh.timedfly.database;

import me.jackint0sh.timedfly.interfaces.AsyncDatabase;
import me.jackint0sh.timedfly.interfaces.Callback;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends SQL {

    public MySQL(Plugin plugin) {
        super(plugin);
    }

    @Override
    public AsyncDatabase connect(String host, int port, String database, String user, String password, Callback<String> callback) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            callback.handle(e, null);
            return this;
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database
                    + "?autoReconnect=true", user, password);
            callback.handle(null, null);
        } catch (SQLException e) {
            callback.handle(e, null);
        }
        return this;
    }

}
