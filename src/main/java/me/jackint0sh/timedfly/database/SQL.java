package me.jackint0sh.timedfly.database;

import me.jackint0sh.timedfly.interfaces.AsyncDatabase;
import me.jackint0sh.timedfly.interfaces.Callback;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.Hashtable;
import java.util.Map;

public abstract class SQL implements AsyncDatabase {

    private Plugin plugin;
    Connection connection;

    SQL(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void createTable(Callback<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!isConnected(callback)) return;

            String sql = "CREATE TABLE IF NOT EXISTS " + table + " ("
                    + "	id INTEGER PRIMARY KEY,"
                    + "	UUID TEXT NOT NULL,"
                    + "	Name TEXT NOT NULL,"
                    + "	TimeLeft LONG DEFAULT 0,"
                    + "	InitialTime LONG DEFAULT 0,"
                    + "	CurrentTimeLimit LONG DEFAULT 0,"
                    + "	TimeLimitCooldownExpires LONG DEFAULT 0,"
                    + " TimeRunning BOOLEAN DEFAULT false,"
                    + " TimePaused BOOLEAN DEFAULT false,"
                    + " SaveDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ");";
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(sql);
                boolean execute = statement.execute();

                Bukkit.getScheduler().runTask(plugin, () -> {
                    callback.handle(null, execute);
                });
            } catch (SQLException e) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    callback.handle(e, false);
                });
            } finally {
                try {
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void select(String key, String whereKey, Object whereValue, Callback<Map<String, Object>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!isConnected(callback)) return;

            String sql = "SELECT " + key + " FROM " + table;
            if (whereKey == null || whereValue == null) sql += " WHERE " + whereKey + " = " + whereValue + ";";
            else sql += ";";

            PreparedStatement statement = null;
            ResultSet execute = null;
            try {
                statement = connection.prepareStatement(sql);
                this.set(whereValue, 1, statement);
                execute = statement.executeQuery();
                ResultSetMetaData metaData = execute.getMetaData();
                Map<String, Object> result = new Hashtable<>();

                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String colName = metaData.getColumnName(i);
                    result.put(colName, execute.getObject(colName));
                }

                Bukkit.getScheduler().runTask(plugin, () -> {
                    callback.handle(null, result);
                });
            } catch (SQLException e) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    callback.handle(e, null);
                });
            } finally {
                try {
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    if (execute != null) execute.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void insert(String[] keys, Object[] values, Callback<Object> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!isConnected(callback)) return;

            if (keys.length != values.length) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    callback.handle(new Exception("Mismatch on keys and values length."), null);
                });
                return;
            }

            String sql = "INSERT INTO " + table + " (" + String.join(",", keys) + ") " +
                    "SELECT * FROM (SELECT ";
            for (int i = 0; i < keys.length; i++) {
                sql += "?";
                if (i < keys.length - 1) sql += ",";
            }
            sql += ") AS tmp WHERE NOT EXISTS (" +
                    "SELECT " + keys[0] + " FROM " + table + " WHERE " + keys[0] + " = ?" +
                    ") LIMIT 1;";

            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(sql);
                for (int i = 0; i < keys.length; i++) this.set(values[i], i + 1, statement);
                this.set(values[0], values.length + 1, statement);
                boolean execute = statement.execute();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    callback.handle(null, execute);
                });
            } catch (SQLException e) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    callback.handle(e, null);
                });
            } finally {
                try {
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void update(String[] keys, Object[] values, String whereKey, Object whereValue, Callback<Object> callback) {
        if (!plugin.isEnabled()) {
            runUpdate(keys, values, whereKey, whereValue, callback);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> runUpdate(keys, values, whereKey, whereValue, callback));
    }

    private void runUpdate(String[] keys, Object[] values, String whereKey, Object whereValue, Callback<Object> callback) {
        if (!isConnected(callback)) return;

        if (keys.length != values.length) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                callback.handle(new Exception("Mismatch on keys and values length."), null);
            });
            return;
        }

        String sql = "UPDATE " + table + " SET ";
        for (int i = 0; i < keys.length; i++) {
            sql += keys[i] + " = ?";
            if (i < keys.length - 1) sql += ",";
        }

        sql += " WHERE " + whereKey + " = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            for (int i = 0; i < keys.length; i++) this.set(values[i], i + 1, statement);
            this.set(whereValue, values.length + 1, statement);
            int update = statement.executeUpdate();
            Bukkit.getScheduler().runTask(plugin, () -> {
                callback.handle(null, update);
            });
        } catch (SQLException e) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                callback.handle(e, null);
            });
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private <T> boolean isConnected(Callback<T> callback) {
        try {
            if (connection == null || connection.isClosed()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    callback.handle(new Exception("Database not connected!"), null);
                });
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void set(Object o, int index, PreparedStatement statement) throws SQLException {
        if (o instanceof String) statement.setString(index, (String) o);
        else if (o instanceof Boolean) statement.setBoolean(index, (Boolean) o);
        else if (o instanceof Long) statement.setLong(index, (Long) o);
        else if (o instanceof Integer) statement.setInt(index, (Integer) o);
    }
}
