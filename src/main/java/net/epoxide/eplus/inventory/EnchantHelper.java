package net.epoxide.eplus.inventory;

import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import net.darkhax.bookshelf.handler.BookshelfHooks;

import net.epoxide.eplus.common.PlayerProperties;
import net.epoxide.eplus.handler.EPlusConfigurationHandler;

public class EnchantHelper {
    
    public static boolean isEnchantmentValid (Enchantment ench, EntityPlayer entityPlayer) {
        
        return ench != null && ((EPlusConfigurationHandler.useQuestMode && PlayerProperties.getProperties(entityPlayer).unlockedEnchantments.contains(ench.effectId)) || entityPlayer.capabilities.isCreativeMode);
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
     * @param enchantmentData : The Enchantment Data that is set onto the itemstack
     * @param itemStack : The itemstack currently in the enchantment table
     * @param player : The current player that hit the button to enchant the item the
     *            enchantment table
     * @param cost
     * @return
     */
    public static ItemStack setEnchantments (List<EnchantmentData> enchantmentData, ItemStack itemStack, EntityPlayer player, int cost) {
        
        if (hasRestriction(itemStack) && !restrictionMatches(itemStack, player))
            return itemStack;
            
        enchantmentData = BookshelfHooks.onItemEnchanted(player, itemStack, cost, enchantmentData);
        
        NBTTagList nbttaglist = new NBTTagList();
        for (final EnchantmentData data : enchantmentData) {
            final NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setInteger("id", data.enchantmentobj.effectId);
            nbttagcompound.setInteger("lvl", data.enchantmentLevel);
            nbttaglist.appendTag(nbttagcompound);
        }
        
        if (itemStack.getItem() == Items.book)
            itemStack = new ItemStack(Items.enchanted_book);
            
        if (itemStack.getItem() == Items.enchanted_book) {
            
            if (nbttaglist.tagCount() > 0) {
                itemStack.setTagInfo("StoredEnchantments", nbttaglist);
            }
            else if (itemStack.hasTagCompound()) {
                itemStack.getTagCompound().removeTag("StoredEnchantments");
                itemStack.setTagCompound(new NBTTagCompound());
                itemStack = new ItemStack(Items.book);
            }
        }
        else if (nbttaglist.tagCount() > 0) {
            itemStack.setTagInfo("ench", nbttaglist);
        }
        else if (itemStack.hasTagCompound()) {
            itemStack.getTagCompound().removeTag("ench");
            itemStack.getTagCompound().removeTag("enchantedOwnerUUID");
            
        }
        return itemStack;
    }
    
    public static boolean hasRestriction (ItemStack itemStack) {
        
        if (itemStack.hasTagCompound()) {
            String enchantedOwner = itemStack.getTagCompound().getString("enchantedOwnerUUID");
            return !enchantedOwner.equals("");
        }
        return false;
    }
    
    public static boolean restrictionMatches (ItemStack itemStack, EntityPlayer player) {
        
        String enchantedOwner = itemStack.getTagCompound().getString("enchantedOwnerUUID");
        return player.getUniqueID().toString().equals(enchantedOwner);
    }
    
    /**
     * Calculates the amount of levels that an enchantment should cost. This factors in the
     * enchantability of the enchantment, the level of the enchantment, the enchantability of
     * the Item, and the enchantment factor from the config. If the ItemStack passed already
     * has the enchantment on it, the cost will be adjusted to an upgrade price.
     *
     * @param enchant: The enchantment being applied.
     * @param level: The level of the enchantment being applied.
     * @param stack: The ItemStack being enchanted.
     * @return int: The amount of experience levels that should be charged for the enchantment.
     */
    public static int calculateEnchantmentCost (Enchantment enchant, int level, ItemStack stack) {
        
        int existingLevel = EnchantmentHelper.getEnchantmentLevel(enchant.effectId, stack);
        int enchantability = enchant.getMaxEnchantability(level);
        
        if (existingLevel > 0 && existingLevel != level)
            enchantability -= enchant.getMaxEnchantability(existingLevel);
            
        return (int) (((enchantability - stack.getItem().getItemEnchantability(stack)) / 2) * EPlusConfigurationHandler.costFactor);
    }
    
    public static int calculateLevelsFromExp (int exp) {
        
        int level = 0;
        float cap = (level >= 30 ? 62 + (level - 30) * 7 : (level >= 15 ? 17 + (level - 15) * 3 : 17));
        while (exp >= cap) {
            exp -= cap;
            level += 1;
            cap = (level >= 30 ? 62 + (level - 30) * 7 : (level >= 15 ? 17 + (level - 15) * 3 : 17));
        }
        return level;
    }
}
