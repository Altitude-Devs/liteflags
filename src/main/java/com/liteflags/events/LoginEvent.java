package com.liteflags.events;

import com.liteflags.LiteFlags;
import com.liteflags.auth.Authentication;
import com.liteflags.config.Config;
import com.liteflags.data.database.Methods;
import com.liteflags.data.maps.MapCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class LoginEvent implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!player.hasPermission("liteflags.authentication.bypass")) {
            Authentication.checkAuthStatus(player);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!Methods.hasActiveFlags(player)) {
                    return;
                }

                MapCache.activeFlags.add(player.getName());

                Component message = MiniMessage.miniMessage().deserialize(Config.ALERT_ACTIVE_FLAGS, TagResolver.resolver(List.of(
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.unparsed("total_act_flags", String.valueOf(Methods.getTotalActiveFlags(player))),
                        Placeholder.unparsed("console_flags", String.valueOf(Methods.consoleFlags)),
                        Placeholder.unparsed("staff_flags", String.valueOf(Methods.staffFlags))
                )));

                Bukkit.getOnlinePlayers().stream()
                        .filter(onlinePlayer -> onlinePlayer.hasPermission("liteflags.alertflags"))
                        .forEach(onlinePlayer -> onlinePlayer.sendMessage(message));
            }
        }.runTask(LiteFlags.getInstance());
    }
}
