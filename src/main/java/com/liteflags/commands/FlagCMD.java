package com.liteflags.commands;

import com.liteflags.LiteFlags;
import com.liteflags.data.database.Database;
import com.liteflags.data.database.Methods;
import com.liteflags.data.maps.MapCache;
import com.liteflags.util.Utilities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlagCMD implements CommandExecutor {
    private LiteFlags flags;

    public FlagCMD(LiteFlags flags) {
        this.flags = flags;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        OfflinePlayer target;
        String removeTimeLetter;
        if (cmd.getName().equalsIgnoreCase("flag") && sender instanceof Player) {
            if (sender.hasPermission("liteflags.staff")) {
                if (args.length == 0) {
                    sender.sendMessage(ChatColor.RED + "Invalid command. /flag help");
                }

                if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage(ChatColor.YELLOW + "/flaglist | /fl " + ChatColor.GRAY + "- Shows all the players with active flags");
                    sender.sendMessage(ChatColor.YELLOW + "/flaglist | /fl <player> " + ChatColor.GRAY + "- Shows all flags a player has");
                    sender.sendMessage(ChatColor.YELLOW + "/flag <player> <time> <reason> " + ChatColor.GRAY + "- This will flag a player for a given reason. Ex: /flag BobTheBuilder 7d Possible hacked client.");
                }

                if (args.length >= 2) {
                    if (args[0].equalsIgnoreCase("remove")) {
                        if (sender.hasPermission("liteflags.removeflags")) {
                            target = Bukkit.getServer().getOfflinePlayer(args[2]);
                            int id = Integer.parseInt(args[1]);
                            if (args.length == 4) {
                                if (!args[3].isEmpty() && args[3].equalsIgnoreCase("-c") && Database.hasFlags(target.getUniqueId())) {
                                    sender.sendMessage(ChatColor.GRAY + "You have successfully removed the flag '" + Database.getFlagReason(target.getUniqueId(), id) + "' from " + ChatColor.YELLOW + target.getName() + "'s" + ChatColor.GRAY + " flag history.");
                                    LiteFlags.getInstance().getLogger().info(sender.getName() + " has removed the flag '" + Database.getFlagReason(target.getUniqueId(), id) + "' from " + target.getName() + "'s flag history.");
                                    Database.removeFlag(target.getUniqueId(), id);
                                }
                            } else if (args.length == 3) {
                                Utilities.sendFlagConfirmMessage(sender, target, id, Database.getFlagReason(target.getUniqueId(), id));
                            }
                        } else {
                            sender.sendMessage(Utilities.format(LiteFlags.getInstance().getConfig().getString("Messages.NoPermission")));
                        }
                    } else {
                        target = Bukkit.getServer().getOfflinePlayer(args[0]);
                        if (Methods.getTotalActiveFlags(target) < LiteFlags.getInstance().getConfig().getInt("ActiveFlags.Limit")) {
                            if (target != null) {
                                if (!args[1].equals("*")) {
                                    removeTimeLetter = removeLastChar(args[1]);
                                    if (!removeTimeLetter.isEmpty()) {
                                        if (this.isInt(removeTimeLetter)) {
                                            String getLetter = getLastChar(args[1]);
                                            String[] validTimes = new String[]{"d", "h", "m"};
                                            if (Arrays.asList(validTimes).contains(getLetter)) {
                                                int number = Integer.parseInt(removeTimeLetter);
                                                int timeInMin = this.convertTimeToMinutes(number, getLetter);
                                                long currentTime = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
                                                int conTime = (int) currentTime + timeInMin;
                                                StringBuilder reason = new StringBuilder();

                                                for (int i = 2; i < args.length; ++i) {
                                                    if (i > 2) {
                                                        reason.append(" ");
                                                    }

                                                    reason.append(args[i]);
                                                }

                                                Database.addFlag(target.getUniqueId(), (long) ((int) TimeUnit.MINUTES.toSeconds((long) conTime)), reason.toString(), sender.getName(), Utilities.convertTime(timeInMin));
                                                if (target.isOnline() && !MapCache.activeFlags.contains(target.getName())) {
                                                    MapCache.activeFlags.add(target.getName());
                                                }

                                                Iterator var25 = Bukkit.getOnlinePlayers().iterator();

                                                while (var25.hasNext()) {
                                                    Player staff = (Player) var25.next();
                                                    if (staff.hasPermission("liteflags.alertflags")) {
                                                        staff.sendMessage(Utilities.format(LiteFlags.getInstance().getConfig().getString("Messages.FlaggedPlayer").replace("%staff%", sender.getName()).replace("%player%", target.getName()).replace("%flaglength%", Utilities.convertTime(timeInMin)).replace("%reason%", reason.toString())));
                                                    }
                                                }
                                            } else {
                                                sender.sendMessage(ChatColor.RED + getLetter + " is not a valid letter. (d, h, m)");
                                            }
                                        } else {
                                            sender.sendMessage(ChatColor.RED + removeTimeLetter + " is not a valid number.");
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "Invalid time argument! Example: 1d");
                                    }
                                } else if (sender.hasPermission("liteflags.flagperm")) {
                                    StringBuilder reason = new StringBuilder();

                                    for (int i = 2; i < args.length; ++i) {
                                        if (i > 2) {
                                            reason.append(" ");
                                        }

                                        reason.append(args[i]);
                                    }

                                    Database.addFlag(target.getUniqueId(), 0L, reason.toString(), sender.getName(), "Permanent");
                                    if (!MapCache.activeFlags.contains(target.getName())) {
                                        MapCache.activeFlags.add(target.getName());
                                    }

                                    Iterator var21 = Bukkit.getOnlinePlayers().iterator();

                                    while (var21.hasNext()) {
                                        Player staff = (Player) var21.next();
                                        if (staff.hasPermission("liteflags.alertflags")) {
                                            staff.sendMessage(Utilities.format(LiteFlags.getInstance().getConfig().getString("Messages.FlaggedPlayer").replace("%staff%", sender.getName()).replace("%player%", target.getName()).replace("%flaglength%", "Permanent").replace("%reason%", reason.toString())));
                                        }
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + "You do not have permission to flag permanently.");
                                }
                            } else {
                                sender.sendMessage(target.getName() + " is not a valid username!");
                            }
                        } else {
                            sender.sendMessage(Utilities.format(LiteFlags.getInstance().getConfig().getString("Messages.ActiveFlagsLimit").replace("%player%", target.getName())));
                        }
                    }
                }
            } else {
                sender.sendMessage(Utilities.format(LiteFlags.getInstance().getConfig().getString("Messages.NoPermission")));
            }
        } else if (cmd.getName().equalsIgnoreCase("flaglist")) {
            if (sender.hasPermission("liteflags.staff")) {
                if (args.length == 0) {
                    if (MapCache.activeFlags.size() > 0) {
                        Iterator var17 = MapCache.activeFlags.iterator();

                        while (var17.hasNext()) {
                            removeTimeLetter = (String) var17.next();
                            OfflinePlayer afPlayer = Bukkit.getServer().getOfflinePlayer(removeTimeLetter);
                            int totalFlags = Methods.getTotalActiveFlags(afPlayer);
                            Utilities.sendStaffHoverMessage(afPlayer, (Player) sender, Utilities.format(LiteFlags.getInstance().getConfig().getString("Messages.AlertActiveFlags").replace("%player%", afPlayer.getName()).replace("%totalactflags%", "" + totalFlags).replace("%consoleFlags%", "" + Methods.consoleFlags).replace("%staffFlags%", "" + Methods.staffFlags)));
                        }
                    } else {
                        sender.sendMessage(ChatColor.GRAY + "No players have active flags.");
                    }
                }

                if (args.length >= 1) {
                    target = Bukkit.getServer().getOfflinePlayer(args[0]);
                    this.sendFlagMessage(sender, target);
                }
            } else {
                sender.sendMessage(Utilities.format(LiteFlags.getInstance().getConfig().getString("Messages.NoPermission")));
            }
        }

        return false;
    }

    private static String getLastChar(String str) {
        return str.substring(str.length() - 1);
    }

    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    public int convertTimeToMinutes(int number, String dhm) {
        if (dhm.equalsIgnoreCase("d")) {
            return 1440 * number;
        } else {
            return dhm.equalsIgnoreCase("h") ? 60 * number : number;
        }
    }

    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException var3) {
            return false;
        }
    }

    public void sendFlagMessage(CommandSender sender, OfflinePlayer targetPlayer) {
        List<String> body = LiteFlags.getInstance().getConfig().getStringList("Messages.PlayerFlagsBody");
        int i = 0;
        //int id = false;
        boolean headerMessage = false;
        if (Database.hasFlags(targetPlayer.getUniqueId())) {
            try {
                ResultSet flag = Database.getPlayerFlags(targetPlayer.getUniqueId());

                label52:
                while (flag.next()) {
                    long expireTime = TimeUnit.SECONDS.toMinutes((long) flag.getInt("expire_time"));
                    long timeFlagged = TimeUnit.SECONDS.toMinutes((long) flag.getInt("time_flagged"));
                    long currentTime = TimeUnit.SECONDS.toMinutes(System.currentTimeMillis() / 1000L);
                    int convertedExpireTime = (int) expireTime - (int) currentTime;
                    int convertedFlaggedTime = (int) currentTime - (int) timeFlagged;
                    String flaggedBy = flag.getString("flagged_by");
                    String flagLength = flag.getString("flag_length");
                    String reason = flag.getString("reason");
                    int id = flag.getInt("id");
                    TextComponent mainComponent = null;
                    Iterator var20 = body.iterator();

                    while (true) {
                        while (true) {
                            if (!var20.hasNext()) {
                                continue label52;
                            }

                            String s = (String) var20.next();
                            s = s.replaceFirst("%player%", targetPlayer.getName());
                            s = s.replaceFirst("%staff%", flaggedBy);
                            s = s.replaceFirst("%flaglength%", flagLength);
                            s = s.replaceFirst("%reason%", reason);
                            s = s.replaceFirst("%flagtime%", Utilities.convertTime(convertedFlaggedTime));
                            if (expireTime != 0L) {
                                s = s.replaceFirst("%expiretime%", Utilities.convertTime(convertedExpireTime));
                            }

                            s = s.replaceFirst("%limit%", "" + Database.countFlags(targetPlayer.getUniqueId()));
                            s = s.replaceFirst("%nl%", " ");
                            if (convertedExpireTime < 0 && expireTime != 0L) {
                                s = s.replaceFirst("%active%", LiteFlags.getInstance().getConfig().getString("Messages.ExpiredFlags"));
                                mainComponent = new TextComponent(Utilities.format(s));
                            } else {
                                s = s.replaceFirst("%active%", LiteFlags.getInstance().getConfig().getString("Messages.ActiveFlags"));
                                mainComponent = new TextComponent(Utilities.format(s));
                            }

                            if (!headerMessage) {
                                headerMessage = true;
                                sender.sendMessage(Utilities.format(LiteFlags.getInstance().getConfig().getString("Messages.PlayerFlagsHeader").replace("%limit%", "" + Database.countFlags(targetPlayer.getUniqueId()))));
                            }

                            if (!s.contains(LiteFlags.getInstance().getConfig().getString("Messages.ActiveFlags")) && !s.contains(LiteFlags.getInstance().getConfig().getString("Messages.ExpiredFlags"))) {
                                sender.sendMessage(Utilities.format(s));
                            } else {
                                mainComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                                TextComponent removeButton = new TextComponent("[✖]");
                                removeButton.setColor(net.md_5.bungee.api.ChatColor.GRAY);
                                removeButton.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, (new ComponentBuilder(net.md_5.bungee.api.ChatColor.GRAY + "Click to remove this flag")).create()));
                                removeButton.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/flag remove " + id + " " + targetPlayer.getName()));
                                mainComponent.addExtra(removeButton);
                                sender.spigot().sendMessage(mainComponent);
                            }
                        }
                    }
                }
            } catch (SQLException var23) {
                var23.printStackTrace();
            }
        } else {
            sender.sendMessage(Utilities.format(LiteFlags.getInstance().getConfig().getString("Messages.PlayerFlagsHeader").replace("%limit%", "" + i)));
        }

    }
}
