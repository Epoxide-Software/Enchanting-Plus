package net.darkhax.eplus.handler;

import java.util.ArrayList;
import java.util.List;

import net.darkhax.bookshelf.util.OreDictUtils;
import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.eplus.block.BlockAdvancedTable;
import net.darkhax.eplus.block.BlockBookDecoration;
import net.darkhax.eplus.item.ItemBook;
import net.darkhax.eplus.item.ItemScroll;
import net.darkhax.eplus.item.ItemTableUpgrade;
import net.darkhax.eplus.libs.Constants;
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
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@EventBusSubscriber
public final class ContentHandler {
    
    /**
     * A list of all blacklisted enchantment IDs.
     */
    private static List<ResourceLocation> enchantBlacklist = new ArrayList<>();
    
    /**
     * A list of all blacklisted item IDs.
     */
    private static List<ResourceLocation> itemBlacklist = new ArrayList<>();
    
    /**
     * A map of all enchantment type colors. The key is the enchantment type, and the Integer
     * is a RGB integer. Used when rendering scroll items.
     */
    
    /**
     * A List of all modifiers that have been registered. Modifiers are used with the Arcane
     * Inscriber.
     */
    public static List<ScrollModifier> modifiers = new ArrayList<>();
    
    /**
     * The amount of achievements added by the mod. This is used purely for calculating the
     * placement of the achievement on the achievement tab.
     */
    //private static int achievementCount = 0;
    
    public static Block blockAdvancedTable;
    public static Block blockArcaneInscriber;
    public static Block blockDecoration;
    
    public static Item itemTableUpgrade;
    public static Item itemScroll;
    
    public static final List<Item> ITEMS = new ArrayList<Item>();
    public static final List<Block> BLOCKS = new ArrayList<Block>();
    public static final List<IRecipe> RECIPES = new ArrayList<IRecipe>();
    
    @SubscribeEvent
    public static void onItemRegister(Register<Item> event){
    	initItems();
    	event.getRegistry().registerAll(ITEMS.toArray(new Item[0]));
    }
    
    @SubscribeEvent
    public static void onBlockRegister(Register<Block> event){
    	initBlocks();
    	event.getRegistry().registerAll(BLOCKS.toArray(new Block[0]));
    }
    
    @SubscribeEvent
    public static void onRecipeRegister(Register<IRecipe> event){
    	initRecipes();
    	event.getRegistry().registerAll(new IRecipe[0]);
    }
    
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
            if (StackUtils.areStacksSimilar(modifier.stack, stack))
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
    
    private static int j = 0;
    
    private static void addRecipe(IRecipe rec){
    	if(rec.getRegistryName() == null) rec.setRegistryName(new ResourceLocation(Constants.MOD_ID, "recipe" + j++));
    	RECIPES.add(rec);
    }
    
    /**
     * Initializes all crafting recipes.
     */
    public static void initRecipes () {
        
        addRecipe(new ShapedOreRecipe(new ResourceLocation(Constants.MOD_ID, "recipe" + j++), new ItemStack(itemTableUpgrade), new Object[] { "gbg", "o o", "geg", 'b', Items.WRITABLE_BOOK, 'o', OreDictUtils.OBSIDIAN, 'e', Items.ENDER_EYE, 'g', OreDictUtils.INGOT_GOLD }));
        addRecipe(new ShapedOreRecipe(new ResourceLocation(Constants.MOD_ID, "recipe" + j++), new ItemStack(blockAdvancedTable), new Object[] { "gbg", "oto", "geg", 'b', Items.WRITABLE_BOOK, 'o', OreDictUtils.OBSIDIAN, 'e', Items.ENDER_EYE, 'g', OreDictUtils.INGOT_GOLD, 't', Blocks.ENCHANTING_TABLE }));
        addRecipe(new ShapedOreRecipe(new ResourceLocation(Constants.MOD_ID, "recipe" + j++), new ItemStack(blockDecoration, 1, 0), new Object[] { " g ", "gbg", " g ", 'g', OreDictUtils.DUST_GLOWSTONE, 'b', Items.ENCHANTED_BOOK }));
        addRecipe(new ShapedOreRecipe(new ResourceLocation(Constants.MOD_ID, "recipe" + j++), new ItemStack(blockDecoration, 1, 1), new Object[] { " g ", "gbg", " g ", 'g', Items.BOOK, 'b', Items.ENCHANTED_BOOK }));
        addRecipe(new ShapedOreRecipe(new ResourceLocation(Constants.MOD_ID, "recipe" + j++), new ItemStack(blockDecoration, 1, 2), new Object[] { " g ", "gbg", " g ", 'g', OreDictUtils.GEM_PRISMARINE, 'b', Items.ENCHANTED_BOOK }));
        addRecipe(new ShapedOreRecipe(new ResourceLocation(Constants.MOD_ID, "recipe" + j++), new ItemStack(blockDecoration, 1, 3), new Object[] { " g ", "gbg", " g ", 'g', OreDictUtils.INGOT_BRICK_NETHER, 'b', Items.ENCHANTED_BOOK }));
        addRecipe(new ShapedOreRecipe(new ResourceLocation(Constants.MOD_ID, "recipe" + j++), new ItemStack(blockDecoration, 1, 5), new Object[] { " g ", "gbg", " g ", 'g', OreDictUtils.PAPER, 'b', Items.ENCHANTED_BOOK }));
        addRecipe(new ShapedOreRecipe(new ResourceLocation(Constants.MOD_ID, "recipe" + j++), new ItemStack(blockDecoration, 1, 6), new Object[] { " g ", "gbg", " g ", 'g', OreDictUtils.INGOT_IRON, 'b', Items.ENCHANTED_BOOK }));
        addRecipe(new ShapelessOreRecipe(new ResourceLocation(Constants.MOD_ID, "recipe" + j++), new ItemStack(blockAdvancedTable), new Object[] { Blocks.ENCHANTING_TABLE, itemTableUpgrade }));
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
    /*
    public static Achievement registerAchievement (String key, Item item) {
        
        final int posX = achievementCount % 8;
        final int posY = achievementCount / 8;
        achievementCount++;
        
        return new Achievement(key, key, posX, posY + 1, item, null).registerStat();
    }*/
    
    /**
     * Provides the same functionality as older forge tile registration.
     * 
     * @param block The block to register.
     * @param ID The ID to register the block with.
     */
    private static void registerBlock (Block block, ItemBlock item, String ID) {
        
        block.setRegistryName(ID);
        BLOCKS.add(block);
        ITEMS.add(item.setRegistryName(block.getRegistryName()));
    }
    
    /**
     * Provides the same functionality as older forge tile registration.
     * 
     * @param block The block to register.
     * @param ID The ID to register the block with.
     */
    private static void registerBlock (Block block, String ID) {
        
        block.setRegistryName(ID);
        BLOCKS.add(block);
        ITEMS.add(new ItemBlock(block).setRegistryName(block.getRegistryName()));
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
        
        ITEMS.add(item);
    }
}