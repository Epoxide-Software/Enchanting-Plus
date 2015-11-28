package net.epoxide.eplus.command;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import net.darkhax.bookshelf.lib.util.Utilities;

import net.epoxide.eplus.common.PlayerProperties;
import net.epoxide.eplus.handler.ContentHandler;

public class CommandUnlockEnchantments extends CommandBase {
    
    @Override
    public String getCommandName () {
        
        return "learnEnchant";
    }
    
    @Override
    public String getCommandUsage (ICommandSender sender) {
        
        return "command.eplus.learnenchant.usage";
    }
    
    @Override
    public void processCommand (ICommandSender sender, String[] params) {
        
        if (params.length != 2)
            throw new WrongUsageException("command.eplus.learnenchant.usage", new Object[0]);
            
        EntityPlayerMP player = getPlayer(sender, params[0]);
        
        unlockEnchantsForPlayer(sender, player, StringUtils.isNumeric(params[1]) ? Integer.parseInt(params[1]) : (params[1].equalsIgnoreCase("a") || params[1].equalsIgnoreCase("all")) ? -13 : 0);
    }
    
    @Override
    public int getRequiredPermissionLevel () {
        
        return 2;
    }
    
    public static void unlockEnchantsForPlayer (ICommandSender sender, EntityPlayer player, int id) {
        
        PlayerProperties props = PlayerProperties.getProperties(player);
        
        if (props != null && id != -1) {
            
            if (id == -13) {
                
                for (Enchantment ench : Utilities.getAvailableEnchantments())
                    if (!ContentHandler.isBlacklisted(ench) && !props.unlockedEnchantments.contains(ench.effectId))
                        props.unlockedEnchantments.add(ench.effectId);
                        
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + player.getDisplayName() + EnumChatFormatting.GREEN + " " + StatCollector.translateToLocal("command.eplus.learnenchant.all.success")));
            }
            
            else {
                
                Enchantment ench = Utilities.getEnchantment(id);
                
                if (ench != null && !ContentHandler.isBlacklisted(ench) && !props.unlockedEnchantments.contains(ench.effectId)) {
                    
                    props.unlockedEnchantments.add(ench.effectId);
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + player.getDisplayName() + EnumChatFormatting.GREEN + " " + StatCollector.translateToLocal("command.eplus.learnenchant.success") + " " + StatCollector.translateToLocal(ench.getName())));
                }
            }
            
            if (!player.worldObj.isRemote)
                props.sync();
        }
    }
}