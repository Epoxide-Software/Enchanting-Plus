package net.epoxide.eplus.handler;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;

import net.darkhax.bookshelf.lib.util.EnchantmentUtils;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;

public class IMCHandler {
    
    /**
     * Processes all incoming IMC messages for the mod. Please read our project README for more
     * information on what IMC messages are accepted.
     * 
     * @param message: A message that has been sent to the Enchanting Plus mod.
     */
    public static void handleMessage (IMCMessage message) {
        
        String sender = message.getSender();
        
        if (message.key.equalsIgnoreCase("blacklistItems")) {
            
            if (message.isStringMessage())
                ContentHandler.addItemToBlacklist(message.getStringValue(), sender);
                
            if (message.isItemStackMessage())
                ContentHandler.addItemToBlacklist(message.getItemStackValue(), sender);
                
            if (message.isNBTMessage()) {
                
                NBTTagList list = message.getNBTValue().getTagList("blacklistItems", 8);
                
                for (int count = 0; count < list.tagCount(); count++)
                    ContentHandler.addItemToBlacklist(list.getStringTagAt(count), sender);
            }
        }
        
        else if (message.key.equalsIgnoreCase("blacklistEnchantments")) {
            
            if (message.isStringMessage() && StringUtils.isNumeric(message.getStringValue()))
                ContentHandler.blacklistEnchantment(Integer.parseInt(message.getStringValue()), sender);
                
            if (message.isItemStackMessage()) {
                
                ItemStack stack = message.getItemStackValue();
                
                if (ItemStackUtils.isValidStack(stack))
                    for (Enchantment enchant : EnchantmentUtils.getEnchantmentsFromStack(stack, (stack.getItem() instanceof ItemEnchantedBook)))
                        ContentHandler.blacklistEnchantment(enchant, sender);
            }
            
            if (message.isNBTMessage()) {
                
                for (int id : message.getNBTValue().getIntArray("blacklistedEnchantments"))
                    ContentHandler.blacklistEnchantment(id, sender);
            }
        }
        
        else if (message.key.equalsIgnoreCase("setEnchantmentColor")) {
            
            if (message.isStringMessage())
                setEnchantmentColorFromString(message.getStringValue());
                
            if (message.isNBTMessage()) {
                
                NBTTagList list = message.getNBTValue().getTagList("setEnchantmentColor", 8);
                
                for (int count = 0; count < list.tagCount(); count++)
                    setEnchantmentColorFromString(list.getStringTagAt(count));
            }
        }
    }
    
    /**
     * Sets an enchantment color using a string message. The message is split into several
     * parts, using : to seperate the name and the color. For the string to be valid, the
     * message must only have one : and the text after the : must be an integer.
     * 
     * @param message: The string to use for adding the color.
     */
    private static void setEnchantmentColorFromString (String message) {
        
        if (message.contains(":")) {
            
            String[] components = message.split(":");
            
            if (components.length == 2 && StringUtils.isNumeric(components[1]))
                ContentHandler.setEnchantmentColor(components[0], Integer.parseInt(components[1]));
        }
    }
}