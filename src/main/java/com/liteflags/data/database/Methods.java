package com.liteflags.data.database;

import com.liteflags.LiteFlags;
import com.liteflags.util.Utilities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.bukkit.OfflinePlayer;

public class Methods {
    public static int consoleFlags;
    public static int staffFlags;
    public static String flagListLimit = LiteFlags.getInstance().getConfig().getString("ActiveFlags.Limit");
    public static String playerFlagsHeader;

    public static boolean hasActiveFlags(OfflinePlayer player) {
        try {
            ResultSet flag = Database.getActiveTime(player.getUniqueId());

            while (flag.next()) {
                long expireTime = TimeUnit.SECONDS.toMinutes((long) flag.getInt("expire_time"));
                long currentTime = TimeUnit.SECONDS.toMinutes(System.currentTimeMillis() / 1000L);
                int convertedExpireTime = (int) expireTime - (int) currentTime;

                if (convertedExpireTime >= 0 || expireTime == 0L) {
                    return true;
                }
            }
        } catch (SQLException var7) {
            var7.printStackTrace();
        }

        return false;
    }

    public static int getTotalActiveFlags(OfflinePlayer player) {
        consoleFlags = 0;
        staffFlags = 0;
        int i = 0;

        try {
            ResultSet flag = Database.getActiveTime(player.getUniqueId());

            while (flag.next()) {
                long expireTime = TimeUnit.SECONDS.toMinutes((long) flag.getInt("expire_time"));
                long currentTime = TimeUnit.SECONDS.toMinutes(System.currentTimeMillis() / 1000L);
                int convertedExpireTime = (int) expireTime - (int) currentTime;

                if (convertedExpireTime >= 0 || expireTime == 0L) {
                    ++i;
                    if (flag.getString("flagged_by").equalsIgnoreCase("console")) {
                        ++consoleFlags;
                    } else {
                        ++staffFlags;
                    }
                }
            }
        } catch (SQLException var9) {
            var9.printStackTrace();
        }

        return i;
    }

    public static int getActiveConsoleFlags(OfflinePlayer player) {
        int i = 0;

        try {
            ResultSet flag = Database.getActiveTime(player.getUniqueId());

            while (flag.next()) {
                long expireTime = TimeUnit.SECONDS.toMinutes((long) flag.getInt("expire_time"));
                long currentTime = TimeUnit.SECONDS.toMinutes(System.currentTimeMillis() / 1000L);
                int convertedExpireTime = (int) expireTime - (int) currentTime;

                if (convertedExpireTime >= 0) {
                    String s = flag.getString("flagged_by");
                    if (s.equalsIgnoreCase("console")) {
                        ++i;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return i;
    }

    public static int getRandomIntegerBetweenRange(int min, int max) {
        int x = (int) (Math.random() * (double) (max - min + 1)) + min;
        return x;
    }

    static {
        playerFlagsHeader = Utilities.format(LiteFlags.getInstance().getConfig().getString("Messages.PlayerFlagsHeader")
                .replace("%limit%", flagListLimit));
    }
}
