package net.darkhax.eplus.libs;

import net.darkhax.bookshelf.item.ItemBlockBasic;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.eplus.block.BlockAdvancedTable;
import net.darkhax.eplus.block.BlockBookDecoration;
import net.darkhax.eplus.creativetab.CreativeTabEPlus;
import net.darkhax.eplus.item.ItemTableUpgrade;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class Content {

    public static RegistryHelper REGISTRY = new RegistryHelper("eplus").setTab(new CreativeTabEPlus()).enableAutoRegistration();

    // BLOCKS
    public static Block blockAdvancedTable;
    public static Block blockDecorativeBook;

    // ITEMS
    public static Item itemTableUpgrade;
    public static Item itemDecorativeBook;

    public static void registerBlocks () {

        blockAdvancedTable = REGISTRY.registerBlock(new BlockAdvancedTable(), "advanced_table");
        blockDecorativeBook = new BlockBookDecoration();
        REGISTRY.registerBlock(blockDecorativeBook, new ItemBlockBasic(blockDecorativeBook, BlockBookDecoration.TYPES, false), "decorative_book");
    }

    public static void registerItems () {

        itemTableUpgrade = REGISTRY.registerItem(new ItemTableUpgrade(), "table_upgrade");
    }
}
