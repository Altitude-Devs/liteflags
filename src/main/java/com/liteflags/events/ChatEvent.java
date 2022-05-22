package com.liteflags.events;

import com.liteflags.LiteFlags;
import com.liteflags.config.Config;
import com.liteflags.data.database.Database;
import com.liteflags.data.database.Methods;
import com.liteflags.data.maps.MapCache;
import com.liteflags.util.Logger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatEvent implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        final Player player = e.getPlayer();

        if (!MapCache.reauthedPlayers.containsKey(player.getUniqueId().toString())) {
            return;
        }

        String value = MapCache.reauthedPlayers.get(player.getUniqueId().toString());

        if (e.getMessage().equals(value)) {
            e.setCancelled(true);

            player.sendMiniMessage(Config.AUTHENTICATE_SUCCESS, null);

            final int min = Config.MIN_RANDOM_RE_AUTH;
            final int max = Config.MAX_RANDOM_RE_AUTH;

            final String timeAmount = Config.TIME_FORMAT;

            new BukkitRunnable() {
                public void run() {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                            Config.AUTH_SUCCESS_COMMAND
                                    .replaceAll("<player>", player.getName())
                                    .replaceAll("<permission>", "liteflags.authentication.success")
                                    .replaceAll("<expiretime>", Methods.getRandomIntegerBetweenRange(min, max)
                                                    + timeAmount.substring(0, 1).toLowerCase()));
                }
            }.runTask(LiteFlags.getInstance());

            MapCache.reauthedPlayers.remove(player.getUniqueId().toString());

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (Database.inPlayerCache(player.getUniqueId())) {
                        long currentTime = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
                        int expireTime = 10080;
                        int conTime = (int) currentTime + expireTime;

                        Database.addFlag(player.getUniqueId(), (int) TimeUnit.MINUTES.toSeconds(conTime), "Possible Hacked Client - Logged out before authenticating.", "Console", "7 Days");
                        Database.removePlayerCache(player.getUniqueId());
                    }
                }
            }.runTask(LiteFlags.getInstance());

        } else {
            e.setCancelled(true);
            player.sendMiniMessage(Config.AUTHENTICATE_FAILED, TagResolver.resolver(
                    Placeholder.unparsed("code", MapCache.reauthedPlayers.get(player.getUniqueId().toString()))));
            Logger.info(player.getName() + " tried talking while authenticating: " + e.getMessage());
        }
    }
}
