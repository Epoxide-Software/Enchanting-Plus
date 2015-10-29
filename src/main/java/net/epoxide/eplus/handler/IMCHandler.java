package net.epoxide.eplus.handler;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

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
                    for (Enchantment enchant : ItemStackUtils.getEnchantmentsFromStack(stack, (stack.getItem() instanceof ItemEnchantedBook)))
                        ContentHandler.blacklistEnchantment(enchant, sender);
            }
            
            if (message.isNBTMessage()) {
                
                NBTTagList list = message.getNBTValue().getTagList("blacklistEnchantments", 8);
                
                for (int count = 0; count < list.tagCount(); count++) {
                    
                    String id = list.getStringTagAt(count);
                    
                    if (StringUtils.isNumeric(id))
                        ContentHandler.blacklistEnchantment(Integer.parseInt(id), sender);
                }
            }
        }
    }
}