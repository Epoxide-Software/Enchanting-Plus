package eplus.helper;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

/**
 * Helper class for book enchanting functions
 */
public class BookHelper
{

    /**
     * Adds enchantments to book
     * 
     * @param itemStack
     *            the book to be enchanted
     * @param enchantmentData
     *            the EnchantmentData to add
     */
    public static void addEnchantmentData(ItemStack itemStack, EnchantmentData enchantmentData)
    {
        final NBTTagList nbttaglist = getTag(itemStack);
        boolean flag = true;

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            final NBTTagCompound nbttagcompound = (NBTTagCompound) nbttaglist.tagAt(i);

            if (nbttagcompound.getShort("id") == enchantmentData.enchantmentobj.effectId)
            {
                if (nbttagcompound.getShort("lvl") != enchantmentData.enchantmentLevel)
                {
                    nbttagcompound.setShort("lvl", (short) enchantmentData.enchantmentLevel);
                }

                flag = false;
                break;
            }
        }

        if (flag)
        {
            final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setShort("id", (short) enchantmentData.enchantmentobj.effectId);
            nbttagcompound1.setShort("lvl", (short) enchantmentData.enchantmentLevel);
            nbttaglist.appendTag(nbttagcompound1);
        }

        if (!itemStack.hasTagCompound())
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        itemStack.getTagCompound().setTag("StoredEnchantments", nbttaglist);
    }

    /**
     * Gets stack compound of a enchanted book
     * 
     * @param par1ItemStack
     *            the book to check
     * @return the stack compound of the enchanted book
     */
    public static NBTTagList getTag(ItemStack par1ItemStack)
    {
        return par1ItemStack.stackTagCompound != null && par1ItemStack.stackTagCompound.hasKey("StoredEnchantments") ? (NBTTagList) par1ItemStack.stackTagCompound
                .getTag("StoredEnchantments") : new NBTTagList();
    }
}
