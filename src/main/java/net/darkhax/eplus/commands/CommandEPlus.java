package net.darkhax.eplus.commands;

import net.darkhax.bookshelf.command.CommandTree;
import net.minecraft.command.ICommandSender;

public class CommandEPlus extends CommandTree {

    public CommandEPlus () {

    }

    @Override
    public int getRequiredPermissionLevel () {

        return 0;
    }

    @Override
    public String getName () {

        return "eplus";
    }

    @Override
    public String getUsage (ICommandSender sender) {

        return "commands.eplus.usage";
    }
}