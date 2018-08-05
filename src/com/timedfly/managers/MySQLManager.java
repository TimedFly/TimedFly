package com.timedfly.managers;

import com.timedfly.utilities.SqlSetup;
import org.bukkit.entity.Player;

import java.sql.Connection;
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
            if (connection == null || connection.isClosed()) return false;

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + table + "` WHERE UUID=?;");
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
        if (playerExists(uuid)) return;
        Connection connection = this.sqlSetup.getConnection();

        try {
            if (connection == null || connection.isClosed()) return;

            PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO `" + table + "` (UUID,NAME,TIMELEFT,INITIALTIME,TimeStopped) VALUES (?,?,?,?,?);");
            insert.setString(1, uuid.toString());
            insert.setString(2, player.getName());
            insert.setInt(3, 0);
            insert.setInt(4, 0);
            insert.setBoolean(5, false);
            insert.execute();
            insert.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveData(Player player, int timeleft, int initial) {
        String uuid = player.getUniqueId().toString();
        Connection connection = this.sqlSetup.getConnection();

        try {
            if (connection == null || connection.isClosed()) return;

            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE `" + table + "` SET TIMELEFT = ?, INITIALTIME = ?, TimeStopped = ? WHERE UUID = ?;");

            statement.setInt(1, timeleft);
            statement.setInt(2, initial);
            statement.setBoolean(3, false);
            statement.setString(4, uuid);

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTimeLeft(Player player) {
        String uuid = player.getUniqueId().toString();
        Connection connection = this.sqlSetup.getConnection();

        try {
            if (connection == null || connection.isClosed()) return 0;

            PreparedStatement statement = connection.prepareStatement("SELECT TIMELEFT FROM `" + table + "` WHERE UUID = ?;");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Integer timeleft = resultSet.getInt(1);
            resultSet.close();
            return timeleft;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getInitialTime(Player player) {
        String uuid = player.getUniqueId().toString();
        Connection connection = this.sqlSetup.getConnection();

        try {
            if (connection == null || connection.isClosed()) return 0;

            PreparedStatement statement = connection.prepareStatement("SELECT INITIALTIME FROM `" + table + "` WHERE UUID = ?;");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Integer initial = resultSet.getInt(1);
            resultSet.close();
            return initial;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
