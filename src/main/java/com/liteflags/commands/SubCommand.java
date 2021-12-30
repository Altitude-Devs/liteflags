package com.liteflags.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {

    public abstract boolean onCommand(CommandSender commandSender, String[] args);

    public abstract String getName();

    public String getPermission() {
        return "liteflags." + getName();
    }

    public abstract List<String> getTabComplete(CommandSender commandSender, String[] args);

    public abstract String getHelpMessage();
}
