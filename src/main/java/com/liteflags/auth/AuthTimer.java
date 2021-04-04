package com.liteflags.auth;

import com.liteflags.LiteFlags;
import com.liteflags.data.maps.MapCache;
import com.liteflags.util.Utilities;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AuthTimer<taskID> {
   public LiteFlags flags;
   public static Map<UUID, Integer> taskID = new HashMap();

   public AuthTimer(LiteFlags flags) {
      this.flags = flags;
   }

   public static void startTimer(final Player player) {
      int tid = LiteFlags.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(LiteFlags.getInstance(), new Runnable() {
         int timeRemaining = LiteFlags.getInstance().getConfig().getInt("Authenticate_Timer");

         public void run() {
            if (this.timeRemaining <= 0) {
               if (player.hasPermission("liteflags.authentication.success")) {
                  AuthTimer.endTask(player);
               } else {
                  player.sendMessage(Utilities.format(LiteFlags.getInstance().getConfig().getString("Messages.Authenticate").replace("%code%", (CharSequence)MapCache.reauthedPlayers.get(player.getUniqueId().toString()))));
                  Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), LiteFlags.getInstance().getConfig().getString("Authenticate_Timer_Command").replace("%player%", player.getName()).replace("%code%", (CharSequence)MapCache.reauthedPlayers.get(player.getUniqueId().toString())));
                  this.timeRemaining = LiteFlags.getInstance().getConfig().getInt("Authenticate_Timer");
               }
            } else if (player.hasPermission("liteflags.authentication.success")) {
               AuthTimer.endTask(player);
            } else {
               --this.timeRemaining;
            }

         }
      }, 0L, 20L);
      Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), LiteFlags.getInstance().getConfig().getString("Authenticate_Timer_Command").replace("%player%", player.getName()).replace("%code%", (CharSequence)MapCache.reauthedPlayers.get(player.getUniqueId().toString())));
      taskID.put(player.getUniqueId(), tid);
   }

   public static void endTask(Player player) {
      if (taskID.containsKey(player.getUniqueId())) {
         int tid = (Integer)taskID.get(player.getUniqueId());
         LiteFlags.getInstance().getServer().getScheduler().cancelTask(tid);
         taskID.remove(player.getUniqueId());
      }

   }
}
