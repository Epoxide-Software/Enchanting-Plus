package net.epoxide.eplus.inventory;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotEnchant extends Slot {
    final ContainerEnchantTable container;

    public SlotEnchant (ContainerEnchantTable container, IInventory tableInventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {

        super(tableInventory, slotIndex, xDisplayPosition, yDisplayPosition);
        this.container = container;
    }

    @Override
    public int getSlotStackLimit () {

        return 1;
    }

    @Override
    public boolean isItemValid (ItemStack par1ItemStack) {

        return par1ItemStack.isItemEnchantable() || par1ItemStack.isItemEnchanted() || par1ItemStack.getItem() == Items.book || par1ItemStack.getItem() == Items.enchanted_book;
    }
}
