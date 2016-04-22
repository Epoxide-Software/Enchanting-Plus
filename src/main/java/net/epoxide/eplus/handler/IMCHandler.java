package net.epoxide.eplus.handler;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

public final class IMCHandler {
    
    /**
     * Processes all incoming IMC messages for the mod. Please read our project README for more
     * information on what IMC messages are accepted.
     * 
     * @param message: A message that has been sent to the Enchanting Plus mod.
     */
    public static void handleMessage (IMCMessage message) {
        
        if (message.key.equalsIgnoreCase("blacklistItems")) {
            
            if (message.isStringMessage())
                ContentHandler.blacklistItem(Item.REGISTRY.getObject(new ResourceLocation(message.getStringValue())));
                
            if (message.isItemStackMessage())
                ContentHandler.blacklistItem(message.getItemStackValue().getItem());
            ;
            
            if (message.isNBTMessage()) {
                
                final NBTTagList list = message.getNBTValue().getTagList("blacklistItems", 8);
                
                for (int count = 0; count < list.tagCount(); count++)
                    ContentHandler.blacklistItem(Item.REGISTRY.getObject(new ResourceLocation(list.getStringTagAt(count))));
            }
        }
        
        else if (message.key.equalsIgnoreCase("blacklistEnchantments")) {
            
            if (message.isStringMessage())
                ContentHandler.blacklistEnchantment(Enchantment.REGISTRY.getObject(new ResourceLocation(message.getStringValue())));
                
            if (message.isNBTMessage()) {
                
                final NBTTagList list = message.getNBTValue().getTagList("blacklistEnchantments", 8);
                
                for (int count = 0; count < list.tagCount(); count++)
                    ContentHandler.blacklistEnchantment(Enchantment.REGISTRY.getObject(new ResourceLocation(list.getStringTagAt(count))));
            }
        }
    }
}