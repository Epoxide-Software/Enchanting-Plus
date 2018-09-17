package net.darkhax.eplus.api.event;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired on the client and the server when the cost of an enchantment is
 * calculated for e+.
 */
public class EnchantmentCostEvent extends Event {

    private final Enchantment enchantment;
    private final int level;
    private final int originalCost;
    private int cost;

    public EnchantmentCostEvent (int cost, Enchantment enchantment, int level) {

        super();
        this.cost = cost;
        this.originalCost = cost;
        this.enchantment = enchantment;
        this.level = level;
    }

    public Enchantment getEnchantment () {

        return this.enchantment;
    }

    public int getLevel () {

        return this.level;
    }

    public int getCost () {

        return this.cost;
    }

    public void setCost (int cost) {

        this.cost = cost;
    }

    public int getOriginalCost () {

        return this.originalCost;
    }

}