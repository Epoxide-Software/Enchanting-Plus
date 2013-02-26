package eplus.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraftforge.common.Property;

import java.lang.reflect.Array;
import java.util.List;

/**
 * User: Stengel
 * Date: 2/23/13
 * Time: 12:34 PM
 */
public class eplusCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "eplus";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender var1, String[] args) {
        if (args.length > 0) {
            String commandName = args[0];
            System.arraycopy(args, 1, args, 0, args.length - 1);

            if (commandName.equalsIgnoreCase("texture")) {
                processTexture(var1, args);
            } else if (commandName.equalsIgnoreCase("changelog")){
                processChangelog(var1, args);
            } else {
                throw new WrongUsageException("eplus [ texture | changelog ]", new Object[]{0});
            }
        } else {
            throw new WrongUsageException("eplus [ texture | changelog ]", new Object[]{0});
        }
    }

    private void processChangelog(ICommandSender var1, String[] args) {
        var1.sendChatToPlayer("[EPLUS] Changelog:");
        for(String line : Version.grabChangelog()){
            var1.sendChatToPlayer(line);
        }
    }

    private void processTexture(ICommandSender var1, String[] args) {
        if (args.length > 0) {
            int value = Integer.valueOf(args[0]);

            if (value < 0) {
                throw new WrongUsageException("eplus texture [ 0 | 1 | 2 ]", new Object[]{0});
            } else if (value > 2) {
                throw new WrongUsageException("eplus texture [ 0 | 1 | 2 ]", new Object[]{0});
            }

            Property property = EnchantingPlus.config.get("general", "TextureIndex", 2);
            property.value = String.valueOf(value);
            EnchantingPlus.config.save();
            EnchantingPlus.textureIndex = value;

            var1.sendChatToPlayer("[EPLUS] changed texture to " + value);

        } else {
            throw new WrongUsageException("eplus texture [ 0 | 1 | 2 ]", new Object[]{0});
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args) {

        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, new String[]{"texture", "changelog"});
            case 2:
                if (args[0].equalsIgnoreCase("texture")) {
                    return getListOfStringsMatchingLastWord(args, new String[]{"0", "1", "2"});
                }
            default:
                return null;
        }
    }
}
