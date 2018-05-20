package net.darkhax.eplus;

import net.minecraft.enchantment.Enchantment;

public final class EnchLogic {
    
    public static int calculateNewEnchCost(Enchantment enchantment, int level) {
        
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
        if(enchantment.isCurse()) {
            
            // TODO make configurable
            cost *= 1.5f;
        }
        
        // Treasures cost more to apply
        if(enchantment.isTreasureEnchantment()) {
            
            // TODO make configurable
            cost *= 1.5f;
        }
        
        return cost;
    }
}
