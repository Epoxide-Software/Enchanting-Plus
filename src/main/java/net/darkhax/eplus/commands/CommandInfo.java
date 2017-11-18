package net.darkhax.eplus.commands;

import java.util.stream.Collectors;

import net.darkhax.bookshelf.command.Command;
import net.darkhax.eplus.handler.PlayerHandler;
import net.darkhax.eplus.handler.PlayerHandler.ICustomData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandInfo extends Command {

    @Override
    public String getName () {

        return "info";
    }

    @Override
    public int getRequiredPermissionLevel () {

        return 0;
    }

    @Override
    public String getUsage (ICommandSender sender) {

        return "commands.eplus.info.usage";
    }

    @Override
    public void execute (MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (sender instanceof EntityPlayer) {

            final ICustomData data = PlayerHandler.getPlayerData((EntityPlayer) sender);

            if (data != null) {

                if (data.getUnlockedEnchantments().isEmpty()) {
                    
                    sender.sendMessage(new TextComponentTranslation("chat.eplus.nounlocked"));
                    return;
                }
                
                final String enchants = data.getUnlockedEnchantments().stream().map(Enchantment::getRegistryName).map(ResourceLocation::toString).collect(Collectors.joining(", "));
                sender.sendMessage(new TextComponentString(enchants));
            }
        }
    }
}