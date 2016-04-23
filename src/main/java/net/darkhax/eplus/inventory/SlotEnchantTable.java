package net.darkhax.eplus.inventory;

import net.minecraft.inventory.InventoryBasic;

public class SlotEnchantTable extends InventoryBasic {
    
    private final ContainerAdvancedTable container;
    
    public SlotEnchantTable(ContainerAdvancedTable container, String inventoryTitle, boolean customName, int slotsCount) {
        
        super(inventoryTitle, customName, slotsCount);
        this.container = container;
    }
    
    @Override
    public void markDirty () {
        
        super.markDirty();
        this.container.onCraftMatrixChanged(this);
    }
}