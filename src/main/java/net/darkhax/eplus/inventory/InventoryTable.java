package net.darkhax.eplus.inventory;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public class InventoryTable extends InventoryBasic {

    private final ContainerAdvancedTable container;

    public InventoryTable (ContainerAdvancedTable container, String inventoryTitle, boolean customName, int slotsCount) {

        super(inventoryTitle, customName, slotsCount);
        this.container = container;
    }

    @Override
    public ItemStack removeStackFromSlot (int index) {

        this.container.table.stack = ItemStack.EMPTY;
        final ItemStack stack = super.removeStackFromSlot(index);
        this.markDirty();
        return stack;
    }

    @Override
    public void setInventorySlotContents (int index, ItemStack stack) {

        this.container.table.stack = stack;
        super.setInventorySlotContents(index, stack);
    }

    @Override
    public void markDirty () {

        super.markDirty();
        this.container.onCraftMatrixChanged(this);
        this.container.table.markDirty();
    }
}