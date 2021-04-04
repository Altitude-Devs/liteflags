package com.liteflags.events;

import com.liteflags.LiteFlags;
import com.liteflags.auth.Authentication;
import com.liteflags.data.database.Methods;
import com.liteflags.data.maps.MapCache;
import com.liteflags.util.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginEvent implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!player.hasPermission("liteflags.authentication.bypass")) {
            Authentication.checkAuthStatus(player.getUniqueId());
        }

        if (!Methods.hasActiveFlags(player)) {
            return;
        }

        MapCache.activeFlags.add(player.getName());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("liteflags.alertflags") && !LiteFlags.getInstance().getConfig().getString("Messages.AlertActiveFlags").equalsIgnoreCase("disablethis")) {
                int activeTotalFlags = Methods.getTotalActiveFlags(player);
                Utilities.sendStaffHoverMessage(player, onlinePlayer, Utilities.format(LiteFlags.getInstance().getConfig().getString("Messages.AlertActiveFlags").replace("%player%", player.getName()).replace("%totalactflags%", "" + activeTotalFlags).replace("%consoleFlags%", "" + Methods.consoleFlags).replace("%staffFlags%", "" + Methods.staffFlags)));
            }
        }
    }
}
