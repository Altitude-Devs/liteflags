package com.liteflags.events;

import com.liteflags.LiteFlags;
import com.liteflags.data.database.Database;
import com.liteflags.data.database.Methods;
import com.liteflags.data.maps.MapCache;
import com.liteflags.util.Utilities;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ChatEvent implements Listener {
   private LiteFlags flags;

   public ChatEvent(LiteFlags flags) {
      this.flags = flags;
   }

   @EventHandler
   public void onPlayerChat(AsyncPlayerChatEvent e) {
      final Player player = e.getPlayer();
      if (MapCache.reauthedPlayers.containsKey(player.getUniqueId().toString())) {
         String value = (String)MapCache.reauthedPlayers.get(player.getUniqueId().toString());
         if (e.getMessage().equals(value)) {
            e.setCancelled(true);
            player.sendMessage(Utilities.format(LiteFlags.getInstance().getConfig().getString("Messages.Authenticate_Success")));
            long time = TimeUnit.SECONDS.toMinutes(System.currentTimeMillis() / 1000L) + 5L;
            final int min = LiteFlags.getInstance().getConfig().getInt("RandomReauth.MinNumber");
            final int max = LiteFlags.getInstance().getConfig().getInt("RandomReauth.MaxNumber");
            new Random();
            final String timeAmount = LiteFlags.getInstance().getConfig().getString("RandomReauth.TimeFormat");
            (new BukkitRunnable() {
               public void run() {
                  Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), LiteFlags.getInstance().getConfig().getString("Commands.TempAuthSuccess").replace("%player%", player.getName()).replace("%permission%", "liteflags.authentication.success").replace("%expiretime%", Methods.getRandomIntegerBetweenRange(min, max) + timeAmount.substring(0, 1).toLowerCase()));
               }
            }).runTask(LiteFlags.getInstance());
            MapCache.reauthedPlayers.remove(player.getUniqueId().toString());
            if (Database.inPlayerCache(player.getUniqueId())) {
               long currentTime = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
               int expireTime = 10080;
               int conTime = (int)currentTime + expireTime;
               Database.addFlag(player.getUniqueId(), (long)((int)TimeUnit.MINUTES.toSeconds((long)conTime)), "Possible Hacked Client - Logged out before authenticating.", "Console", "7 Days");
               Database.removePlayerCache(player.getUniqueId());
            }
         } else {
            e.setCancelled(true);
            player.sendMessage(Utilities.format(LiteFlags.getInstance().getConfig().getString("Messages.Authenticate_Failed").replace("%code%", (CharSequence)MapCache.reauthedPlayers.get(player.getUniqueId().toString()))));
            LiteFlags.getInstance().getLogger().info(player.getName() + " tried talking while authenticating: " + e.getMessage());
         }
      }

   }
}
