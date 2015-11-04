package net.epoxide.eplus.inventory;

import net.minecraft.inventory.InventoryBasic;

public class SlotEnchantTable extends InventoryBasic {
    final ContainerEnchantTable container;
    
    public SlotEnchantTable(ContainerEnchantTable container, String inventoryTitle, boolean customName, int slotsCount) {
        
        super(inventoryTitle, customName, slotsCount);
        
        this.container = container;
    }
    
    @Override
    public void markDirty () {
        
        super.markDirty();
        container.onCraftMatrixChanged(this);
    }
}
