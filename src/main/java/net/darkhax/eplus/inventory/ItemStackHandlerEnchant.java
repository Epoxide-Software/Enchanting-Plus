package net.darkhax.eplus.inventory;

import net.darkhax.eplus.EnchantingPlus;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackHandlerEnchant extends ItemStackHandler {

    protected NonNullList<ItemStack> stacks;
    protected TileEntity tableTile;

    public ItemStackHandlerEnchant (TileEntity tableTile) {

        this.stacks = NonNullList.withSize(1, ItemStack.EMPTY);
        this.tableTile = tableTile;
    }

    public ItemStack getEnchantingStack () {

        return this.getStackInSlot(0);
    }

    @Override
    public ItemStack insertItem (int slot, ItemStack stack, boolean simulate) {

        if (!EnchantingPlus.TEST_ENCHANTABILITY.test(stack)) {
            return stack;
        }
        
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public int getSlotLimit (int slot) {

        return 1;
    }
    
    @Override
    protected void onContentsChanged(int slot) {

        tableTile.markDirty();
    }
}
