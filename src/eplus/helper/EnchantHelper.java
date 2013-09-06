package eplus.helper;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

/**
 * Helper class with Enchanting functions
 */
public class EnchantHelper
{

    /**
     * Checks to see if an enchantment can enchant an item
     * 
     * @param itemStack
     *            the item to check
     * @param obj
     *            the enchantment to add
     * @return true is item can accept the enchantment
     */
    public static boolean canEnchantItem(ItemStack itemStack, Enchantment obj)
    {
        // Item.enchantedBook.get(new EnchantmentData(obj, 1));

        return itemStack.itemID == Item.book.itemID || obj.canApply(itemStack); 
        // itemStack.getItem().isBookEnchantable(itemStack,
        // fakeBook);
    }

    /**
     * checks to see if item is enchantable
     * 
     * @param itemStack
     *            the item to check
     * @return true if item can accept more enchantments
     */
    public static boolean isItemEnchantable(ItemStack itemStack)
    {
        boolean flag = true;
        if (itemStack.hasTagCompound())
        {
            flag = !itemStack.getTagCompound().hasKey("charge");
        }
        
        return itemStack.getItem().getItemEnchantability() > 0 && (itemStack.itemID == Item.book.itemID || itemStack.isItemEnchantable() && flag);
    }

    /**
     * Checks to see if item is enchanted
     * 
     * @param itemStack
     *            the item to check
     * @return true if item is enchanted
     */
    public static boolean isItemEnchanted(ItemStack itemStack)
    {
        return itemStack.hasTagCompound()
                && (itemStack.itemID != Item.enchantedBook.itemID ? itemStack.stackTagCompound.hasKey("ench") : itemStack.stackTagCompound.hasKey("StoredEnchantments"));
    }

    /**
     * adds enchantments to an item
     * 
     * @param map
     *            map of enchantments to add
     * @param itemStack
     *            the item to add enchantments to
     * @param levels
     * @param player
     */
    public static void setEnchantments(Map<?, ?> map, ItemStack itemStack, HashMap<Integer, Integer> levels, EntityPlayer player)
    {
        final NBTTagList nbttaglist = new NBTTagList();

        NBTTagList restrictions = null;

        if (itemStack.hasTagCompound())
        {
            restrictions = itemStack.getTagCompound().getTagList("restrictions");
        }
        else
        {
            restrictions = new NBTTagList();
        }

        for (final Object o : map.keySet())
        {
            final int i = (Integer) o;
            final NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setShort("id", (short) i);
            nbttagcompound.setShort("lvl", (short) ((Integer) map.get(i)).intValue());
            nbttaglist.appendTag(nbttagcompound);

            int startLevel = (Integer) map.get(i);
            try
            {
                startLevel = levels.get(i);
            } catch (NullPointerException e)
            {

            }

            for (int y = startLevel; y <= (Integer) map.get(i); y++)
            {
                if (containsKey(restrictions, i, y))
                {
                    continue;
                }

                NBTTagCompound compound = new NBTTagCompound();
                compound.setShort("id", (short) i);
                compound.setShort("lvl", (short) y);
                compound.setString("player", player.username);
                restrictions.appendTag(compound);

            }
        }

        if (itemStack.itemID == Item.book.itemID || itemStack.itemID == Item.enchantedBook.itemID)
        {
            itemStack.itemID = Item.enchantedBook.itemID;
        }

        if (nbttaglist.tagCount() > 0)
        {
            if (itemStack.itemID != Item.enchantedBook.itemID)
            {
                itemStack.setTagInfo("ench", nbttaglist);
            }
            else
            {
                itemStack.setTagInfo("StoredEnchantments", nbttaglist);
            }
            itemStack.setTagInfo("restrictions", restrictions);
        }
        else if (itemStack.hasTagCompound())
        {
            if (itemStack.itemID != Item.enchantedBook.itemID)
            {
                itemStack.getTagCompound().removeTag("ench");
            }
            else
            {
                itemStack.getTagCompound().removeTag("StoredEnchantments");
                itemStack.stackTagCompound = null;
                itemStack.itemID = Item.book.itemID;
            }
        }
    }

    public static boolean containsKey(NBTTagList restrictions, int id, int y)
    {
        for (int k = 0; k < restrictions.tagCount(); k++)
        {
            NBTTagCompound tag = (NBTTagCompound) restrictions.tagAt(k);
            if (tag.getShort("lvl") == y && tag.getShort("id") == id)
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isNewItemEnchantable(Item item)
    {
        if (item.equals(Item.enchantedBook)) 
        {
            return isItemEnchantable(new ItemStack(Item.book));
        }
        return isItemEnchantable(new ItemStack(item));
    }
}
