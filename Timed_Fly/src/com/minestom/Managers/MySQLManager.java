package com.minestom.Managers;

import com.minestom.TimedFly;
import com.minestom.Utilities.Utility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLManager {

    private TimedFly plugin;
    private String table = "timedfly_fly_time";
    private Utility utility;

    public MySQLManager(TimedFly plugin) {
        this.plugin = plugin;
        utility = plugin.getUtility();
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " ("
                + "	UUID text,"
                + "	NAME text ,"
                + "	TIMELEFT int,"
                + "	INITIALTIME int,"
                + " TimeStopped boolean"
                + ");";
        try {
            if (plugin.getConnection() != null && !plugin.getConnection().isClosed()) {
                plugin.getConnection().createStatement().execute(sql);
                updateTable();
            }
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(utility.color("&cSQL ERROR: ") + e.getMessage());
            Bukkit.getConsoleSender().sendMessage(utility.color("&cSQL ERROR: Could not create table " + table + " report this to the developer"));
        }
    }

    private void updateTable() {
        String sql = "ALTER TABLE " + table + " ADD COLUMN INITIALTIME int(11) NOT NULL DEFAULT 0;";
        String sql2 = "ALTER TABLE " + table + " ADD COLUMN TimeStopped boolean NOT NULL DEFAULT 0";
        try {

            DatabaseMetaData md = plugin.getConnection().getMetaData();
            ResultSet rs = md.getColumns(null, null, table, "INITIALTIME");
            ResultSet rs2 = md.getColumns(null, null, table, "TimeStopped");
            if (!rs2.next()) {
                PreparedStatement statement2 = plugin.getConnection().prepareStatement(sql2);
                statement2.executeUpdate();
                Bukkit.getConsoleSender().sendMessage(utility.color("&cSQL ERROR: Your table is not up to date. Updating your SQL table..."));
                statement2.close();
            }
            if (!rs.next()) {
                PreparedStatement statement = plugin.getConnection().prepareStatement(sql);
                statement.executeUpdate();
                Bukkit.getConsoleSender().sendMessage(utility.color("&cSQL ERROR: Your table is not up to date. &7Updating your SQL table..."));
                statement.close();
            }

        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(utility.color("&cSQL ERROR: ") + e.getMessage());
            Bukkit.getConsoleSender().sendMessage(utility.color("&cSQL ERROR: report this to the developer. (UPDATE)"));
        }
    }

    private boolean playerExists(UUID uuid) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM `" + table + "` WHERE UUID=?;");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            boolean containsPlayer = results.next();
            statement.close();
            results.close();
            return containsPlayer;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void createPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        try {
            if (!playerExists(uuid)) {
                PreparedStatement insert = plugin.getConnection().prepareStatement(
                        "INSERT INTO `" + table + "` (UUID,NAME,TIMELEFT,INITIALTIME,TimeStopped)" +
                                " VALUES (?,?,?,?,?);");
                insert.setString(1, uuid.toString());
                insert.setString(2, player.getName());
                insert.setInt(3, 0);
                insert.setInt(4, 0);
                insert.setBoolean(5, false);
                insert.execute();
                insert.close();
            } else {
                PreparedStatement statement = plugin.getConnection()
                        .prepareStatement("SELECT * FROM `" + table + "` WHERE UUID = ?;");
                statement.setString(1, uuid.toString());
                ResultSet results = statement.executeQuery();
                results.next();
                statement.close();
                results.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTimeLeft(Player player) {
        String uuid = player.getUniqueId().toString();
        if (!playerExists(player.getUniqueId())) {
            createPlayer(player);
        }
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT TIMELEFT FROM `" + table + "` WHERE UUID = ?;");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setTimeLeft(Player player, int time) {
        String uuid = player.getUniqueId().toString();
        if (!playerExists(player.getUniqueId())) {
            createPlayer(player);
        }
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("UPDATE `" + table + "` SET TIMELEFT=? WHERE UUID=?;");
            statement.setInt(1, time);
            statement.setString(2, uuid);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getInitialTime(Player player) {
        String uuid = player.getUniqueId().toString();
        if (!playerExists(player.getUniqueId())) {
            createPlayer(player);
        }
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT INITIALTIME FROM `" + table + "` WHERE UUID = ?;");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setInitialTime(Player player, int time) {
        String uuid = player.getUniqueId().toString();
        if (!playerExists(player.getUniqueId())) {
            createPlayer(player);
        }
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("UPDATE `" + table + "` SET INITIALTIME=? WHERE UUID=?;");
            statement.setInt(1, time);
            statement.setString(2, uuid);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isTimeStopped(Player player) {
        String uuid = player.getUniqueId().toString();
        if (!playerExists(player.getUniqueId())) {
            createPlayer(player);
        }
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT TimeStopped FROM `" + table + "` WHERE UUID = ?;");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setTimeStopped(Player player, boolean b) {
        String uuid = player.getUniqueId().toString();
        if (!playerExists(player.getUniqueId())) {
            createPlayer(player);
        }
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("UPDATE `" + table + "` SET TimeStopped=? WHERE UUID=?;");
            statement.setBoolean(1, b);
            statement.setString(2, uuid);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
