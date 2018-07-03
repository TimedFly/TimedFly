package com.timedfly.utilities;

import com.timedfly.configurations.ConfigCache;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlSetup {

    private final Plugin plugin;
    private Connection connection;

    public SqlSetup(Plugin plugin) {
        this.plugin = plugin;
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS timedfly_fly_time ("
                + "	UUID text,"
                + "	NAME text ,"
                + "	TIMELEFT int,"
                + "	INITIALTIME int,"
                + " TimeStopped boolean"
                + ");";
        try {
            if (this.getConnection() != null && !this.getConnection().isClosed()) {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.execute();
                preparedStatement.close();
            }
        } catch (SQLException e) {
            Message.sendConsoleMessage("&cSQL ERROR: " + e.getMessage());
            Message.sendConsoleMessage("&cSQL ERROR: Could not create table &6timedfly_fly_time &creport this to the developer");
        }
    }

    public boolean mysqlSetup() {
        String host = ConfigCache.getMysqlHost();
        String port = ConfigCache.getMysqlPort();
        String database = ConfigCache.getMysqlDB();
        String username = ConfigCache.getMysqlUser();
        String password = ConfigCache.getMysqlPasss();

        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) return false;

                if (ConfigCache.getSqlType().equalsIgnoreCase("sqlite")) {
                    Class.forName("org.sqlite.JDBC");
                    String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/SQLite.db";
                    setConnection(DriverManager.getConnection(url));
                    Message.sendConsoleMessage("&7Successfully connected to &cSQLite &7database.");
                    this.createTable();
                    return true;
                } else if (ConfigCache.getSqlType().equalsIgnoreCase("mysql")) {
                    Class.forName("com.mysql.jdbc.Driver");
                    setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database
                            + "?autoReconnect=true", username, password));
                    Message.sendConsoleMessage("&7Successfully connected to &cMySQL &7database.");
                    this.createTable();
                    return true;
                } else {
                    Message.sendConsoleMessage("&cCould not connect to any database. Please use sqlite or mysql in your MySQL file.");
                    return false;
                }
            }
        } catch (SQLException e) {
            Message.sendConsoleMessage("&cCould not connect to MySQL database, check yor credentials.");
            Message.sendConsoleMessage(e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            Message.sendConsoleMessage(e.getMessage());
            return false;
        }
    }

    public void closeConnection() {
        try {
            Connection connection = getConnection();
            if (connection != null && connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void setConnection(Connection connection) {
        this.connection = connection;
    }
}
