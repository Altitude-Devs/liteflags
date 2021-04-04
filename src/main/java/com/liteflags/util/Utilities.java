package com.liteflags.util;

import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Utilities {
    public static String convertTime(int time) {
        int days = (int) TimeUnit.MINUTES.toDays(time);
        int hours = (int) (TimeUnit.MINUTES.toHours(time) - TimeUnit.DAYS.toHours(days));
        int minutes = (int) (TimeUnit.MINUTES.toMinutes(time) - TimeUnit.HOURS.toMinutes(hours) - TimeUnit.DAYS.toMinutes(days));

        String timeString = formatTime(days, " day");
        timeString += (timeString.length() == 0 ? "" : ", ") + formatTime(hours, " hour");
        timeString += (timeString.length() == 0 ? "" : ", ") + formatTime(minutes, " minute");

        if (timeString.length() == 0) timeString = "0 minutes";

        return timeString;
    }

    private static String formatTime(int value, String s) {
        switch (value) {
            case 0:
                return "";
            case 1:
                return value + s;
            default:
                return value + s + "s";
        }
    }

    public static String format(String m) {
        return ChatColor.translateAlternateColorCodes('&', m);
    }

    public static void sendStaffHoverMessage(OfflinePlayer targetPlayer, Player staffPlayer, String mainString) {
        TextComponent mainComponent = new TextComponent(mainString);
        mainComponent.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.GRAY + "Click to view " + targetPlayer.getName() + "'s flags")).create()));
        mainComponent.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/flaglist " + targetPlayer.getName()));
        staffPlayer.spigot().sendMessage(mainComponent);
    }

    public static void sendFlagConfirmMessage(CommandSender sender, OfflinePlayer targetPlayer, int id, String flagReason) {
        TextComponent mainComponent = new TextComponent("Are you sure you want to remove the flag '" + flagReason + "' from " + ChatColor.YELLOW + targetPlayer.getName() + "'s" + ChatColor.GRAY + " flag history? ");
        mainComponent.setColor(ChatColor.GRAY);
        TextComponent confirmButton = new TextComponent("[Confirm]");
        confirmButton.setColor(ChatColor.GREEN);
        confirmButton.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, (new ComponentBuilder(ChatColor.GREEN + "Click to confirm")).create()));
        confirmButton.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/flag remove " + id + " " + targetPlayer.getName() + " -c"));
        mainComponent.addExtra(confirmButton);
        sender.spigot().sendMessage(mainComponent);
    }

    public static TextComponent textComponent(TextComponent component) {
        return component;
    }

    public static void sendMessage(Player player, TextComponent component) {
        player.spigot().sendMessage(component);
    }
}
