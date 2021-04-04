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
      int hours = (int)(TimeUnit.MINUTES.toHours(time) - TimeUnit.DAYS.toHours(days));
      int minutes = (int)(TimeUnit.MINUTES.toMinutes(time) - TimeUnit.HOURS.toMinutes(hours) - TimeUnit.DAYS.toMinutes(days));
      String d = days + " day";
      String h = hours + " hour";
      String m = minutes + " minute";
      String c = ", ";
      if (days == 0) {
         if (hours == 0) {
            return minutes == 1 ? m : m + "s";
         } else if (hours == 1) {
            if (minutes == 1) {
               return h + c + m;
            } else {
               return minutes == 0 ? h : h + c + m + "s";
            }
         } else if (minutes == 1) {
            return h + "s" + c + m;
         } else {
            return minutes == 0 ? h + "s" : h + "s" + c + m + "s";
         }
      } else if (days == 1) {
         if (hours == 1) {
            if (minutes == 1) {
               return d + c + h + c + m;
            } else {
               return minutes == 0 ? d + c + h : d + c + h + c + m + "s";
            }
         } else if (minutes == 1) {
            return hours == 0 ? d + c + m : d + c + h + "s" + c + m;
         } else if (minutes == 0) {
            return hours == 0 ? d : d + c + h + "s";
         } else {
            return hours == 0 ? d + c + m + "s" : d + c + h + "s" + c + m + "s";
         }
      } else if (hours == 1) {
         if (minutes == 1) {
            return d + "s" + c + h + c + m;
         } else {
            return minutes == 0 ? d + "s" + c + h : d + "s" + c + h + c + m + "s";
         }
      } else if (minutes == 1) {
         return hours == 0 ? d + "s" + c + m : d + "s" + c + h + "s" + c + m;
      } else if (minutes == 0) {
         return hours == 0 ? d + "s" : d + "s" + c + h + "s";
      } else {
         return hours == 0 ? d + "s" + c + m + "s" : d + "s" + c + h + "s" + c + m + "s";
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
