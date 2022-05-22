package com.liteflags.auth;

import com.liteflags.data.maps.MapCache;
import com.liteflags.config.Config;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class Authentication {
    public static String getAuthKey() {
        String randChars = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789abcdefghijklmnopqrstuvwsyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();

        while (salt.length() < 6) {
            int index = (int) (rnd.nextFloat() * (float) randChars.length());
            salt.append(randChars.charAt(index));
        }

        return "." + salt;
    }

    public static void checkAuthStatus(Player player) {
        if (player.hasPermission("liteflags.authentication.success"))
            return;

        MapCache.reauthedPlayers.put(player.getUniqueId().toString(), getAuthKey());
        AuthTimer.startTimer(player);
        player.sendMiniMessage(Config.AUTHENTICATE,
                TagResolver.resolver(Placeholder.unparsed("code", MapCache.reauthedPlayers.get(player.getUniqueId().toString()))));

    }
}
