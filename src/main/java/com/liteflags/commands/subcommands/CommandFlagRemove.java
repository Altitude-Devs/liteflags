package com.liteflags.commands.subcommands;

import com.liteflags.commands.SubCommand;
import com.liteflags.config.Config;
import com.liteflags.data.database.Database;
import com.liteflags.util.Logger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
            commandSender.sendMiniMessage(Config.UNKNOWN_PLAYER, TagResolver.resolver(Placeholder.unparsed("player", args[2])));
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
                commandSender.sendMiniMessage(Config.FLAG_REMOVED, TagResolver.resolver(
                        Placeholder.unparsed("flag_reason", flagReason),
                        Placeholder.unparsed("target", target.getName() == null ? target.getUniqueId().toString() : target.getName())
                ));
                Logger.info("% has removed the flag '%' from %'s flag history.", commandSender.getName(), flagReason, target.getName());
            } else {
                commandSender.sendMiniMessage(Config.NO_FLAG_FOUND, TagResolver.resolver(
                        Placeholder.unparsed("target", target.getName() == null ? target.getUniqueId().toString() : target.getName()),
                        Placeholder.unparsed("id", String.valueOf(id))
                ));
            }
        } else if (args.length == 3) {
            String flagReason = Database.getFlagReason(target.getUniqueId(), id);
            String name = target.getName();
//            commandSender.sendMiniMessage("<white>Are you sure you want to remove the flag <flag_reason> from " +
//                    "<yellow><target_name></yellow>'s flag history? " +
//                    "<hover:Click to confirm><click:run_command:/flag remove " + id + " " + target.getName() + " -c>[<green>Confirm</green>]</click></hover></white>", List.of(
//                    Placeholder.unparsed("flag_reason", flagReason == null ? "Unknown" : flagReason),
//                    Placeholder.unparsed("target_name", name == null ? target.getUniqueId().toString() : name),
//                    Placeholder.unparsed("id", String.valueOf(id))
//            ));
            commandSender.sendMiniMessage(Config.FLAG_CONFIRM, TagResolver.resolver(
                    Placeholder.parsed("flag_reason", flagReason == null ? "Unknown" : flagReason),
                    Placeholder.parsed("target_name", name == null ? target.getUniqueId().toString() : name),
                    Placeholder.parsed("id", String.valueOf(id))
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
