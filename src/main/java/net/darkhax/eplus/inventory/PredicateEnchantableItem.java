package net.darkhax.eplus.inventory;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class PredicateEnchantableItem implements Predicate<ItemStack> {
    
    public static final Predicate<ItemStack> INSTANCE = new PredicateEnchantableItem();
    
    @Override
    public boolean test(ItemStack stack) {
        return stack.isItemEnchantable() || stack.isItemEnchanted() || stack.getItem() == Items.BOOK || stack.getItem() == Items.ENCHANTED_BOOK;
    }
}
