package net.darkhax.eplus.client.renderer.color;

import java.awt.Color;

import net.darkhax.eplus.item.ItemScroll;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public class ScrollColorHandler implements IItemColor {
    
    @Override
    public int getColorFromItemstack (ItemStack stack, int renderPass) {
        
        if (renderPass == 1 && ItemScroll.isValidScroll(stack)) {
            
            final Enchantment enchant = ItemScroll.readScroll(stack);
            
            if (enchant != null) {
                
                final int hash = enchant.getRegistryName().hashCode();
                return new Color(hash >> 16 & 0xFF, hash >> 8 & 0xFF, hash & 0xFF).getRGB();
            }
        }
        
        return 16777215;
    }
}
