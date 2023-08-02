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
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
            commandSender.sendMiniMessage(Config.UNKNOWN_PLAYER, TagResolver.resolver(Placeholder.unparsed("player", args[2])));
            return true;
        }

        if (Config.MAX_ACTIVE_FLAGS >= 0 && Methods.getTotalActiveFlags(target) >= Config.MAX_ACTIVE_FLAGS) {
            commandSender.sendMiniMessage(Config.ACTIVE_FLAGS_LIMIT, TagResolver.resolver(
                    Placeholder.unparsed("player", target.getName() == null ? target.getUniqueId().toString() : target.getName())
            ));
            return true;
        }

        if (args[1].equals("*")) {
            if (!commandSender.hasPermission(getPermission() + ".variable-time")) {
                commandSender.sendMiniMessage(Config.VARIABLE_LENGTH_NOT_ALLOWED, null);
                return true;
            }
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

        Component message = MiniMessage.miniMessage().deserialize(Config.FLAGGED_PLAYER, TagResolver.resolver(List.of(
                Placeholder.unparsed("staff", sender.getName()),
                Placeholder.unparsed("player", target.getName() == null ? target.getUniqueId().toString() : target.getName()),
                Placeholder.unparsed("flag_length", "Permanent"),
                Placeholder.unparsed("reason", reason)
        )));

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("liteflags.alertflags"))
                .forEach(player -> player.sendMessage(message));
    }

    private void tempFlag(CommandSender commandSender, String[] args, OfflinePlayer target) {
        Optional<Duration> flagDurationIfExists = getFlagDurationIfExists(args[1]);
        Instant expireTime = Instant.now();
        int firstArg = 2;
        Duration flagDuration;
        if (flagDurationIfExists.isPresent() && !commandSender.hasPermission(getPermission() + ".variable-time")) {
            commandSender.sendMiniMessage(Config.VARIABLE_LENGTH_NOT_ALLOWED, null);
            return;
        }
        if (flagDurationIfExists.isEmpty()) {
            firstArg = 1;
            flagDuration = Duration.ofDays(Config.DEFAULT_FLAG_LENGTH_DAYS);
        } else {
            flagDuration = flagDurationIfExists.get();
        }
        expireTime = expireTime.plusSeconds(flagDuration.toSeconds());
        String reason = String.join(" ", Arrays.copyOfRange(args, firstArg, args.length));

        Instant finalExpireTime = expireTime;
        new BukkitRunnable() {
            @Override
            public void run() {
                Database.addFlag(target.getUniqueId(), finalExpireTime, reason, commandSender.getName(), Utilities.convertTime(flagDuration));
            }
        }.runTask(LiteFlags.getInstance());

        if (target.isOnline() && !MapCache.activeFlags.contains(target.getName())) {
            MapCache.activeFlags.add(target.getName());
        }

        Component message = MiniMessage.miniMessage().deserialize(Config.FLAGGED_PLAYER, TagResolver.resolver(List.of(
                Placeholder.unparsed("staff", commandSender.getName()),
                Placeholder.unparsed("player", target.getName() == null ? target.getUniqueId().toString() : target.getName()),
                Placeholder.unparsed("flag_length", Utilities.convertTime(flagDuration)),
                Placeholder.unparsed("reason", reason)
        )));

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("liteflags.alertflags"))
                .forEach(player -> player.sendMessage(message));
    }

    Optional<Duration> getFlagDurationIfExists(String timeArg) {
        if (timeArg.length() < 2) {
            return Optional.empty();
        }

        String time = timeArg.substring(0, timeArg.length() - 1);
        String letter = timeArg.substring(timeArg.length() - 1);
        String[] validTimes = new String[]{"d", "h", "m"};
        if (!time.matches("[1-9][0-9]{0,8}") || !Arrays.asList(validTimes).contains(letter)) {
            return Optional.empty();
        }

        int number = Integer.parseInt(time);
        return convertTimeToExpireTime(number, letter);
    }

    public Optional<Duration> convertTimeToExpireTime(int number, String dhm) {
        switch (dhm.toLowerCase()) {
            case "d" -> {
                return Optional.of(Duration.ofDays(number));
            }
            case "h" -> {
                return Optional.of(Duration.ofHours(number));
            }
            case "m" -> {
                return Optional.of(Duration.ofMinutes(number));
            }
            default -> {
                return Optional.empty();
            }
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<String> getTabComplete(CommandSender commandSender, String[] args) {
        ArrayList<String> res = new ArrayList<>();
        if (args.length == 2 && commandSender.hasPermission(getPermission() + ".variable-time"))
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
