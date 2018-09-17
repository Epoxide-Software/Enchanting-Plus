package net.darkhax.eplus.block.tileentity;

import javax.annotation.Nullable;

import net.darkhax.eplus.inventory.ItemStackHandlerEnchant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileEntityAdvancedTable extends TileEntityWithBook {

    private final ItemStackHandlerEnchant inventory;

    private EnchantmentLogicController controller;

    public TileEntityAdvancedTable () {

        this.inventory = new ItemStackHandlerEnchant(this, 1);
        this.controller = new EnchantmentLogicController(this);
    }

    public EnchantmentLogicController getLogic () {

        return this.controller;
    }

    public ItemStack getItem () {

        return this.inventory.getStackInSlot(0);
    }

    public IItemHandler getInventory () {

        return this.inventory;
    }

    @Override
    public ITextComponent getDisplayName () {

        return new TextComponentString("container.enchant");
    }

    @Override
    public void writeNBT (NBTTagCompound dataTag) {

        dataTag.setTag("inventory", this.inventory.serializeNBT());
        dataTag.setTag("logic", this.getLogic().writeToTag());
    }

    @Override
    public void readNBT (NBTTagCompound dataTag) {

        this.inventory.deserializeNBT((NBTTagCompound) dataTag.getTag("inventory"));
        this.controller = new EnchantmentLogicController(this, dataTag.getCompoundTag("logic"));
    }

    @Override
    public boolean hasCapability (Capability<?> capability, @Nullable EnumFacing facing) {

        return this.getCapability(capability, facing) != null || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability (Capability<T> capability, @Nullable EnumFacing facing) {

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.inventory;
        }
        return super.getCapability(capability, facing);
    }
}