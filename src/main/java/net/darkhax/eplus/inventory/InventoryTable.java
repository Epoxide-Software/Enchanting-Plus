package net.darkhax.eplus.inventory;

import net.minecraft.inventory.InventoryBasic;

public class InventoryTable extends InventoryBasic {

    private final ContainerAdvancedTable container;

    public InventoryTable (ContainerAdvancedTable container, String inventoryTitle, boolean customName, int slotsCount) {

        super(inventoryTitle, customName, slotsCount);
        this.container = container;
    }

    @Override
    public void markDirty () {

        super.markDirty();
        this.container.onCraftMatrixChanged(this);
    }
}