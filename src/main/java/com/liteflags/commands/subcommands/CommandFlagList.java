package com.liteflags.commands.subcommands;

import com.liteflags.LiteFlags;
import com.liteflags.commands.SubCommand;
import com.liteflags.config.Config;
import com.liteflags.data.database.Database;
import com.liteflags.data.database.Methods;
import com.liteflags.data.maps.MapCache;
import com.liteflags.util.Logger;
import com.liteflags.util.Utilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CommandFlagList extends SubCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        if (args.length == 1) {
            if (MapCache.activeFlags.size() > 0) {
                MapCache.activeFlags.forEach(playerName -> {
                    OfflinePlayer player = Bukkit.getServer().getOfflinePlayerIfCached(playerName);
                    if (player == null) {
                        Logger.warning("% is not a flagged player", playerName);
                        return;
                    }
                    commandSender.sendMiniMessage(Config.ALERT_ACTIVE_FLAGS, List.of(
                            Template.template("player", player.getName() == null ? playerName : player.getName()),
                            Template.template("total_act_flags", String.valueOf( Methods.getTotalActiveFlags(player))),
                            Template.template("console_flags", String.valueOf(Methods.consoleFlags)),
                            Template.template("staff_flags", String.valueOf(Methods.staffFlags))
                    ));
                });
            } else
                commandSender.sendMiniMessage(Config.NO_ACTIVE_FLAGS, null);
        } else if (args.length == 2 || args.length == 3) {
            List<String> body;
            if (args.length == 3) {
                if (!args[2].equals("full")) {
                    commandSender.sendMiniMessage(getHelpMessage(), null);
                    return true;
                }
                body = Config.PLAYER_FLAG_LIST_LONG;
            } else
                body = Config.PLAYER_FLAG_LIST_SHORT;
            OfflinePlayer player = Bukkit.getServer().getOfflinePlayerIfCached(args[1]);
            if (player == null) {
                commandSender.sendMiniMessage(Config.UNKNOWN_PLAYER, List.of(Template.template("player", args[1])));
                return true;
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendFlagMessage(commandSender, player, body);
                }
            }.runTask(LiteFlags.getInstance());
        } else {
            //TODO return error
        }
        return true;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public List<String> getTabComplete(CommandSender commandSender, String[] args) {
        ArrayList<String> res = new ArrayList<>();
        if (args.length == 2)
            res.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> args[1].isEmpty() || name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList()));
        return res;
    }

    @Override
    public String getHelpMessage() {
        return Config.HELP_FLAG_LIST;
    }

    public void sendFlagMessage(CommandSender commandSender, OfflinePlayer targetPlayer, List<String> body) {
        if (targetPlayer == null) {
            Logger.warning("Player that can't be null is null");
            return;
        }
        MiniMessage miniMessage = MiniMessage.miniMessage();
        String targetName = targetPlayer.getName();

        targetName = targetName == null ? targetPlayer.getUniqueId().toString() : targetName;
        try {
            ResultSet resultSet = Database.getPlayerFlags(targetPlayer.getUniqueId());
            Component message = null;
            while (resultSet.next()) {
                if (message == null) {
                    message = miniMessage.deserialize(Config.PLAYER_FLAGS_HEADER, TemplateResolver.templates(List.of(
                            Template.template("player", targetName),
                            Template.template("flag_amount", String.valueOf(resultSet.getString("total_flags")))
                    )));
                }
                long expireTime = TimeUnit.SECONDS.toMinutes(resultSet.getInt("expire_time"));
                long timeFlagged = TimeUnit.SECONDS.toMinutes(resultSet.getInt("time_flagged"));
                long currentTime = TimeUnit.SECONDS.toMinutes(System.currentTimeMillis() / 1000L);
                int convertedExpireTime = (int) expireTime - (int) currentTime;
                int convertedFlaggedTime = (int) currentTime - (int) timeFlagged;
                String id = String.valueOf(resultSet.getInt("id"));
                List<Template> templates = List.of(Template.template("player", targetPlayer.getName()),
                        Template.template("flag", resultSet.getString("reason")),
                        Template.template("staff", resultSet.getString("flagged_by")),
                        Template.template("flag_length", resultSet.getString("flag_length")),
                        Template.template("reason", resultSet.getString("reason")),
                        Template.template("flag_time", Utilities.convertTime(convertedFlaggedTime)),
                        Template.template("expire_time", Utilities.convertTime(convertedExpireTime)),
                        Template.template("nl", "\n"),
                        Template.template("id", id)
                );
                String str = String.join("\n", body)
                        .replaceAll("<remove_button>", "<white>" +
                                "<hover:show_text:\"Click to remove this flag\">" +
                                "<click:run_command:/flag remove " + id + " " + targetPlayer.getName() + ">" +
                                "[<red>✖</red>]</click></hover></white>");
                if (convertedExpireTime < 0 && expireTime != 0L) //Not active
                    str = str.replaceAll("<active>", Config.EXPIRED_FLAGS);
                else //Active
                    str = str.replaceAll("<active>", Config.ACTIVE_FLAGS);
                str = "\n" + str;
                Logger.info(str);
                message = message.append(miniMessage.deserialize(str, TemplateResolver.templates(templates)));
            }
            if (message == null) {
                commandSender.sendMiniMessage(Config.NO_FLAG_FOUND, null);
            } else
                commandSender.sendMessage(message);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
