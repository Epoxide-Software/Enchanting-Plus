package net.darkhax.eplus.inventory;

import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemStackHandlerEnchant implements IItemHandler, IItemHandlerModifiable, INBTSerializable<NBTTagCompound> {

    public TileEntityAdvancedTable tile;
    protected NonNullList<ItemStack> stacks;

    public ItemStackHandlerEnchant (TileEntityAdvancedTable tile, int size) {

        this.tile = tile;
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public ItemStackHandlerEnchant (TileEntityAdvancedTable tile, NonNullList<ItemStack> stacks) {

        this.tile = tile;
        this.stacks = stacks;
    }

    public void setSize (int size) {

        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public void setStackInSlot (int slot, ItemStack stack) {

        this.validateSlotIndex(slot);
        this.stacks.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > 64) {
            stack.setCount(64);
        }
        this.onContentsChanged(slot);
    }

    @Override
    public int getSlots () {

        return this.stacks.size();
    }

    @Override
    public ItemStack getStackInSlot (int slot) {

        this.validateSlotIndex(slot);
        return this.stacks.get(slot);
    }

    @Override
    public ItemStack insertItem (int slot, ItemStack stack, boolean simulate) {

        // Prevents inserting air
        if (stack.isEmpty() || stack.getCount() == 0) {
            
            return ItemStack.EMPTY;
        }

        this.validateSlotIndex(slot);

        final ItemStack existing = this.stacks.get(slot);

        // If existing exists, prevent all new entries.
        if (!existing.isEmpty() || !PredicateEnchantableItem.INSTANCE.test(stack)) {
            
            return stack;
        }

        // Inserts new item
        if (!simulate) {
            
            this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(stack, this.getSlotLimit(slot)));
            this.onContentsChanged(slot);
        }

        // Returns the decreased item
        return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - this.getSlotLimit(0));
    }

    @Override
    public ItemStack extractItem (int slot, int amount, boolean simulate) {

        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        this.validateSlotIndex(slot);

        final ItemStack existing = this.stacks.get(slot);

        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        final int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.stacks.set(slot, ItemStack.EMPTY);
                this.onContentsChanged(slot);
            }
            return existing;
        }
        else {
            if (!simulate) {
                this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                this.onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit (int slot) {

        return 1;
    }

    @Override
    public NBTTagCompound serializeNBT () {

        final NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < this.stacks.size(); i++) {
            if (!this.stacks.get(i).isEmpty()) {
                final NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setInteger("Slot", i);
                this.stacks.get(i).writeToNBT(itemTag);
                nbtTagList.appendTag(itemTag);
            }
        }
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Items", nbtTagList);
        nbt.setInteger("Size", this.stacks.size());
        return nbt;
    }

    @Override
    public void deserializeNBT (NBTTagCompound nbt) {

        this.setSize(nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : this.stacks.size());
        final NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++) {
            final NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            final int slot = itemTags.getInteger("Slot");

            if (slot >= 0 && slot < this.stacks.size()) {
                this.stacks.set(slot, new ItemStack(itemTags));
            }
        }
        this.onLoad();
    }

    protected void validateSlotIndex (int slot) {

        if (slot < 0 || slot >= this.stacks.size()) {
            // TODO add a warning through logger
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.stacks.size() + ")");
        }
    }

    protected void onLoad () {

    }

    protected void onContentsChanged (int slot) {

        if (slot == 0) {
            this.tile.updateItem();
        }
        if (!this.tile.getWorld().isRemote) {
            this.tile.markDirty();
        }
    }
}
