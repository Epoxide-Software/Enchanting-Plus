package net.darkhax.eplus.block.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;

public class TileEntityAdvancedTable extends TileEntityWithBook implements IInteractionObject {

    public ItemStack stack = ItemStack.EMPTY;

    @Override
    public Container createContainer (InventoryPlayer playerInventory, EntityPlayer playerIn) {

        return new ContainerEnchantment(playerInventory, this.world, this.pos);
    }

    @Override
    public ITextComponent getDisplayName () {

        return new TextComponentString(this.getName());
    }

    @Override
    public String getGuiID () {

        return "eplus:enchanting_table";
    }

    @Override
    public String getName () {

        return "container.enchant";
    }

    @Override
    public boolean hasCustomName () {

        return false;
    }

    @Override
    public void writeNBT (NBTTagCompound dataTag) {

        dataTag.setTag("HeldItem", this.stack.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readNBT (NBTTagCompound dataTag) {

        this.stack = new ItemStack(dataTag.getCompoundTag("HeldItem"));
    }
}