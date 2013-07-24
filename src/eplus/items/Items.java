package eplus.items;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import eplus.EnchantingPlus;
import eplus.lib.ConfigurationSettings;

public class Items
{

    public static void init()
    {
        EnchantingPlus.log.info("Initializing Items.");
        final Item tableUpgrade = new ItemTableUpgrade(ConfigurationSettings.upgradeID).setUnlocalizedName("tableUpgrade");
        LanguageRegistry.addName(tableUpgrade, "Table Upgrade");
        
        CraftingManager.getInstance().addRecipe(new ItemStack(tableUpgrade), "gbg", "o o", "geg", 'b', Item.writableBook, 'o', Block.obsidian, 'e', Item.eyeOfEnder, 'g', Item.ingotGold);

    }

}
