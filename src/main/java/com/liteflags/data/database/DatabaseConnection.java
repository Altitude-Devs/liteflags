package com.liteflags.data.database;

import com.liteflags.config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static DatabaseConnection instance;
    public Connection connection;
    public String drivers = Config.DRIVERS;
    public String ip = Config.IP;
    public String port = Config.PORT;
    public String database = Config.DATABASE;
    public String username = Config.USERNAME;
    public String password = Config.PASSWORD;

    public DatabaseConnection() throws SQLException {
        instance = this;
        instance.openConnection();
        Database.createTables();
    }

    public void openConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            synchronized (this) {
                if (this.connection == null || this.connection.isClosed()) {
                    this.connection = DriverManager.getConnection("jdbc:" + this.drivers + "://" + this.ip + ":" + this.port + "/" + this.database + "?autoReconnect=true&useSSL=false", this.username, this.password);
                }
            }
        }
    }

    public static Connection getConnection() {
        try {
            instance.openConnection();
        } catch (SQLException var1) {
            var1.printStackTrace();
        }

        return instance.connection;
    }

    public static void initialize() throws SQLException {
        instance = new DatabaseConnection();
    }
}
