package net.darkhax.eplus.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public final class ContentHandler {

    /**
     * A list of all blacklisted enchantment IDs.
     */
    private static List<ResourceLocation> enchantBlacklist = new ArrayList<>();

    /**
     * A list of all blacklisted item IDs.
     */
    private static List<ResourceLocation> itemBlacklist = new ArrayList<>();

    /**
     * Attempts to blacklist an enchantment. If the enchantment has already been blacklisted,
     * or the enchantment is null, it will fail.
     *
     * @param enchant The Enchantment to blacklist.
     */
    public static void blacklistEnchantment (Enchantment enchant) {

        if (enchant != null && !enchantBlacklist.contains(enchant.getRegistryName())) {
            enchantBlacklist.add(enchant.getRegistryName());
        }
    }

    /**
     * Attempts to blacklist an item. If the item has already been blacklisted, or the item is
     * null, it will fail.
     *
     * @param item The Item to blacklist.
     */
    public static void blacklistItem (Item item) {

        if (item != null && !itemBlacklist.contains(item.getRegistryName())) {
            itemBlacklist.add(item.getRegistryName());
        }
    }

    /**
     * Initializes the blacklist for both enchantments and items.
     */
    public static void initBlacklist () {

        for (final String entry : ConfigurationHandler.blacklistedItems) {
            blacklistItem(Item.REGISTRY.getObject(new ResourceLocation(entry)));
        }

        for (final String entry : ConfigurationHandler.blacklistedEnchantments) {
            blacklistEnchantment(Enchantment.REGISTRY.getObject(new ResourceLocation(entry)));
        }
    }

    /**
     * Checks if an enchantment is blacklisted.
     *
     * @param enchant The enchantment to check for.
     * @return boolean Whether or not the enchantment is blacklisted.
     */
    public static boolean isEnchantmentBlacklisted (Enchantment enchant) {

        return enchant != null && enchantBlacklist.contains(enchant.getRegistryName());
    }

    /**
     * Checks if an item is blacklisted.
     *
     * @param item The item to check for.
     * @return Whether or not the item was blacklisted.
     */
    public static boolean isItemBlacklisted (Item item) {

        return item != null && itemBlacklist.contains(item.getRegistryName());
    }
}