package net.epoxide.eplus.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.darkhax.bookshelf.lib.util.Utilities;
import net.epoxide.eplus.handler.ContentHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemEnchantedScroll extends Item {
    
    public ItemEnchantedScroll() {
        
        this.setUnlocalizedName("eplus.scroll");
    }
    
    /**
     * Creates a new ItemStack scroll, with the passed Enchantment written to it.
     * 
     * @param ench: The Enchantment to write to the scroll.
     * @return ItemStack: A new ItemStack containing all of the information to be considered a
     *         valid scroll.
     */
    public static ItemStack createScroll (Enchantment ench) {
        
        ItemStack stack = new ItemStack(ContentHandler.scroll);
        ItemStackUtils.prepareDataTag(stack);
        stack.getTagCompound().setInteger("ScrollEnchantment", ench.effectId);
        return stack;
    }
    
    /**
     * Reads an Enchantment from a scroll. If the scroll does not have a valid enchantment,
     * null will be returned.
     * 
     * @param stack: The ItemStack to read from.
     * @return Enchantment: The Enchantment that is stored on the scroll. If no Enchantment was
     *         written, or the scroll is invalid, null will be returned.
     */
    public static Enchantment readScroll (ItemStack stack) {
        
        return (isValidScroll(stack) ? Utilities.getEnchantment(stack.getTagCompound().getInteger("ScrollEnchantment")) : null);
    }
    
    /**
     * Checks to see if an ItemStack is a valid scroll. A valid scroll is one which is not
     * null, represents an ItemEnchantedScroll, and has a ScrollEnchantment tag.
     * 
     * @param stack: The ItemStack to check.
     * @return boolean: Whether or not the ItemStack passed is considered a valid scroll.
     */
    public static boolean isValidScroll (ItemStack stack) {
        
        ItemStackUtils.prepareDataTag(stack);
        return (ItemStackUtils.isValidStack(stack) && stack.getItem() instanceof ItemEnchantedScroll && stack.getTagCompound().hasKey("ScrollEnchantment"));
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack (ItemStack stack, int pass) {
        
        return (isValidScroll(stack)) ? ContentHandler.getEnchantmentColor(readScroll(stack).type.name()) : super.getColorFromItemStack(stack, pass);
    }
}