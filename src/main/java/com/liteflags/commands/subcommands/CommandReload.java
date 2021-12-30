package com.liteflags.commands.subcommands;

import com.liteflags.commands.SubCommand;
import com.liteflags.config.Config;
import com.liteflags.util.Utilities;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandReload extends SubCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        Config.reload();
        commandSender.sendMiniMessage(Config.CONFIG_RELOADED, null);
        return false;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public List<String> getTabComplete(CommandSender commandSender, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public String getHelpMessage() {
        return Config.HELP_RELOAD;
    }
}
