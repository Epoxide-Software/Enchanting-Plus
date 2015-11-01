package net.epoxide.eplus.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;

public class EnchantHelper {

    public static boolean isItemEnchanted (ItemStack itemStack) {

        return itemStack.hasTagCompound() && (itemStack.getItem() != Items.enchanted_book ? itemStack.stackTagCompound.hasKey("ench") : itemStack.stackTagCompound.hasKey("StoredEnchantments"));
    }

    public static boolean isNewItemEnchantable (Item item) {

        if (item.equals(Items.enchanted_book)) {
            return isItemEnchantable(new ItemStack(Items.book));
        }
        return isItemEnchantable(new ItemStack(item));
    }

    public static boolean isItemEnchantable (ItemStack itemStack) {

        boolean flag = true;
        if (itemStack.hasTagCompound())
            flag = !itemStack.getTagCompound().hasKey("charge");

        return itemStack.getItem().getItemEnchantability(itemStack) > 0 && (itemStack.getItem() == Items.book || itemStack.isItemEnchantable() && flag);
    }

    public static ItemStack setEnchantments (HashMap<Integer, Integer> map, ItemStack itemStack, HashMap<Integer, Integer> levels, EntityPlayer player) {

        final NBTTagList nbttaglist = new NBTTagList();

        NBTTagList restrictions;

        if (itemStack.hasTagCompound())
            restrictions = itemStack.getTagCompound().getTagList("restrictions", 10);
        else
            restrictions = new NBTTagList();

        for (final Object o : map.keySet()) {
            final int i = (Integer) o;
            final NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setShort("id", (short) i);
            nbttagcompound.setShort("lvl", (short) map.get(i).intValue());
            nbttaglist.appendTag(nbttagcompound);

            int startLevel = map.get(i);
            try {
                startLevel = levels.get(i);
            } catch (NullPointerException e) {

            }

            for (int y = startLevel; y <= map.get(i); y++) {
                if (containsKey(restrictions, i, y)) {
                    continue;
                }

                NBTTagCompound compound = new NBTTagCompound();
                compound.setShort("id", (short) i);
                compound.setShort("lvl", (short) y);
                compound.setString("player", player.getDisplayName());
                restrictions.appendTag(compound);

            }
        }

        if (itemStack.getItem() == Items.book) {
            itemStack = new ItemStack(Items.enchanted_book);
        }

        if (nbttaglist.tagCount() > 0) {
            if (itemStack.getItem() != Items.enchanted_book) {
                itemStack.setTagInfo("ench", nbttaglist);
            }
            else {
                itemStack.setTagInfo("StoredEnchantments", nbttaglist);
            }
            itemStack.setTagInfo("restrictions", restrictions);
        }
        else if (itemStack.hasTagCompound()) {
            if (itemStack.getItem() != Items.enchanted_book) {
                itemStack.getTagCompound().removeTag("ench");
            }
            else {
                itemStack.getTagCompound().removeTag("StoredEnchantments");
                itemStack.stackTagCompound = null;
                itemStack = new ItemStack(Items.book);
            }
        }
        return itemStack;
    }

    public static boolean containsKey (NBTTagList restrictions, int id, int y) {

        for (int k = 0; k < restrictions.tagCount(); k++) {
            NBTTagCompound tag = restrictions.getCompoundTagAt(k);
            if (tag.getShort("lvl") == y && tag.getShort("id") == id) {
                return true;
            }
        }
        return false;
    }
}
