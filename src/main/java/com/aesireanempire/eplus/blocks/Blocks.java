package com.aesireanempire.eplus.blocks;

import com.aesireanempire.eplus.EnchantingPlus;
import com.aesireanempire.eplus.lib.ConfigurationSettings;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

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

    public static Block table;

    public static void init()
    {
        EnchantingPlus.log.info("Initializing Blocks.");
        table = new BlockEnchantTable(ConfigurationSettings.tableID).setHardness(5.0F).setResistance(2000.0F).setBlockName("advancedEnchantmentTable");
        GameRegistry.registerBlock(table, table.getUnlocalizedName());
        LanguageRegistry.addName(table, "Advanced Enchantment Table");

        CraftingManager.getInstance()
                .addRecipe(new ItemStack(table), "gbg", "oto", "geg", 'b', Items.writable_book, 'o', net.minecraft.init.Blocks.obsidian, 't', net.minecraft.init.Blocks.enchanting_table, 'e',
                        Items.ender_eye, 'g', Items.gold_ingot);

    }
}
