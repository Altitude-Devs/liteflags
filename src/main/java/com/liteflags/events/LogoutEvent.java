package com.liteflags.events;

import com.liteflags.LiteFlags;
import com.liteflags.auth.AuthTimer;
import com.liteflags.data.database.Database;
import com.liteflags.data.maps.MapCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LogoutEvent implements Listener {
    private LiteFlags flags;

    public LogoutEvent(LiteFlags flags) {
        this.flags = flags;
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (AuthTimer.taskID.containsKey(player.getUniqueId())) {
            AuthTimer.endTask(player);
        }

        if (MapCache.reauthedPlayers.containsKey(player.getUniqueId().toString())) {
            Database.addPlayerCache(player.getUniqueId(), player.getName());
            MapCache.reauthedPlayers.remove(player.getUniqueId().toString());
        }

        MapCache.activeFlags.remove(player.getName());

    }
}
