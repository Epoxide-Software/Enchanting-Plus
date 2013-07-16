package eplus.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import cpw.mods.fml.common.network.PacketDispatcher;
import eplus.EnchantingPlus;
import eplus.handlers.ConfigurationHandler;
import eplus.handlers.Version;
import eplus.helper.StringHelper;
import eplus.lib.References;
import eplus.network.packets.ConfigPacket;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class EplusCommands extends CommandBase
{
    private static List<String> commands = new ArrayList<String>();

    static
    {
        commands.addAll(CommandRegister.commands.keySet());
        commands.add("changelog");
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
    {
        switch (args.length)
        {
        case 1:
        {
            return getListOfStringsFromIterableMatchingLastWord(args, commands);
        }
        case 2:
        {
            for (final String command : CommandRegister.commands.keySet())
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
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return true;
    }

    @Override
    public String getCommandName()
    {
        return "eplus";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender)
    {
        // TODO Auto-generated method stub
        return null;
    }

    private void processChangelog(ICommandSender icommandsender, String commandName, String[] args)
    {
        icommandsender.sendChatToPlayer(String.format("\u00A7e[%s] Changelog for %s", References.MODID, Version.getRecommendedVersion()));
        for (final String line : Version.getChangelog())
        {
            icommandsender.sendChatToPlayer(line);
        }

    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] args)
    {
        if (args.length > 0)
        {
            final String commandName = args[0];
            System.arraycopy(args, 1, args, 0, args.length - 1);

            for (final String command : CommandRegister.commands.keySet())
            {
                if (commandName.equalsIgnoreCase(command))
                {
                    processConfigCommand(icommandsender, commandName, args);
                    return;
                }
            }
            if (commandName.equalsIgnoreCase("changelog"))
            {
                processChangelog(icommandsender, commandName, args);
                return;
            }

            throw new WrongUsageException("eplus " + StringHelper.keySetToString(CommandRegister.commands.keySet()));
        }
        else
        {
            throw new WrongUsageException("eplus " + StringHelper.keySetToString(CommandRegister.commands.keySet()));
        }
    }

    private void processConfigCommand(ICommandSender icommandsender, String commandName, String[] args)
    {
        for (final String arg : args)
        {
            if (CommandRegister.commands.get(commandName).contains(arg))
            {
                EnchantingPlus.log.info(commandName + ":" + args[0]);
                ConfigurationHandler.set(commandName, args[0]);

                final HashMap<String, String> config = new HashMap<String, String>();

                config.put(commandName, args[0]);

                PacketDispatcher.sendPacketToServer(new ConfigPacket(config).makePacket());
                icommandsender.sendChatToPlayer(String.format("%s: Config '%s' changed to %s.", References.MODID.toUpperCase(), commandName, args[0]));
                return;
            }
        }
        throw new WrongUsageException("eplus " + commandName + " " + StringHelper.listToString(CommandRegister.commands.get(commandName)));
    }
}
