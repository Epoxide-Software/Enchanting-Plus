package net.darkhax.eplus.inventory;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotEnchant extends Slot {
    
    public SlotEnchant(ContainerAdvancedTable container, IInventory tableInventory, int slotIndex, int xPos, int yPos) {
        
        super(tableInventory, slotIndex, xPos, yPos);
    }
    
    @Override
    public int getSlotStackLimit () {
        
        return 1;
    }
    
    @Override
    public boolean isItemValid (ItemStack stack) {
        
        return stack.isItemEnchantable() || stack.isItemEnchanted() || stack.getItem() == Items.BOOK || stack.getItem() == Items.ENCHANTED_BOOK;
    }
}