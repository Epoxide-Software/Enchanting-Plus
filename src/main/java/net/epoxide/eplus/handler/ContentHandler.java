package net.epoxide.eplus.handler;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.epoxide.eplus.block.BlockEnchantTable;
import net.epoxide.eplus.tileentity.TileEntityEnchantingTable;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContentHandler {
    
    /**
     * A blacklist containing all of the numeric IDs of Enchantments that have been
     * blacklisted.
     */
    private static List<Integer> blacklistEnchantments = new ArrayList<Integer>();
    
    /**
     * A blacklist containing all of the string IDs of Items that have been blacklisted.
     */
    private static List<String> blacklistItems = new ArrayList<String>();
    
    public static Block eplusTable;
    
    public static Item tableUpgrade;
    public static Item scroll;
    
    /**
     * Initializes all of the blocks for the Enchanting Plus mod. Used to handle Block
     * construction and registry.
     */
    public static void initBlocks () {
        
        eplusTable = new BlockEnchantTable();
        GameRegistry.registerBlock(eplusTable, "advancedEnchantmentTable");
        GameRegistry.registerTileEntity(TileEntityEnchantingTable.class, "advancedEnchantmentTable");
    }
    
    /**
     * Initializes all of the items for the Enchanting Plus mod. Used to handle Item
     * construction and registry.
     */
    public static void initItems () {
    
    }
    
    /**
     * Adds an Enchantment to the blacklist. Enchantments that are on this list can not be
     * applied by the advanced enchantment table.
     * 
     * @param enchant: The Enchantment to add to the blacklist.
     * @param blacklister: The name of the person, mod, group or entity that is attempting to
     *            add this entry to the blacklist.
     */
    public static void blacklistEnchantment (Enchantment enchant, String blacklister) {
        
        blacklistEnchantment(enchant.effectId, blacklister);
    }
    
    /**
     * Adds an Enchantment to the blacklist. Enchantments that are on this list can not be
     * applied by the advanced enchantment table.
     * 
     * @param enchantID: The Enchantment ID to add to the blacklist.
     * @param blacklister: The name of the person, mod, group or entity that is attempting to
     *            add this entry to the blacklist.
     */
    public static void blacklistEnchantment (int enchantID, String blacklister) {
        
        if (!blacklistEnchantments.contains(enchantID))
            blacklistEnchantments.add(enchantID);
    }
    
    /**
     * Adds an Item to the blacklist. Items that are on this list can not be enchanted using
     * the advanced enchanting table.
     * 
     * @param stack: The ItemStack to add to the blacklist. Does not support meta or NBT!
     * @param blacklister: The name of the person, mod, group or entity that is attempting to
     *            add this entry to the blacklist.
     */
    public static void addItemToBlacklist (ItemStack stack, String blacklister) {
        
        if (ItemStackUtils.isValidStack(stack))
            addItemToBlacklist(stack.getItem(), blacklister);
    }
    
    /**
     * Adds an Item to the blacklist. Items that are on this list can not be enchanted using
     * the advanced enchantment table.
     * 
     * @param item: The Item to add to the blacklist.
     * @param blacklister: The name of the person, mod, group or entity that is attempting to
     *            add this entry to the blacklist.
     */
    public static void addItemToBlacklist (Item item, String blacklister) {
        
        addItemToBlacklist(GameData.getItemRegistry().getNameForObject(item), blacklister);
    }
    
    /**
     * Adds an Item to the blacklist, directly from it's item ID. Items on this list can not be
     * enchanted using the advanced enchantment table.
     * 
     * @param name: The name of the item to blacklist. Should be equal to the item ID.
     * @param blacklister: The name of the person, mod, group or entity that is attempting to
     *            add this entry to the blacklist.
     */
    public static void addItemToBlacklist (String name, String blacklister) {
        
        if (!blacklistItems.contains(name)) {
            
            blacklistItems.add(name);
            
            // TODO Blacklist message
        }
    }
    
    /**
     * Checks if an Enchantment is on the blacklist.
     * 
     * @param enchant: The enchantment to check for.
     * @return boolean: True if the blacklist contains the ID of the passed enchantment.
     */
    public static boolean isBlacklisted (Enchantment enchant, String blacklister) {
        
        return blacklistEnchantments.contains(enchant.effectId);
    }
    
    /**
     * Checks if an ItemStack is on the blacklist.
     * 
     * @param stack: The ItemStack to check for.
     * @return boolean: True if the Item contained in the passed stack is blacklisted, or if
     *         the passed stack is invalid.
     */
    public static boolean isBlacklisted (ItemStack stack) {
        
        return !ItemStackUtils.isValidStack(stack) || isBlacklisted(stack.getItem());
    }
    
    /**
     * Checks if an Item is on the blacklist.
     * 
     * @param item: The Item to check for.
     * @return boolean: True if the name of the passed Item is in the blacklist.
     */
    public static boolean isBlacklisted (Item item) {
        
        return blacklistItems.contains(GameData.getItemRegistry().getNameForObject(item));
    }
}