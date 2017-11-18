package net.darkhax.eplus.commands;

import net.darkhax.bookshelf.command.Command;
import net.darkhax.eplus.handler.PlayerHandler;
import net.darkhax.eplus.handler.PlayerHandler.ICustomData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandClear extends Command {

    @Override
    public String getName () {

        return "clear";
    }

    @Override
    public int getRequiredPermissionLevel () {

        return 2;
    }

    @Override
    public String getUsage (ICommandSender sender) {

        return "commands.eplus.clear.usage";
    }

    @Override
    public void execute (MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        
        final EntityPlayer player = (args.length == 1) ? getPlayer(server, sender, args[0]) : (sender instanceof EntityPlayer) ? (EntityPlayer) sender : null;
        
        if (player != null) {

            final ICustomData data = PlayerHandler.getPlayerData(player);

            if (data != null) {

                data.getUnlockedEnchantments().clear();
                data.syncData();
            }
        }
    }
}