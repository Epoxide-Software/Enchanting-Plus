package net.darkhax.eplus.libs;

import net.darkhax.bookshelf.item.ItemBlockBasic;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.bookshelf.util.OreDictUtils;
import net.darkhax.eplus.block.BlockAdvancedTable;
import net.darkhax.eplus.block.BlockBookDecoration;
import net.darkhax.eplus.creativetab.CreativeTabEPlus;
import net.darkhax.eplus.item.ItemTableUpgrade;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class Content {

    public static RegistryHelper REGISTRY = new RegistryHelper("eplus").setTab(new CreativeTabEPlus()).enableAutoRegistration();

    // BLOCKS
    public static Block blockAdvancedTable;
    public static Block blockDecorativeBook;

    // ITEMS
    public static ItemBlock itemAdvancedTable;
    public static Item itemTableUpgrade;
    public static ItemBlock itemDecorativeBook;

    public static void registerBlocks () {

        blockAdvancedTable = new BlockAdvancedTable();
        itemAdvancedTable = new ItemBlock(blockAdvancedTable);
        REGISTRY.registerBlock(blockAdvancedTable, itemAdvancedTable, "advanced_table");

        blockDecorativeBook = new BlockBookDecoration();
        itemDecorativeBook = new ItemBlockBasic(blockDecorativeBook, BlockBookDecoration.TYPES, false);
        REGISTRY.registerBlock(blockDecorativeBook, itemDecorativeBook, "decorative_book");
    }

    public static void registerItems () {

        itemTableUpgrade = REGISTRY.registerItem(new ItemTableUpgrade(), "table_upgrade");
    }

    public static void registerRecipes () {

        REGISTRY.registerRecipe("upgrade", new ShapedOreRecipe(null, new ItemStack(itemTableUpgrade), new Object[] { "gbg", "o o", "geg", 'b', Items.WRITABLE_BOOK, 'o', OreDictUtils.OBSIDIAN, 'e', Items.ENDER_EYE, 'g', OreDictUtils.INGOT_GOLD }));
        REGISTRY.registerRecipe("table", new ShapedOreRecipe(null, new ItemStack(itemAdvancedTable), new Object[] { "gbg", "oto", "geg", 'b', Items.WRITABLE_BOOK, 'o', OreDictUtils.OBSIDIAN, 'e', Items.ENDER_EYE, 'g', OreDictUtils.INGOT_GOLD, 't', Blocks.ENCHANTING_TABLE }));
        REGISTRY.registerRecipe("book_eplus", new ShapedOreRecipe(null, new ItemStack(itemDecorativeBook, 1, 0), new Object[] { " g ", "gbg", " g ", 'g', OreDictUtils.DUST_GLOWSTONE, 'b', Items.ENCHANTED_BOOK }));
        REGISTRY.registerRecipe("book_vanilla", new ShapedOreRecipe(null, new ItemStack(itemDecorativeBook, 1, 1), new Object[] { " g ", "gbg", " g ", 'g', Items.BOOK, 'b', Items.ENCHANTED_BOOK }));
        REGISTRY.registerRecipe("booke_prismarine", new ShapedOreRecipe(null, new ItemStack(itemDecorativeBook, 1, 2), new Object[] { " g ", "gbg", " g ", 'g', OreDictUtils.GEM_PRISMARINE, 'b', Items.ENCHANTED_BOOK }));
        REGISTRY.registerRecipe("book_nether", new ShapedOreRecipe(null, new ItemStack(itemDecorativeBook, 1, 3), new Object[] { " g ", "gbg", " g ", 'g', OreDictUtils.INGOT_BRICK_NETHER, 'b', Items.ENCHANTED_BOOK }));
        REGISTRY.registerRecipe("book_white", new ShapedOreRecipe(null, new ItemStack(itemDecorativeBook, 1, 5), new Object[] { " g ", "gbg", " g ", 'g', OreDictUtils.PAPER, 'b', Items.ENCHANTED_BOOK }));
        REGISTRY.registerRecipe("book_metal", new ShapedOreRecipe(null, new ItemStack(itemDecorativeBook, 1, 6), new Object[] { " g ", "gbg", " g ", 'g', OreDictUtils.INGOT_IRON, 'b', Items.ENCHANTED_BOOK }));
        REGISTRY.registerRecipe("shapeless_upgrade", new ShapelessOreRecipe(null, new ItemStack(itemAdvancedTable), new Object[] { Blocks.ENCHANTING_TABLE, itemTableUpgrade }));
    }
}
