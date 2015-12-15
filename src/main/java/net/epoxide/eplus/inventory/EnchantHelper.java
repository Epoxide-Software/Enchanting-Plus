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
import net.darkhax.bookshelf.lib.util.EnchantmentUtils;

import net.epoxide.eplus.common.PlayerProperties;
import net.epoxide.eplus.handler.ConfigurationHandler;

public class EnchantHelper {
    
    public static boolean isEnchantmentValid (Enchantment ench, EntityPlayer entityPlayer) {
        
        return ench != null && ((ConfigurationHandler.useQuestMode ? PlayerProperties.getProperties(entityPlayer).unlockedEnchantments.contains(ench.effectId) : true) || entityPlayer.capabilities.isCreativeMode);
    }
    
    public static boolean isNewItemEnchantable (Item item) {
        
        if (item.equals(Items.enchanted_book)) {
            return EnchantmentUtils.isItemEnchantable(new ItemStack(Items.book));
        }
        return EnchantmentUtils.isItemEnchantable(new ItemStack(item));
    }
    
    /**
     * Updates the enchantments of an ItemStack.
     * 
     * @param enchantmentData: A List of EnchantmentData being set to the ItemStack.
     * @param itemStack: The ItemStack being updated.
     * @param player: The player doing the enchanting.
     * @param cost: The cost of the enchanting.
     * @return ItemStack: The enchanted ItemStack.
     */
    public static ItemStack updateEnchantments (List<EnchantmentData> enchantmentData, ItemStack itemStack, EntityPlayer player, int cost) {
        
        if (hasRestriction(itemStack) && !isValidOwner(itemStack, player))
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
    
    /**
     * Checks to see if an ItemStack has a restriction set on it. A restriction is classified
     * as a populated enchantedOwnerUUID tag.
     * 
     * @param itemStack: The ItemStack to check.
     * @return boolean: Whether or not the passed ItemStack has a restriction on it.
     */
    public static boolean hasRestriction (ItemStack itemStack) {
        
        if (itemStack.hasTagCompound()) {
            String enchantedOwner = itemStack.getTagCompound().getString("enchantedOwnerUUID");
            return !enchantedOwner.equals("");
        }
        return false;
    }
    
    /**
     * Checks to see if a player is the valid owner of an ItemStack.
     * 
     * @param itemStack: The ItemStack to check against.
     * @param player: The player to check.
     * @return boolean: Whether or not the player passed is a valid owner for the ItemStack.
     */
    public static boolean isValidOwner (ItemStack itemStack, EntityPlayer player) {
        
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
            
        return (int) (((enchantability - stack.getItem().getItemEnchantability(stack)) / 2) * ConfigurationHandler.costFactor);
    }
}
