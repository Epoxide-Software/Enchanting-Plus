package net.darkhax.eplus.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.darkhax.bookshelf.util.StackUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public final class Blacklist {

    private static final List<ItemStack> BLACKLIST_ITEMS = new ArrayList<>();
    private static final List<Enchantment> BLACKLIST_ENCHANTMENTS = new ArrayList<>();

    private static final Map<Enchantment, Integer> MAX_LEVELS = new HashMap<>();

    public static void blacklist (ItemStack stack) {

        BLACKLIST_ITEMS.add(stack);
    }

    public static void blacklist (Enchantment enchantment) {

        BLACKLIST_ENCHANTMENTS.add(enchantment);
    }

    public static boolean isItemBlacklisted (ItemStack stack) {

        for (final ItemStack blacklisted : BLACKLIST_ITEMS) {

            if (StackUtils.areStacksSimilar(blacklisted, stack)) {

                return true;
            }
        }

        return false;
    }

    public static boolean isEnchantmentBlacklisted (Enchantment enchantment) {

        return BLACKLIST_ENCHANTMENTS.contains(enchantment);
    }
}
