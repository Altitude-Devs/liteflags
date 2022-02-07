package com.liteflags.auth;

import com.liteflags.LiteFlags;
import com.liteflags.config.Config;
import com.liteflags.data.maps.MapCache;
import com.liteflags.util.Utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AuthTimer<taskID> {
    public LiteFlags flags;
    public static Map<UUID, Integer> taskID = new HashMap<>();

    public AuthTimer(LiteFlags flags) {
        this.flags = flags;
    }

    public static void startTimer(final Player player) {
        final String uuid = player.getUniqueId().toString();

        int tid = LiteFlags.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(LiteFlags.getInstance(), new Runnable() {
            int timeRemaining = Config.AUTH_MESSAGE_REPEAT_TIMER;

            public void run() {
                if (this.timeRemaining <= 0) {
                    if (player.hasPermission("liteflags.authentication.success")) {
                        AuthTimer.endTask(player);
                    } else {
                        final String code = MapCache.reauthedPlayers.get(uuid);
                        if (code == null)
                            return;
                        player.sendMiniMessage(Config.AUTHENTICATE, List.of(
                                Template.template("code", code)));
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Config.AUTH_MESSAGE_COMMAND
                                .replaceAll("<player>", player.getName())
                                .replaceAll("<code>", code));
                        this.timeRemaining = Config.AUTH_MESSAGE_REPEAT_TIMER;
                    }
                } else if (player.hasPermission("liteflags.authentication.success")) {
                    AuthTimer.endTask(player);
                } else {
                    --this.timeRemaining;
                }

            }
        }, 0L, 20L);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Config.AUTH_MESSAGE_COMMAND
                .replaceAll("<player>", player.getName())
                .replaceAll("<code>", MapCache.reauthedPlayers.get(uuid)));
        taskID.put(player.getUniqueId(), tid);
    }

    public static void endTask(Player player) {
        if (taskID.containsKey(player.getUniqueId())) {
            int tid = taskID.get(player.getUniqueId());
            LiteFlags.getInstance().getServer().getScheduler().cancelTask(tid);
            taskID.remove(player.getUniqueId());
        }

    }
}
