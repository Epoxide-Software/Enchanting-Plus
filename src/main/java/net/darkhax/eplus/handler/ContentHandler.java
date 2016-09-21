package net.darkhax.eplus.handler;

import java.util.ArrayList;
import java.util.List;

import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.darkhax.eplus.block.BlockAdvancedTable;
import net.darkhax.eplus.block.BlockBookDecoration;
import net.darkhax.eplus.item.ItemBook;
import net.darkhax.eplus.item.ItemScroll;
import net.darkhax.eplus.item.ItemTableUpgrade;
import net.darkhax.eplus.modifiers.ScrollModifier;
import net.darkhax.eplus.tileentity.TileEntityAdvancedTable;
import net.darkhax.eplus.tileentity.TileEntityDecoration;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ContentHandler {
    
    /**
     * A list of all blacklisted enchantment IDs.
     */
    private static List<ResourceLocation> enchantBlacklist = new ArrayList<ResourceLocation>();
    
    /**
     * A list of all blacklisted item IDs.
     */
    private static List<ResourceLocation> itemBlacklist = new ArrayList<ResourceLocation>();
    
    /**
     * A map of all enchantment type colors. The key is the enchantment type, and the Integer
     * is a RGB integer. Used when rendering scroll items.
     */
    
    /**
     * A List of all modifiers that have been registered. Modifiers are used with the Arcane
     * Inscriber.
     */
    public static List<ScrollModifier> modifiers = new ArrayList<ScrollModifier>();
    
    /**
     * The amount of achievements added by the mod. This is used purely for calculating the
     * placement of the achievement on the achievement tab.
     */
    private static int achievementCount = 0;
    
    public static Block blockAdvancedTable;
    public static Block blockArcaneInscriber;
    public static Block blockDecoration;
    
    public static Item itemTableUpgrade;
    public static Item itemScroll;
    
    /**
     * Registers a ScrollModifier with our List of modifiers.
     *
     * @param modifier The modifier to register.
     */
    public static void addScrollModifier (ScrollModifier modifier) {
        
        modifiers.add(modifier);
    }
    
    /**
     * Attempts to blacklist an enchantment. If the enchantment has already been blacklisted,
     * or the enchantment is null, it will fail.
     * 
     * @param enchant The Enchantment to blacklist.
     */
    public static void blacklistEnchantment (Enchantment enchant) {
        
        if (enchant != null && !enchantBlacklist.contains(enchant.getRegistryName()))
            enchantBlacklist.add(enchant.getRegistryName());
    }
    
    /**
     * Attempts to blacklist an item. If the item has already been blacklisted, or the item is
     * null, it will fail.
     * 
     * @param item The Item to blacklist.
     */
    public static void blacklistItem (Item item) {
        
        if (item != null && !itemBlacklist.contains(item.getRegistryName()))
            itemBlacklist.add(item.getRegistryName());
    }
    
    /**
     * Retrieves a ScrollModifier using the modifier ItemStack as a key.
     * 
     * @param stack The ItemStack associated with the modifier you are looking for.
     * @return ScrollModifier If a valid modifier is found, it will be returned. Otherwise
     *         null.
     */
    public static ScrollModifier findScrollModifier (ItemStack stack) {
        
        for (final ScrollModifier modifier : modifiers)
            if (ItemStackUtils.areStacksSimilar(modifier.stack, stack))
                return modifier;
                
        return null;
    }
    
    /**
     * Initializes the blacklist for both enchantments and items.
     */
    public static void initBlacklist () {
        
        for (final String entry : ConfigurationHandler.blacklistedItems)
            blacklistItem(Item.REGISTRY.getObject(new ResourceLocation(entry)));
            
        for (final String entry : ConfigurationHandler.blacklistedEnchantments)
            blacklistEnchantment(Enchantment.REGISTRY.getObject(new ResourceLocation(entry)));
    }
    
    /**
     * Initializes all of the blocks for the Enchanting Plus mod. Used to handle Block
     * construction and registry.
     */
    public static void initBlocks () {
        
        blockAdvancedTable = new BlockAdvancedTable();
        registerBlock(blockAdvancedTable, "advanced_table");
        GameRegistry.registerTileEntity(TileEntityAdvancedTable.class, "advanced_table");
        
        blockDecoration = new BlockBookDecoration();
        registerBlock(blockDecoration, new ItemBook(), "decoration");
        GameRegistry.registerTileEntity(TileEntityDecoration.class, "decoration");
    }
    
    /**
     * Initializes all of the items for the Enchanting Plus mod. Used to handle Item
     * construction and registry.
     */
    public static void initItems () {
        
        itemTableUpgrade = new ItemTableUpgrade();
        registerItem(itemTableUpgrade, "table_upgrade");
        
        itemScroll = new ItemScroll();
        registerItem(itemScroll, "scroll");
    }
    
    /**
     * Initializes all vanilla modifiers.
     */
    public static void initModifiers () {
        
        addScrollModifier(new ScrollModifier(new ItemStack(Items.BLAZE_POWDER), -0.05f, 0.1f, false));
        addScrollModifier(new ScrollModifier(new ItemStack(Blocks.OBSIDIAN), 0.1f, -0.05f, false));
        addScrollModifier(new ScrollModifier(new ItemStack(Items.DIAMOND), 0.25f, -0.05f, false));
        addScrollModifier(new ScrollModifier(new ItemStack(Items.EMERALD), -0.05f, 0.25f, false));
        addScrollModifier(new ScrollModifier(new ItemStack(Items.ENDER_PEARL), 0.05f, 0f, false));
        addScrollModifier(new ScrollModifier(new ItemStack(Blocks.GLOWSTONE), 0f, 0.05f, false));
        addScrollModifier(new ScrollModifier(new ItemStack(Items.ENDER_EYE), 0f, 0.1f, false));
    }
    
    /**
     * Initializes all crafting recipes.
     */
    public static void initRecipes () {
        
        GameRegistry.addRecipe(new ItemStack(itemTableUpgrade), new Object[] { "gbg", "o o", "geg", 'b', Items.WRITABLE_BOOK, 'o', Blocks.OBSIDIAN, 'e', Items.ENDER_EYE, 'g', Items.GOLD_INGOT });
        GameRegistry.addRecipe(new ItemStack(blockAdvancedTable), new Object[] { "gbg", "oto", "geg", 'b', Items.WRITABLE_BOOK, 'o', Blocks.OBSIDIAN, 'e', Items.ENDER_EYE, 'g', Items.GOLD_INGOT, 't', Blocks.ENCHANTING_TABLE });
        GameRegistry.addRecipe(new ItemStack(blockDecoration), new Object[] { " g ", "gbg", " g ", 'g', Items.GLOWSTONE_DUST, 'b', Items.ENCHANTED_BOOK });
        GameRegistry.addShapelessRecipe(new ItemStack(blockAdvancedTable), new Object[] { Blocks.ENCHANTING_TABLE, itemTableUpgrade });
    }
    
    /**
     * Checks if an enchantment is blacklisted.
     * 
     * @param enchant The enchantment to check for.
     * @return boolean Whether or not the enchantment is blacklisted.
     */
    public static boolean isEnchantmentBlacklisted (Enchantment enchant) {
        
        return enchant != null && enchantBlacklist.contains(enchant.getRegistryName());
    }
    
    /**
     * Checks if an item is blacklisted.
     * 
     * @param item The item to check for.
     * @return Whether or not the item was blacklisted.
     */
    public static boolean isItemBlacklisted (Item item) {
        
        return item != null && itemBlacklist.contains(item.getRegistryName());
    }
    
    /**
     * Creates a new Achievement to be used in the mod. The posX and posY are automatically
     * generated.
     * 
     * @param key The localization key to use for both the description and the title.
     * @param item The Item to show in the tab. Also works for blocks.
     * @return Achievement The instance of Achievement created.
     */
    public static Achievement registerAchievement (String key, Item item) {
        
        final int posX = achievementCount % 8;
        final int posY = achievementCount / 8;
        achievementCount++;
        
        return new Achievement(key, key, posX, posY + 1, item, null).registerStat();
    }
    
    /**
     * Provides the same functionality as older forge tile registration.
     * 
     * @param block The block to register.
     * @param ID The ID to register the block with.
     */
    private static void registerBlock (Block block, ItemBlock item, String ID) {
        
        block.setRegistryName(ID);
        GameRegistry.register(block);
        GameRegistry.register(item, block.getRegistryName());
    }
    
    /**
     * Provides the same functionality as older forge tile registration.
     * 
     * @param block The block to register.
     * @param ID The ID to register the block with.
     */
    private static void registerBlock (Block block, String ID) {
        
        block.setRegistryName(ID);
        GameRegistry.register(block);
        GameRegistry.register(new ItemBlock(block), block.getRegistryName());
    }
    
    /**
     * Provides the same functionality as older forge item registration.
     * 
     * @param item The item to register.
     * @param ID The ID to register the item with.
     */
    private static void registerItem (Item item, String ID) {
        
        if (item.getRegistryName() == null)
            item.setRegistryName(ID);
            
        GameRegistry.register(item);
    }
}