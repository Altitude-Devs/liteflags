package com.liteflags.commands;

import com.liteflags.LiteFlags;
import com.liteflags.commands.subcommands.*;
import com.liteflags.config.Config;
import com.liteflags.util.Logger;
import com.liteflags.util.Utilities;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, TabExecutor {
    private final List<SubCommand> subCommands;
    private SubCommand flagPlayer = new CommandFlagPlayer();

    public CommandManager() {
        LiteFlags liteFlags = LiteFlags.getInstance();

        PluginCommand command = liteFlags.getCommand("flag");
        if (command == null) {
            subCommands = null;
            Logger.severe("Unable to find LiteFLags command.");
            return;
        }
        command.setExecutor(this);
        command.setTabCompleter(this);

        subCommands = Arrays.asList(//TODO add the flag player command separately
                new CommandHelp(this),
                new CommandFlagList(),
                new CommandFlagPlayer(),
                new CommandFlagRemove(),
                new CommandReload()
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String cmd, @NotNull String[] args) {
        if (!commandSender.hasPermission("liteflags.use")) {
            commandSender.sendMiniMessage(Config.NO_PERMISSION, null);
            return true;
        }
        if (args.length == 0) {
            commandSender.sendMiniMessage(Config.HELP_MESSAGE_WRAPPER.replaceAll("<commands>", subCommands.stream()
                    .filter(subCommand -> commandSender.hasPermission(subCommand.getPermission()))
                    .map(SubCommand::getHelpMessage)
                    .collect(Collectors.joining("\n"))), null);
            return true;
        }

        SubCommand subCommand = getSubCommand(args[0]);

        if (!commandSender.hasPermission(subCommand.getPermission())) {
            commandSender.sendMiniMessage(Config.NO_PERMISSION, null);
            return true;
        }

        return subCommand.onCommand(commandSender, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String cmd, @NotNull String[] args) {
        List<String> res = new ArrayList<>();

        if (args.length <= 1) {
            res.addAll(subCommands.stream()
                    .filter(subCommand -> commandSender.hasPermission(subCommand.getPermission()))
                    .map(SubCommand::getName)
                    .filter(Objects::nonNull)
                    .filter(name -> args.length == 0 || name.startsWith(args[0]))
                    .collect(Collectors.toList()));
            res.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> args.length == 0 || name.startsWith(args[0]))
                    .collect(Collectors.toList()));
        } else {
            SubCommand subCommand = getSubCommand(args[0]);
            if (subCommand != null && commandSender.hasPermission(subCommand.getPermission()))
                res.addAll(subCommand.getTabComplete(commandSender, args));
        }
        return res;
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    private SubCommand getSubCommand(String cmdName) {
        return subCommands.stream()
                .filter(Objects::nonNull)
                .filter(subCommand -> subCommand.getName() != null && subCommand.getName().equals(cmdName))
                .findFirst()
                .orElse(flagPlayer);
    }
}