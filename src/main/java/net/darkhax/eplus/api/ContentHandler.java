package net.darkhax.eplus.api;

import java.util.HashMap;
import java.util.Map;

import net.darkhax.bookshelf.lib.ItemStackMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class ContentHandler {

    private static final ItemStackMap<String> BLACKLIST_ITEMS = new ItemStackMap<>(ItemStackMap.SIMILAR_WITH_NBT);
    private static final Map<Enchantment, String> BLACKLIST_ENCHANTMENTS = new HashMap<>();

    public static void blacklist (ItemStack stack, String blacklister) {

        BLACKLIST_ITEMS.put(stack, blacklister);
    }

    public static void blacklist (Enchantment enchantment, String blacklister) {

        BLACKLIST_ENCHANTMENTS.put(enchantment, blacklister);
    }

    public static boolean isValidItem (EntityPlayer player, ItemStack stack) {

        return stack.getItem().isEnchantable(stack) && !BLACKLIST_ITEMS.containsKey(stack);
    }

    public static boolean isValidEnchantment (EntityPlayer player, Enchantment enchantment) {

        return !BLACKLIST_ENCHANTMENTS.containsKey(enchantment);
    }

    public static String getBlacklister (ItemStack stack) {

        return BLACKLIST_ITEMS.get(stack);
    }

    public static String getBlacklister (Enchantment enchantment) {

        return BLACKLIST_ENCHANTMENTS.get(enchantment);
    }
}