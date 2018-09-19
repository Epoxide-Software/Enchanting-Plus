package net.darkhax.eplus.block.tileentity;

import javax.annotation.Nullable;

import net.darkhax.eplus.inventory.ItemStackHandlerEnchant;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityAdvancedTable extends TileEntityWithBook {

    private final ItemStackHandlerEnchant inventory;

    public TileEntityAdvancedTable () {

        this.inventory = new ItemStackHandlerEnchant(1);
    }

    public ItemStackHandlerEnchant getInventory () {

        return this.inventory;
    }

    @Override
    public void writeNBT (NBTTagCompound dataTag) {

        dataTag.setTag("inventory", this.inventory.serializeNBT());
    }

    @Override
    public void readNBT (NBTTagCompound dataTag) {

        this.inventory.deserializeNBT((NBTTagCompound) dataTag.getTag("inventory"));
    }

    @Override
    public boolean hasCapability (Capability<?> capability, @Nullable EnumFacing facing) {

        return this.getCapability(capability, facing) != null || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability (Capability<T> capability, @Nullable EnumFacing facing) {

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.inventory;
        }
        return super.getCapability(capability, facing);
    }
}