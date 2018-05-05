package net.darkhax.eplus.handler;

import java.util.ArrayList;

import net.darkhax.bookshelf.util.NBTUtils;
import net.darkhax.bookshelf.util.RegistryUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

public final class IMCHandler {

    /**
     * Processes all incoming IMC messages for the mod. Please read our project README for more
     * information on what IMC messages are accepted.
     *
     * @param message A message that has been sent to the Enchanting Plus mod.
     */
    public static void handleMessage (IMCMessage message) {

        if (message.key.equalsIgnoreCase("blacklistItems")) {

//            if (message.isStringMessage()) {
//                ContentHandler.blacklistItem(Item.REGISTRY.getObject(new ResourceLocation(message.getStringValue())));
//            }
//
//            if (message.isItemStackMessage()) {
//                ContentHandler.blacklistItem(message.getItemStackValue().getItem());
//            }
//
//            if (message.isNBTMessage()) {
//
//                final NBTTagList list = message.getNBTValue().getTagList("blacklistItems", NBT.TAG_STRING);
//
//                for (int count = 0; count < list.tagCount(); count++) {
//                    ContentHandler.blacklistItem(Item.REGISTRY.getObject(new ResourceLocation(list.getStringTagAt(count))));
//                }
//            }
        }

        else if (message.key.equalsIgnoreCase("blacklistEnchantments")) {

            if (message.isStringMessage()) {
                
                blacklistEnchantmentString(message.getStringValue(), message.getSender());
            }

            if (message.isNBTMessage()) {

                for (String id : NBTUtils.readCollection(new ArrayList<String>(), message.getNBTValue().getTagList("blacklistEnchantments", NBT.TAG_STRING), entry -> entry)) {
                    
                    blacklistEnchantmentString(message.getStringValue(), message.getSender());
                }
            }
        }
    }
    
    private static void blacklistEnchantmentString(String enchantmentid, String sender) {
        
        ContentHandler.blacklist(RegistryUtils.getEnchantment(enchantmentid), sender);
    }
}