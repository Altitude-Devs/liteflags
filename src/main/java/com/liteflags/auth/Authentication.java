package com.liteflags.auth;

import com.liteflags.LiteFlags;
import com.liteflags.data.maps.MapCache;
import com.liteflags.util.Utilities;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Authentication {
   public static String getAuthKey() {
      String randChars = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789abcdefghijklmnopqrstuvwsyz";
      StringBuilder salt = new StringBuilder();
      Random rnd = new Random();

      while(salt.length() < 6) {
         int index = (int)(rnd.nextFloat() * (float)randChars.length());
         salt.append(randChars.charAt(index));
      }

      return "." + salt.toString();
   }

   public static void checkAuthStatus(UUID uuid) {
      Player player = Bukkit.getPlayer(uuid);
      if (!player.hasPermission("liteflags.authentication.success")) {
         MapCache.reauthedPlayers.put(player.getUniqueId().toString(), getAuthKey());
         AuthTimer.startTimer(player);
         player.sendMessage(Utilities.format(LiteFlags.getInstance().getConfig().getString("Messages.Authenticate").replace("%code%", (CharSequence)MapCache.reauthedPlayers.get(player.getUniqueId().toString()))));
      }

   }
}
