package com.minestom.Managers;

import com.minestom.TimedFly;
import com.minestom.Utilities.Utility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLManager {

    private TimedFly plugin = TimedFly.getInstance();
    private String table = "timedfly_fly_time";
    private Utility utility = new Utility(plugin);

    public void createTable(TimedFly plugin) {
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " ("
                + "	UUID text,"
                + "	NAME text ,"
                + "	TIMELEFT int"
                + ");";
        try {
            if (plugin.getConnection() != null && !plugin.getConnection().isClosed()) {
                plugin.getConnection().createStatement().execute(sql);
            }
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(utility.color("&cCould not create table " + table + " report this to the developer"));
            Bukkit.getConsoleSender().sendMessage(e.getMessage());
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
                        "INSERT INTO `" + table + "` (UUID,NAME,TIMELEFT)" +
                                " VALUES (?,?,?);");
                insert.setString(1, uuid.toString());
                insert.setString(2, player.getName());
                insert.setInt(3, 0);
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

    public int getTimeLeft(Player player){
        String uuid = player.getUniqueId().toString();
        if(!playerExists(player.getUniqueId())){
            createPlayer(player);
        }
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("SELECT TIMELEFT FROM `" + table + "` WHERE UUID = ?;");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }catch (SQLException e){
            e.printStackTrace();
            return 0;
        }
    }

    public void setTimeLeft(Player player, int time){
        String uuid = player.getUniqueId().toString();
        if(!playerExists(player.getUniqueId())){
            createPlayer(player);
        }
        try {
            PreparedStatement statement = plugin.getConnection()
                    .prepareStatement("UPDATE `" + table + "` SET TIMELEFT=? WHERE UUID=?;");
            statement.setInt(1, time);
            statement.setString(2, uuid);
            statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
