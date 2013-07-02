package eplus.helper;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Map;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

/**
 * Helper class with Enchanting functions
 */
public class EnchantHelper {

    /**
     * Checks to see if item is enchanted
     *
     * @param itemStack the item to check
     * @return true if item is enchanted
     */
    public static boolean isItemEnchanted(ItemStack itemStack)
    {
        return itemStack.hasTagCompound()
                && ((itemStack.itemID != Item.enchantedBook.itemID) ? itemStack.stackTagCompound
                .hasKey("ench") : itemStack.stackTagCompound
                .hasKey("StoredEnchantments"));
    }

    /**
     * checks to see if item is enchantable
     *
     * @param itemStack the item to check
     * @return true if item can accept more enchantments
     */
    public static boolean isItemEnchantable(ItemStack itemStack)
    {
        return (itemStack.itemID == Item.book.itemID)
                || itemStack.isItemEnchantable();
    }

    /**
     * Checks to see if an enchantment can enchant an item
     *
     * @param itemStack the item to check
     * @param obj       the enchantment to add
     * @return true is item can accept the enchantment
     */
    public static boolean canEnchantItem(ItemStack itemStack, Enchantment obj)
    {
        ItemStack fakeBook = Item.enchantedBook
                .func_92111_a(new EnchantmentData(obj, 1));

        return itemStack.itemID == Item.book.itemID || obj.canApply(itemStack); // ||
        // itemStack.getItem().isBookEnchantable(itemStack,
        // fakeBook);
    }

    /**
     * adds enchantments to an item
     *
     * @param map       map of enchantments to add
     * @param itemStack the item to add enchantments to
     */
    public static void setEnchantments(Map map, ItemStack itemStack)
    {
        NBTTagList nbttaglist = new NBTTagList();

        for (Object o : map.keySet()) {
            int i = (Integer) o;
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setShort("id", (short) i);
            nbttagcompound.setShort("lvl",
                    (short) ((Integer) map.get(i)).intValue());
            nbttaglist.appendTag(nbttagcompound);

            if (itemStack.itemID == Item.book.itemID
                    || itemStack.itemID == Item.enchantedBook.itemID) {
                itemStack.itemID = Item.enchantedBook.itemID;
            }
        }

        if (nbttaglist.tagCount() > 0) {
            if (itemStack.itemID != Item.enchantedBook.itemID) {
                itemStack.setTagInfo("ench", nbttaglist);
            } else {
                itemStack.setTagInfo("StoredEnchantments", nbttaglist);
            }
        } else if (itemStack.hasTagCompound()) {
            if (itemStack.itemID != Item.enchantedBook.itemID) {
                itemStack.getTagCompound().removeTag("ench");
            } else {
                itemStack.getTagCompound().removeTag("StoredEnchantments");
                itemStack.stackTagCompound = null;
                itemStack.itemID = Item.book.itemID;
            }
        }
    }
}
