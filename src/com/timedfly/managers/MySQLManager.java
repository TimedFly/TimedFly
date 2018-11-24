package com.timedfly.managers;

import com.github.entrypointkr.timedfly.SqlProcessor;
import com.timedfly.TimedFly;
import com.timedfly.utilities.SqlSetup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLManager {
    private String table = "timedfly_fly_time";
    private SqlSetup sqlSetup;

    public MySQLManager(SqlSetup sqlSetup) {
        this.sqlSetup = sqlSetup;
    }

    private boolean playerExists(UUID uuid) {
        Connection connection = this.sqlSetup.getConnection();

        try {
            if (connection != null && !connection.isClosed()) {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + this.table + "` WHERE UUID=?;");
                statement.setString(1, uuid.toString());
                ResultSet results = statement.executeQuery();
                boolean containsPlayer = results.next();
                statement.close();
                results.close();
                return containsPlayer;
            } else {
                return false;
            }
        } catch (SQLException var6) {
            var6.printStackTrace();
            return false;
        }
    }

    public void createPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (!this.playerExists(uuid)) {
            Connection connection = this.sqlSetup.getConnection();

            try {
                if (connection == null || connection.isClosed()) {
                    return;
                }

                PreparedStatement insert = connection.prepareStatement("INSERT INTO `" + this.table + "` (UUID,NAME,TIMELEFT,INITIALTIME,TimeStopped) VALUES (?,?,?,?,?);");
                insert.setString(1, uuid.toString());
                insert.setString(2, player.getName());
                insert.setInt(3, 0);
                insert.setInt(4, 0);
                insert.setBoolean(5, false);
                insert.execute();
                insert.close();
            } catch (SQLException var5) {
                var5.printStackTrace();
            }

        }
    }

    public void saveData(Player player, int timeleft, int initial, boolean manuallyStopped) {
        String uuid = player.getUniqueId().toString();
        Connection connection = this.sqlSetup.getConnection();

        try {
            if (connection == null || connection.isClosed()) {
                return;
            }

            PreparedStatement statement = connection.prepareStatement("UPDATE `" + this.table + "` SET TIMELEFT = ?, INITIALTIME = ?, TimeStopped = ? WHERE UUID = ?;");
            statement.setInt(1, timeleft);
            statement.setInt(2, initial);
            statement.setBoolean(3, manuallyStopped);
            statement.setString(4, uuid);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException var8) {
            var8.printStackTrace();
        }

    }

    public void saveDataAsync(Player player, int timeleft, int initial, boolean manuallyStopped) {
        TimedFly timedFly = JavaPlugin.getPlugin(TimedFly.class);
        SqlProcessor processor = timedFly.getSqlProcessor();
        processor.queue(connection -> {
            String uuid = player.getUniqueId().toString();
            try {
                if (connection == null || connection.isClosed()) {
                    return;
                }

                try (PreparedStatement statement = connection.prepareStatement("UPDATE `" + this.table + "` SET TIMELEFT = ?, INITIALTIME = ?, TimeStopped = ? WHERE UUID = ?;")) {
                    statement.setInt(1, timeleft);
                    statement.setInt(2, initial);
                    statement.setBoolean(3, manuallyStopped);
                    statement.setString(4, uuid);
                    statement.executeUpdate();
                }
            } catch (SQLException var8) {
                var8.printStackTrace();
            }
        });
    }

    public int getTimeLeft(Player player) {
        String uuid = player.getUniqueId().toString();
        Connection connection = this.sqlSetup.getConnection();

        try {
            if (connection != null && !connection.isClosed()) {
                PreparedStatement statement = connection.prepareStatement("SELECT TIMELEFT FROM `" + this.table + "` WHERE UUID = ?;");
                statement.setString(1, uuid);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                Integer timeleft = resultSet.getInt(1);
                resultSet.close();
                return timeleft;
            } else {
                return 0;
            }
        } catch (SQLException var7) {
            var7.printStackTrace();
            return 0;
        }
    }

    public int getInitialTime(Player player) {
        String uuid = player.getUniqueId().toString();
        Connection connection = this.sqlSetup.getConnection();

        try {
            if (connection != null && !connection.isClosed()) {
                PreparedStatement statement = connection.prepareStatement("SELECT INITIALTIME FROM `" + this.table + "` WHERE UUID = ?;");
                statement.setString(1, uuid);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                Integer initial = resultSet.getInt(1);
                resultSet.close();
                return initial;
            } else {
                return 0;
            }
        } catch (SQLException var7) {
            var7.printStackTrace();
            return 0;
        }
    }

    public boolean getManuallyStopped(Player player) {
        String uuid = player.getUniqueId().toString();
        Connection connection = this.sqlSetup.getConnection();

        try {
            if (connection != null && !connection.isClosed()) {
                PreparedStatement statement = connection.prepareStatement("SELECT TimeStopped FROM `" + this.table + "` WHERE UUID = ?;");
                statement.setString(1, uuid);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                boolean initial = resultSet.getBoolean(1);
                resultSet.close();
                return initial;
            } else {
                return false;
            }
        } catch (SQLException var7) {
            var7.printStackTrace();
            return false;
        }
    }
}
