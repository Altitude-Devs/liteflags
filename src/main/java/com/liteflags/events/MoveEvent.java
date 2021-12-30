package com.liteflags.events;

import com.liteflags.data.maps.MapCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveEvent implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (MapCache.reauthedPlayers.containsKey(player.getUniqueId().toString()))
            e.setCancelled(true);
    }
}
