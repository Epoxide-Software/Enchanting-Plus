package net.epoxide.eplus.inventory;

import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.epoxide.eplus.common.PlayerProperties;
import net.epoxide.eplus.handler.EPlusConfigurationHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;

public class EnchantHelper {

    public static boolean isEnchantmentValid (Enchantment ench, EntityPlayer entityPlayer) {

        return ench != null && EPlusConfigurationHandler.useQuestMode && (PlayerProperties.getProperties(entityPlayer).unlockedEnchantments.contains(ench.effectId) || entityPlayer.capabilities.isCreativeMode);
    }

    public static boolean isEnchantmentsCompatible (Enchantment ench1, Enchantment ench2) {

        return ench1.canApplyTogether(ench2) && ench2.canApplyTogether(ench1);
    }

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

        boolean flag = !itemStack.hasTagCompound() || !itemStack.getTagCompound().hasKey("charge");

        return itemStack.getItem().getItemEnchantability(itemStack) > 0 && (itemStack.getItem() == Items.book || itemStack.getItem() == Items.enchanted_book || itemStack.isItemEnchantable() && flag);
    }

    /**
     * @param map:       The enchantments that should be applied to the item
     * @param itemStack: The itemstack currently in the enchantment table
     * @param levels:    The current enchantments on the itemstack
     * @param player:    The current player that hit the button to enchant the item the enchantment table
     * @return
     */
    public static ItemStack setEnchantments (HashMap<Integer, Integer> map, ItemStack itemStack, HashMap<Integer, Integer> levels, EntityPlayer player) {

        NBTTagList restrictions = itemStack.hasTagCompound() ? itemStack.getTagCompound().getTagList("restrictions", 10) : new NBTTagList();

        NBTTagList nbttaglist = new NBTTagList();
        for (final int i : map.keySet()) {
            final NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setShort("id", (short) i);
            nbttagcompound.setShort("lvl", map.get(i).shortValue());
            nbttaglist.appendTag(nbttagcompound);

            int startLevel = levels.get(i) == null ? map.get(i) : levels.get(i);

            for (int y = startLevel; y <= map.get(i); y++) {
                if (hasRestriction(restrictions, i, y)) {
                    continue;
                }

                NBTTagCompound compound = new NBTTagCompound();
                compound.setShort("id", (short) i);
                compound.setShort("lvl", (short) y);
                compound.setString("player", player.getDisplayName());
                restrictions.appendTag(compound);
            }
        }

        if (itemStack.getItem() == Items.book)
            itemStack = new ItemStack(Items.enchanted_book);


        if (itemStack.getItem() == Items.enchanted_book) {

            if (nbttaglist.tagCount() > 0) {
                itemStack.setTagInfo("StoredEnchantments", nbttaglist);
                itemStack.setTagInfo("restrictions", restrictions);
            }
            else if (itemStack.hasTagCompound()) {
                itemStack.getTagCompound().removeTag("StoredEnchantments");
                itemStack.stackTagCompound = null;
                itemStack = new ItemStack(Items.book);
            }
        }
        else if (nbttaglist.tagCount() > 0) {
            itemStack.setTagInfo("ench", nbttaglist);
            itemStack.setTagInfo("restrictions", restrictions);
        }
        else if (itemStack.hasTagCompound()) {
            itemStack.getTagCompound().removeTag("ench");
        }
        return itemStack;
    }

    public static boolean hasRestriction (NBTTagList restrictions, int id, int y) {

        for (int k = 0; k < restrictions.tagCount(); k++) {
            NBTTagCompound tag = restrictions.getCompoundTagAt(k);
            if (tag.getShort("lvl") == y && tag.getShort("id") == id) {
                return true;
            }
        }
        return false;
    }
}
