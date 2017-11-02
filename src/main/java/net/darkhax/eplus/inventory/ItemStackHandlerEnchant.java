package net.darkhax.eplus.inventory;

import net.darkhax.bookshelf.util.InventoryUtils;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
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

        this.validateSlotIndex(slot);

        final ItemStack existing = this.stacks.get(slot);

        if (amount <= 0 || existing.isEmpty()) {

            return ItemStack.EMPTY;
        }

        final int toExtract = Math.min(amount, existing.getMaxStackSize());
        final boolean isCompletelyEmpty = existing.getCount() <= toExtract;

        if (!simulate) {

            this.stacks.set(slot, isCompletelyEmpty ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
            this.onContentsChanged(slot);
        }

        return isCompletelyEmpty ? existing : ItemHandlerHelper.copyStackWithSize(existing, toExtract);
    }

    @Override
    public int getSlotLimit (int slot) {

        return 1;
    }

    @Override
    public NBTTagCompound serializeNBT () {

        return InventoryUtils.writeInventory(this.stacks);
    }

    @Override
    public void deserializeNBT (NBTTagCompound tag) {

        this.setSize(((NBTTagList) tag.getTag("Items")).tagCount());
        this.stacks = InventoryUtils.readInventory(tag);

        this.onLoad();
    }

    protected void validateSlotIndex (int slot) {

        if (slot < 0 || slot >= this.stacks.size()) {

            EnchantingPlus.LOG.warn("Attempted to access invalid slot {}. Valid range: 0 - {}", slot, this.stacks.size());
            throw new IllegalArgumentException("Slot " + slot + " not in valid range - [0," + this.stacks.size() + ")");
        }
    }

    protected void onLoad () {

    }

    protected void onContentsChanged (int slot) {

        if (slot == 0) {

            this.tile.updateItem();
        }
    }
}
