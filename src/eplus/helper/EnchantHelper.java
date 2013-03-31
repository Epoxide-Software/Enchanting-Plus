package eplus.helper;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Iterator;
import java.util.Map;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class EnchantHelper {
    public static boolean isItemEnchanted(ItemStack itemStack) {
        return itemStack.hasTagCompound() && ((itemStack.itemID != Item.enchantedBook.itemID) ? itemStack.stackTagCompound.hasKey("ench") : itemStack.stackTagCompound.hasKey("StoredEnchantments"));
    }

    public static boolean isItemEnchantable(ItemStack itemStack) {
        return (itemStack.itemID == Item.book.itemID) || itemStack.isItemEnchantable();
    }

    public static boolean canEnchantItem(ItemStack itemStack, Enchantment obj) {
        return (itemStack.itemID == Item.book.itemID) || obj.func_92089_a(itemStack);
    }

    public static void setEnchantments(Map map, ItemStack itemStack) {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            int i = ((Integer) iterator.next()).intValue();
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setShort("id", (short) i);
            nbttagcompound.setShort("lvl", (short) ((Integer) map.get(Integer.valueOf(i))).intValue());
            nbttaglist.appendTag(nbttagcompound);

            if (itemStack.itemID == Item.book.itemID || itemStack.itemID == Item.enchantedBook.itemID) {
                Item.enchantedBook.func_92115_a(itemStack, new EnchantmentData(i, ((Integer) map.get(Integer.valueOf(i))).intValue()));
                BookHelper.addEnchantmentData(itemStack, new EnchantmentData(i, ((Integer) map.get(Integer.valueOf(i))).intValue()));
                itemStack.itemID = Item.enchantedBook.itemID;
            }
        }

        if (nbttaglist.tagCount() > 0) {
            if (itemStack.itemID != Item.enchantedBook.itemID) {
                itemStack.setTagInfo("ench", nbttaglist);
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
