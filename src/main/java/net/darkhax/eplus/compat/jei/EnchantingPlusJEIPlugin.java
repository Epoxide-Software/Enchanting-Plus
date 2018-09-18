package net.darkhax.eplus.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import net.darkhax.eplus.EnchantingPlus;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class EnchantingPlusJEIPlugin implements IModPlugin {

    @Override
    public void register (IModRegistry registry) {

        this.addDescription(registry, new ItemStack(EnchantingPlus.itemAdvancedTable), "advancedtable");
        this.addDescription(registry, new ItemStack(EnchantingPlus.itemTableUpgrade), "upgrade");
        this.addDescription(registry, new ItemStack(EnchantingPlus.itemDecorativeBook), "decorative");
    }

    private void addDescription (IModRegistry registry, ItemStack stack, String key) {

        registry.<ItemStack> addIngredientInfo(stack, VanillaTypes.ITEM, "jei.eplus." + key);
    }
}