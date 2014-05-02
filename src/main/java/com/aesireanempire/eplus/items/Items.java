package com.aesireanempire.eplus.items;

import com.aesireanempire.eplus.EnchantingPlus;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class Items
{

    public static void init()
    {
        EnchantingPlus.log.info("Initializing Items.");
        final Item tableUpgrade = new ItemTableUpgrade().setUnlocalizedName("tableUpgrade");
        LanguageRegistry.addName(tableUpgrade, "Table Upgrade");

        CraftingManager.getInstance()
                .addRecipe(new ItemStack(tableUpgrade), "gbg", "o o", "geg", 'b', net.minecraft.init.Items.writable_book, 'o', Blocks.obsidian, 'e', net.minecraft.init.Items.ender_eye, 'g',
                        net.minecraft.init.Items.gold_ingot);

    }

}
