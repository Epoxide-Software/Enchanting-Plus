package eplus.commands;

import cpw.mods.fml.common.network.PacketDispatcher;
import eplus.EnchantingPlus;
import eplus.handlers.ConfigurationHandler;
import eplus.helper.StringHelper;
import eplus.lib.References;
import eplus.network.packets.ConfigPacket;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.HashMap;
import java.util.List;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class EplusCommands extends CommandBase
{


    @Override
    public String getCommandName()
    {
        return "eplus";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
    {
        switch (args.length)
        {
            case 1:
            {
                return getListOfStringsFromIterableMatchingLastWord(args, CommandRegister.commands.keySet());
            }
            case 2:
            {
                for (String command : CommandRegister.commands.keySet())
                {
                    if (args[0].equalsIgnoreCase(command))
                    {
                        return getListOfStringsFromIterableMatchingLastWord(args, CommandRegister.commands.get(command));
                    }
                }
            }
            default:
            {
                return null;
            }
        }
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] args)
    {
        if (args.length > 0)
        {
            String commandName = args[0];
            System.arraycopy(args, 1, args, 0, args.length - 1);

            for (String command : CommandRegister.commands.keySet())
            {
                if (commandName.equalsIgnoreCase(command))
                {
                    processConfigCommand(icommandsender, commandName, args);
                    return;
                }
            }
            throw new WrongUsageException("eplus " + StringHelper.keySetToString(CommandRegister.commands.keySet()));
        } else
        {
            throw new WrongUsageException("eplus " + StringHelper.keySetToString(CommandRegister.commands.keySet()));
        }
    }

    private void processConfigCommand(ICommandSender icommandsender, String commandName, String[] args)
    {
        for (String arg : args)
        {
            if (CommandRegister.commands.get(commandName).contains(arg))
            {
                EnchantingPlus.log.info(commandName + ":" + args[0]);
                ConfigurationHandler.set(commandName, args[0]);

                HashMap<String, String> config = new HashMap<String, String>();

                config.put(commandName, args[0]);

                PacketDispatcher.sendPacketToServer(new ConfigPacket(config).makePacket());
                icommandsender.sendChatToPlayer(String.format("%s: Config '%s' changed to %s.", References.MODID.toUpperCase(), commandName, args[0]));

                return;
            }
        }
        throw new WrongUsageException("eplus " + commandName + " " + StringHelper.listToString(CommandRegister.commands.get(commandName)));
    }
}

