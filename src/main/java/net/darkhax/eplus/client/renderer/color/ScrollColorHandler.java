package net.darkhax.eplus.client.renderer.color;

import net.darkhax.eplus.item.ItemScroll;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public class ScrollColorHandler implements IItemColor {
    
    @Override
    public int getColorFromItemstack (ItemStack stack, int renderPass) {
        
        if (renderPass == 1 && ItemScroll.isValidScroll(stack)) {
            
            final Enchantment enchant = ItemScroll.readScroll(stack);
            
            if (enchant != null)
                return enchant.getRegistryName().hashCode();
        }
        
        return 16777215;
    }
}
