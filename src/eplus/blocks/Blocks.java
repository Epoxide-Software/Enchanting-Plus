package eplus.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import eplus.EnchantingPlus;
import eplus.lib.ConfigurationSettings;

/**
 * Enchanting Plus
 * 
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class Blocks
{

    /**
     * Initializes all mod blocks
     */

    public static void init()
    {
        EnchantingPlus.log.info("Initializing Blocks.");
        final Block table = new BlockEnchantTable(ConfigurationSettings.tableID).setHardness(5.0F).setResistance(2000.0F).setUnlocalizedName("advancedEnchantmentTable");
        GameRegistry.registerBlock(table, table.getUnlocalizedName());
        LanguageRegistry.addName(table, "Advanced Enchantment Table");
        
        CraftingManager.getInstance().addRecipe(new ItemStack(table), "gbg", "oto", "geg", 'b', Item.writableBook, 'o', Block.obsidian, 't', Block.enchantmentTable, 'e', Item.eyeOfEnder, 'g', Item.ingotGold);

    }
}
