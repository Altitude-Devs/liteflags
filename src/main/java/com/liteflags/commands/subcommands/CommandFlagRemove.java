package com.liteflags.commands.subcommands;

import com.liteflags.commands.SubCommand;
import com.liteflags.config.Config;
import com.liteflags.data.database.Database;
import com.liteflags.util.Logger;
import com.liteflags.util.Utilities;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandFlagRemove extends SubCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        int id = Integer.parseInt(args[1]);
        OfflinePlayer target = Bukkit.getServer().getOfflinePlayerIfCached(args[2]);

        if (target == null) {
            commandSender.sendMiniMessage(Config.UNKNOWN_PLAYER, List.of(Template.template("player", args[2])));
            return true;
        }
        if (args.length == 4) {
            if (args[3].isEmpty() || !args[3].equalsIgnoreCase("-c")) {
                commandSender.sendMiniMessage(getHelpMessage(), null);
                return true;
            }
            String flagReason = Database.getFlagReason(target.getUniqueId(), id);
            flagReason = flagReason == null ? "Flag not found" : flagReason;
            if (Database.removeFlag(target.getUniqueId(), id)) {
                commandSender.sendMiniMessage(Config.FLAG_REMOVED, List.of(
                        Template.template("flag_reason", flagReason),
                        Template.template("target", target.getName() == null ? target.getUniqueId().toString() : target.getName())
                ));
                Logger.info("% has removed the flag '%' from %'s flag history.", commandSender.getName(), flagReason, target.getName());
            } else {
                commandSender.sendMiniMessage(Config.NO_FLAG_FOUND, List.of(
                        Template.template("target", target.getName() == null ? target.getUniqueId().toString() : target.getName()),
                        Template.template("id", String.valueOf(id))
                ));
            }
        } else if (args.length == 3) {
            String flagReason = Database.getFlagReason(target.getUniqueId(), id);
            String name = target.getName();
//            commandSender.sendMiniMessage("<white>Are you sure you want to remove the flag <flag_reason> from " +
//                    "<yellow><target_name></yellow>'s flag history? " +
//                    "<hover:Click to confirm><click:run_command:/flag remove " + id + " " + target.getName() + " -c>[<green>Confirm</green>]</click></hover></white>", List.of(
//                    Template.template("flag_reason", flagReason == null ? "Unknown" : flagReason),
//                    Template.template("target_name", name == null ? target.getUniqueId().toString() : name),
//                    Template.template("id", String.valueOf(id))
//            ));
            commandSender.sendMiniMessage(Config.FLAG_CONFIRM, List.of(
                    Template.template("flag_reason", flagReason == null ? "Unknown" : flagReason),
                    Template.template("target_name", name == null ? target.getUniqueId().toString() : name),
                    Template.template("id", String.valueOf(id))
            ));
        }
        return true;
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public List<String> getTabComplete(CommandSender commandSender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String getHelpMessage() {
        return Config.HELP_FLAG_REMOVE;
    }
}
