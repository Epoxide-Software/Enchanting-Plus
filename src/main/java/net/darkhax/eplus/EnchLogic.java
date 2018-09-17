package net.darkhax.eplus;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public final class EnchLogic {

    public static int calculateNewEnchCost (Enchantment enchantment, int level) {

        // Base cost is equal to roughly 2.5 levels of EXP.
        int cost = 25;

        // Cost is multiplied up to 10, based on rarity of the enchant.
        // Rarer the enchant, higher the cost.
        cost *= Math.max(11 - enchantment.getRarity().getWeight(), 1);

        // Linear cost increase based on level.
        cost *= level;

        // The cost factor is applied. Default is 1.5.
        // TODO make configurable
        cost *= 1.5f;

        // Curses cost even more to apply
        if (enchantment.isCurse()) {

            // TODO make configurable
            cost *= 1.5f;
        }

        // Treasures cost more to apply
        if (enchantment.isTreasureEnchantment()) {

            // TODO make configurable
            cost *= 1.5f;
        }

        return cost;
    }
    
    public static List<Enchantment> getValidEnchantments (ItemStack stack) {

        //TODO add whitelist and blacklist
        final List<Enchantment> enchList = new ArrayList<>();

        if (!stack.isEmpty() && (stack.isItemEnchantable() || stack.isItemEnchanted())) {
            
            for (final Enchantment enchantment : Enchantment.REGISTRY) {

                if (stack.isItemEnchanted()) {

                    if (enchantment.canApply(stack) && !enchantment.isCurse() && !enchantment.isTreasureEnchantment()) {

                        enchList.add(enchantment);
                    }
                }
                
                else {

                    if (enchantment.type.canEnchantItem(stack.getItem()) && !enchantment.isCurse() && !enchantment.isTreasureEnchantment()) {

                        enchList.add(enchantment);
                    }
                }
            }
        }

        return enchList;
    }
}
