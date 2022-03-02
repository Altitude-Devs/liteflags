package com.liteflags.commands.subcommands;

import com.liteflags.LiteFlags;
import com.liteflags.commands.SubCommand;
import com.liteflags.config.Config;
import com.liteflags.data.database.Database;
import com.liteflags.data.database.Methods;
import com.liteflags.data.maps.MapCache;
import com.liteflags.util.Utilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandFlagPlayer extends SubCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        if (args.length < 2) {
            commandSender.sendMiniMessage(getHelpMessage(), null);
            return true;
        }

        OfflinePlayer target = Bukkit.getServer().getOfflinePlayerIfCached(args[0]);
        if (target == null) {
            commandSender.sendMiniMessage(Config.UNKNOWN_PLAYER, List.of(Template.template("player", args[2])));
            return true;
        }

        if (Config.MAX_ACTIVE_FLAGS >= 0 && Methods.getTotalActiveFlags(target) >= Config.MAX_ACTIVE_FLAGS) {
            commandSender.sendMiniMessage(Config.ACTIVE_FLAGS_LIMIT, List.of(
                    Template.template("player", target.getName() == null ? target.getUniqueId().toString() : target.getName())
            ));
            return true;
        }

        if (args[1].equals("*")) {
            permFlag(commandSender, args, target);
        } else {
            tempFlag(commandSender, args, target);
        }
        return true;
    }

    private void permFlag(CommandSender sender, String[] args, OfflinePlayer target) {
        if (!sender.hasPermission("liteflags.flagperm")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to flag permanently.");
            return;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        new BukkitRunnable() {
            @Override
            public void run() {
                Database.addFlag(target.getUniqueId(), 0L, reason, sender.getName(), "Permanent");
            }
        }.runTask(LiteFlags.getInstance());

        if (!MapCache.activeFlags.contains(target.getName())) {
            MapCache.activeFlags.add(target.getName());
        }

        Component message = MiniMessage.miniMessage().deserialize(Config.FLAGGED_PLAYER, TemplateResolver.templates(List.of(
                Template.template("staff", sender.getName()),
                Template.template("player", target.getName() == null ? target.getUniqueId().toString() : target.getName()),
                Template.template("flag_length", "Permanent"),
                Template.template("reason", reason)
        )));

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("liteflags.alertflags"))
                .forEach(player -> player.sendMessage(message));
    }

    private void tempFlag(CommandSender commandSender, String[] args, OfflinePlayer target) {
        if (args[1].length() < 2) {
            commandSender.sendMiniMessage(Config.INVALID_TIME_ARGUMENT, List.of(Template.template("arg", args[1])));
            return;
        }

        String time = args[1].substring(0, args[1].length() - 1);
        String letter = args[1].substring(args[1].length() - 1);
        String[] validTimes = new String[]{"d", "h", "m"};
        if (!time.matches("[1-9][0-9]{0,8}") || !Arrays.asList(validTimes).contains(letter)) {
            commandSender.sendMiniMessage(Config.INVALID_TIME_ARGUMENT, List.of(Template.template("arg", args[1])));
            return;
        }

        int number = Integer.parseInt(time);
        int timeInMin = this.convertTimeToMinutes(number, letter);
        long currentTime = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
        int conTime = (int) currentTime + timeInMin;
        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        new BukkitRunnable() {
            @Override
            public void run() {
                Database.addFlag(target.getUniqueId(), (int) TimeUnit.MINUTES.toSeconds(conTime), reason, commandSender.getName(), Utilities.convertTime(timeInMin));
            }
        }.runTask(LiteFlags.getInstance());

        if (target.isOnline() && !MapCache.activeFlags.contains(target.getName())) {
            MapCache.activeFlags.add(target.getName());
        }

        Component message = MiniMessage.miniMessage().deserialize(Config.FLAGGED_PLAYER, TemplateResolver.templates(List.of(
                Template.template("staff", commandSender.getName()),
                Template.template("player", target.getName() == null ? target.getUniqueId().toString() : target.getName()),
                Template.template("flag_length", Utilities.convertTime(timeInMin)),
                Template.template("reason", reason)
        )));

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("liteflags.alertflags"))
                .forEach(player -> player.sendMessage(message));
    }

    public int convertTimeToMinutes(int number, String dhm) {
        if (dhm.equalsIgnoreCase("d")) {
            return 1440 * number;
        } else {
            return dhm.equalsIgnoreCase("h") ? 60 * number : number;
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<String> getTabComplete(CommandSender commandSender, String[] args) {
        ArrayList<String> res = new ArrayList<>();
        if (args.length == 2)
            for (int i = 1; i <= 9; i++) {
                if (args[1].isEmpty() || args[1].startsWith(String.valueOf(i)))
                    res.add(i + "d");
            }
        return res;
    }

    @Override
    public String getHelpMessage() {
        return Config.HELP_FLAG_PLAYER;
    }

    @Override
    public String getPermission() {
        return "liteflags.flagplayer";
    }
}
