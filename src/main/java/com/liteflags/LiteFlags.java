package com.liteflags;

import com.liteflags.auth.AuthTimer;
import com.liteflags.commands.FlagCMD;
import com.liteflags.data.database.DatabaseConnection;
import com.liteflags.events.ChatEvent;
import com.liteflags.events.LoginEvent;
import com.liteflags.events.LogoutEvent;
import com.liteflags.events.MoveEvent;
import java.sql.SQLException;
import org.bukkit.plugin.java.JavaPlugin;

public class LiteFlags extends JavaPlugin {
   private static LiteFlags instance;
   public DatabaseConnection database;
   public AuthTimer timer;

   public void onEnable() {
      instance = this;
      this.saveDefaultConfig();
      this.getCommand("flag").setExecutor(new FlagCMD(this));
      this.getCommand("flaglist").setExecutor(new FlagCMD(this));
      this.getServer().getPluginManager().registerEvents(new LoginEvent(this), this);
      this.getServer().getPluginManager().registerEvents(new LogoutEvent(this), this);
      this.getServer().getPluginManager().registerEvents(new ChatEvent(this), this);
      this.getServer().getPluginManager().registerEvents(new MoveEvent(this), this);

      try {
         DatabaseConnection var10000 = this.database;
         DatabaseConnection.initialize();
      } catch (SQLException var2) {
         this.getLogger().severe("*** Could not connect to the database. ***");
         this.getLogger().severe("*** This plugin will be disabled. ***");
         this.setEnabled(false);
         var2.printStackTrace();
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
