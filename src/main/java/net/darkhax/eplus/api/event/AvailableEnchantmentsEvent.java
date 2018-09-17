package net.darkhax.eplus.api.event;

import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired on both the client and server, and is used to determine the types of
 * enchantments that can be applied to an item at the advanced table.
 */
public class AvailableEnchantmentsEvent extends Event {

    private final ItemStack stack;
    private List<Enchantment> enchantments;

    public AvailableEnchantmentsEvent (ItemStack stack, List<Enchantment> enchantments) {

        super();
        this.stack = stack;
        this.enchantments = enchantments;
    }

    public List<Enchantment> getEnchantments () {

        return this.enchantments;
    }

    public void setEnchantments (List<Enchantment> enchantments) {

        this.enchantments = enchantments;
    }

    public ItemStack getStack () {

        return this.stack;
    }

}
