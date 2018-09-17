package net.darkhax.eplus;

import java.util.function.Predicate;

import net.darkhax.bookshelf.item.ItemBlockBasic;
import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.network.NetworkHandler;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.bookshelf.util.OreDictUtils;
import net.darkhax.eplus.api.Blacklist;
import net.darkhax.eplus.block.BlockAdvancedTable;
import net.darkhax.eplus.block.BlockBookDecoration;
import net.darkhax.eplus.creativetab.CreativeTabEPlus;
import net.darkhax.eplus.item.ItemTableUpgrade;
import net.darkhax.eplus.network.GuiHandler;
import net.darkhax.eplus.network.messages.MessageEnchant;
import net.darkhax.eplus.network.messages.MessageSliderUpdate;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod(modid = "eplus", name = "Enchanting Plus", version = "@VERSION@", dependencies = "required-after:bookshelf@[2.3.553,);", certificateFingerprint = "@FINGERPRINT@")
public final class EnchantingPlus {

    public static final NetworkHandler NETWORK = new NetworkHandler("eplus");
    public static final LoggingHelper LOG = new LoggingHelper("Enchanting Plus");
    public static final RegistryHelper REGISTRY = new RegistryHelper("eplus").setTab(new CreativeTabEPlus()).enableAutoRegistration();

    // BLOCKS
    public static Block blockAdvancedTable;
    public static Block blockDecorativeBook;

    // ITEMS
    public static ItemBlock itemAdvancedTable;
    public static Item itemTableUpgrade;
    public static ItemBlock itemDecorativeBook;

    public static final Predicate<ItemStack> TEST_ENCHANTABILITY = (stack) -> !Blacklist.isItemBlacklisted(stack) && (stack.isItemEnchantable() || stack.isItemEnchanted() || stack.getItem() == Items.BOOK || stack.getItem() == Items.ENCHANTED_BOOK);

    @Instance("eplus")
    public static EnchantingPlus instance;

    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        ConfigurationHandler.initConfig(event.getSuggestedConfigurationFile());

        NETWORK.register(MessageEnchant.class, Side.SERVER);
        NETWORK.register(MessageSliderUpdate.class, Side.SERVER);

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        blockAdvancedTable = new BlockAdvancedTable();
        itemAdvancedTable = new ItemBlock(blockAdvancedTable);
        REGISTRY.registerBlock(blockAdvancedTable, itemAdvancedTable, "advanced_table");

        blockDecorativeBook = new BlockBookDecoration();
        itemDecorativeBook = new ItemBlockBasic(blockDecorativeBook, BlockBookDecoration.TYPES, false);
        REGISTRY.registerBlock(blockDecorativeBook, itemDecorativeBook, "decorative_book");

        itemTableUpgrade = REGISTRY.registerItem(new ItemTableUpgrade(), "table_upgrade");

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

    @EventHandler
    public void onLoadComplete (FMLLoadCompleteEvent event) {

        ConfigurationHandler.buildBlacklist();
    }
}