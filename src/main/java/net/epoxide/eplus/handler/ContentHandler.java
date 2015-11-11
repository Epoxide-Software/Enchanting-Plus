package net.epoxide.eplus.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.darkhax.bookshelf.potion.Buff;
import net.darkhax.bookshelf.potion.BuffHelper;
import net.epoxide.eplus.EnchantingPlus;
import net.epoxide.eplus.block.BlockArcaneInscriber;
import net.epoxide.eplus.block.BlockEnchantTable;
import net.epoxide.eplus.block.BlockEnchantmentBook;
import net.epoxide.eplus.buff.BuffBookFall;
import net.epoxide.eplus.item.ItemBlockEnchantmentBook;
import net.epoxide.eplus.item.ItemBookSummoner;
import net.epoxide.eplus.item.ItemEnchantedScroll;
import net.epoxide.eplus.item.ItemTableUpgrade;
import net.epoxide.eplus.modifiers.ScrollModifier;
import net.epoxide.eplus.tileentity.TileEntityArcaneInscriber;
import net.epoxide.eplus.tileentity.TileEntityEnchantTable;
import net.epoxide.eplus.tileentity.TileEntityEnchantmentBook;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

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
    
    /**
     * A map of all enchantment type colors. The key is the enchantment type, and the Integer
     * is a RGB integer. Used when rendering scroll items.
     */
    private static Map<String, Integer> colorMap = new HashMap<String, Integer>();
    
    /**
     * A List of all modifiers that have been registered. Modifiers are used with the Arcane
     * Inscriber.
     */
    public static List<ScrollModifier> modifiers = new ArrayList<ScrollModifier>();
    
    public static Block eplusTable;
    public static Block eplusArcaneInscriber;
    public static Block blockEnchantmentBook;
    
    public static Item tableUpgrade;
    public static Item scroll;
    public static Item book;
    
    public static Buff bookBuff;
    
    /**
     * Initializes all of the blocks for the Enchanting Plus mod. Used to handle Block
     * construction and registry.
     */
    public static void initBlocks () {
        
        eplusTable = new BlockEnchantTable();
        GameRegistry.registerBlock(eplusTable, "advancedEnchantmentTable");
        GameRegistry.registerTileEntity(TileEntityEnchantTable.class, "eplus:advancedEnchantmentTable");
        
        eplusArcaneInscriber = new BlockArcaneInscriber();
        GameRegistry.registerBlock(eplusArcaneInscriber, "arcane_inscriber");
        GameRegistry.registerTileEntity(TileEntityArcaneInscriber.class, "eplus:arcane_inscriber");
        
        blockEnchantmentBook = new BlockEnchantmentBook();
        GameRegistry.registerBlock(blockEnchantmentBook, ItemBlockEnchantmentBook.class, "enchantment_book");
        GameRegistry.registerTileEntity(TileEntityEnchantmentBook.class, "eplus:enchantment_book");
        GameRegistry.addRecipe(new ItemStack(blockEnchantmentBook), new Object[]{" g ", "gbg", " g ", Character.valueOf('g'), Items.glowstone_dust, Character.valueOf('b'), Items.enchanted_book});
    }
    
    /**
     * Initializes all of the items for the Enchanting Plus mod. Used to handle Item
     * construction and registry.
     */
    public static void initItems () {
        
        tableUpgrade = new ItemTableUpgrade();
        GameRegistry.registerItem(tableUpgrade, "tableUpgrade");
        CraftingManager.getInstance().addRecipe(new ItemStack(tableUpgrade), "gbg", "o o", "geg", 'b', Items.writable_book, 'o', Blocks.obsidian, 'e', Items.ender_eye, 'g', Items.gold_ingot);
        
        scroll = new ItemEnchantedScroll();
        GameRegistry.registerItem(scroll, "enchantment_scroll");
        
        book = new ItemBookSummoner();
        GameRegistry.registerItem(book, "tomb_guardian");
    }
    
    /**
     * Initializes all of the colors for the vanilla enchantment types. Generic enchantments
     * are purple, armor enchantments are gray, weapone enchantments are red, fishing
     * enchantments are blue, and bow enchantments are dark green.
     */
    public static void initEnchantmentColors () {
        
        setEnchantmentColor(EnumEnchantmentType.all, 15029174);
        setEnchantmentColor(EnumEnchantmentType.armor, 10394268);
        setEnchantmentColor(EnumEnchantmentType.armor_feet, 10394268);
        setEnchantmentColor(EnumEnchantmentType.armor_legs, 10394268);
        setEnchantmentColor(EnumEnchantmentType.armor_torso, 10394268);
        setEnchantmentColor(EnumEnchantmentType.armor_head, 10394268);
        setEnchantmentColor(EnumEnchantmentType.weapon, 16711680);
        setEnchantmentColor(EnumEnchantmentType.digger, 9127187);
        setEnchantmentColor(EnumEnchantmentType.fishing_rod, 1596073);
        setEnchantmentColor(EnumEnchantmentType.breakable, 10394268);
        setEnchantmentColor(EnumEnchantmentType.bow, 29696);
    }
    
    /**
     * Initializes all of the modifiers added by the base mod.
     */
    public static void initModifiers () {
        
        addScrollModifier(new ScrollModifier(new ItemStack(Items.blaze_powder), -0.05f, 0.1f, false));
        addScrollModifier(new ScrollModifier(new ItemStack(Blocks.obsidian), 0.1f, -0.05f, false));
        addScrollModifier(new ScrollModifier(new ItemStack(Items.diamond), 0.25f, -0.05f, false));
        addScrollModifier(new ScrollModifier(new ItemStack(Items.emerald), -0.05f, 0.25f, false));
        addScrollModifier(new ScrollModifier(new ItemStack(Items.ender_pearl), 0.05f, 0f, false));
        addScrollModifier(new ScrollModifier(new ItemStack(Blocks.glowstone), 0f, 0.05f, false));
        addScrollModifier(new ScrollModifier(new ItemStack(Items.ender_eye), 0f, 0.1f, false));
    }
    
    /**
     * Used to initialize random things.
     */
    public static void initMisc () {
        
        bookBuff = new BuffBookFall();
        BuffHelper.registerBuff(bookBuff);
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
        
        if (!blacklistEnchantments.contains(enchantID)) {
            
            blacklistEnchantments.add(enchantID);
            EnchantingPlus.printDebugMessage(blacklister + " has succesfullt blocked an enchantment with the ID of " + enchantID);
        }
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
            EnchantingPlus.printDebugMessage(blacklister + " has successfully blacklisted " + name);
        }
    }
    
    /**
     * Checks if an Enchantment is on the blacklist.
     * 
     * @param enchant: The enchantment to check for.
     * @return boolean: True if the blacklist contains the ID of the passed enchantment.
     */
    public static boolean isBlacklisted (Enchantment enchant) {
        
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
    
    /**
     * Retrieves the color linked to an EnumEnchantmentType. If no color has been set, white
     * will be used.
     * 
     * @param enchType: The name of the EnumEnchantmentType you are getting the color for.
     * @return int: The color associated to the EnumEnchantmentType. If no color is set, white
     *         will be used.
     */
    public static int getEnchantmentColor (String enchType) {
        
        return (colorMap.containsKey(enchType)) ? colorMap.get(enchType) : 16777215;
    }
    
    /**
     * Sets a color to represent an enchantment type. This color is used in rendering the
     * scroll items, and possibly in other places.
     * 
     * @param enchType: The EnumEnchantmentType you are setting the color for.
     * @param color: An RGB integer that represents the color to set.
     */
    public static void setEnchantmentColor (EnumEnchantmentType enchType, int color) {
        
        setEnchantmentColor(enchType.name(), color);
    }
    
    /**
     * Sets a color to represent an enchantment type. This color is used in rendering the
     * scroll items, and possibly in other places.
     * 
     * @param enchType: The name of the EnumEnchantmentType you are setting the color for.
     * @param color: An RGB integer that represents the color to set.
     */
    public static void setEnchantmentColor (String enchType, int color) {
        
        if (!colorMap.containsKey(enchType)) {
            
            colorMap.put(enchType, color);
            EnchantingPlus.printDebugMessage("The color of enchantment type " + enchType + " has been set to " + color);
        }
    }
    
    /**
     * Registers a ScrollModifier with our List of modifiers.
     *
     * @param modifier: The modifier to register.
     */
    public static void addScrollModifier (ScrollModifier modifier) {
        
        modifiers.add(modifier);
    }
    
    /**
     * Retrieves a ScrollModifier using the modifier ItemStack as a key.
     * 
     * @param stack: The ItemStack associated with the modifier you are looking for.
     * @return ScrollModifier: If a valid modifier is found, it will be returned. Otherwise
     *         null.
     */
    public static ScrollModifier findScrollModifier (ItemStack stack) {
        
        for (ScrollModifier modifier : modifiers)
            if (ItemStackUtils.areStacksSimilar(modifier.stack, stack))
                return modifier;
                
        return null;
    }
}