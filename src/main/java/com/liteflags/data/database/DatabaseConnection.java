package com.liteflags.data.database;

import com.liteflags.LiteFlags;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
   public static DatabaseConnection instance;
   public Connection connection;
   public String drivers = LiteFlags.getInstance().getConfig().getString("Database.Drivers");
   public String ip = LiteFlags.getInstance().getConfig().getString("Database.IP");
   public String port = LiteFlags.getInstance().getConfig().getString("Database.Port");
   public String database = LiteFlags.getInstance().getConfig().getString("Database.Database");
   public String username = LiteFlags.getInstance().getConfig().getString("Database.Username");
   public String password = LiteFlags.getInstance().getConfig().getString("Database.Password");

   public DatabaseConnection() throws SQLException {
      instance = this;
      instance.openConnection();
      Database.createTables();
   }

   public void openConnection() throws SQLException {
      if (this.connection == null || this.connection.isClosed()) {
         synchronized(this) {
            if (this.connection == null || this.connection.isClosed()) {
               this.connection = DriverManager.getConnection("jdbc:" + this.drivers + "://" + this.ip + ":" + this.port + "/" + this.database, this.username, this.password);
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
