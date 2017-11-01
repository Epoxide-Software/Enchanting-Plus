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
    private final PredicateEnchantableItem predicate = new PredicateEnchantableItem();;

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

        if (stack.isEmpty() || stack.getCount() == 0) {
            return ItemStack.EMPTY;
        }

        this.validateSlotIndex(slot);

        final ItemStack existing = this.stacks.get(slot);

        int limit = this.getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                return stack;
            }

            limit -= existing.getCount();
        }

        if (slot == 0 && !this.predicate.test(stack)) {
            return stack;
        }
        if (limit <= 0) {
            return stack;
        }

        final boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            this.onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
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

        return 64;
    }

    protected int getStackLimit (int slot, ItemStack stack) {

        return stack.getMaxStackSize();
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
