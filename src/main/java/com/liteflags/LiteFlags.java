package com.liteflags;

import com.liteflags.auth.AuthTimer;
import com.liteflags.commands.CommandManager;
import com.liteflags.data.database.DatabaseConnection;
import com.liteflags.events.ChatEvent;
import com.liteflags.events.LoginEvent;
import com.liteflags.events.LogoutEvent;
import com.liteflags.events.MoveEvent;
import java.sql.SQLException;

import com.liteflags.config.Config;
import org.bukkit.plugin.java.JavaPlugin;

public class LiteFlags extends JavaPlugin {
   private static LiteFlags instance;
   public DatabaseConnection database;
   public AuthTimer timer;

   public void onEnable() {
      instance = this;
      Config.reload();
      this.getCommand("flag").setExecutor(new CommandManager());
      this.getServer().getPluginManager().registerEvents(new LoginEvent(), this);
      this.getServer().getPluginManager().registerEvents(new LogoutEvent(), this);
      this.getServer().getPluginManager().registerEvents(new ChatEvent(), this);
      this.getServer().getPluginManager().registerEvents(new MoveEvent(), this);

      try {
         DatabaseConnection.initialize();
      } catch (SQLException exception) {
         this.getLogger().severe("*** Could not connect to the database. ***");
         this.getLogger().severe("*** This plugin will be disabled. ***");
         this.setEnabled(false);
         exception.printStackTrace();
      }

   }

   public void onDisable() {
   }

   public static LiteFlags getInstance() {
      return instance;
   }

   public AuthTimer authTimer() {
      return this.timer;
   }
}
