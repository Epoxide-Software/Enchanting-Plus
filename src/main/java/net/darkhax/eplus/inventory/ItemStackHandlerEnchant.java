package net.darkhax.eplus.inventory;

import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.*;
import net.minecraftforge.items.*;

import javax.annotation.Nonnull;

public class ItemStackHandlerEnchant implements IItemHandler, IItemHandlerModifiable, INBTSerializable<NBTTagCompound> {
    
    public TileEntityAdvancedTable tile;
    protected NonNullList<ItemStack> stacks;
    private final PredicateEnchantableItem predicate=  new PredicateEnchantableItem();;
    
    public ItemStackHandlerEnchant(TileEntityAdvancedTable tile, int size) {
        this.tile = tile;
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }
    
    public ItemStackHandlerEnchant(TileEntityAdvancedTable tile, NonNullList<ItemStack> stacks) {
        this.tile = tile;
        this.stacks = stacks;
    }
    
    public void setSize(int size) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }
    
    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        validateSlotIndex(slot);
        stacks.set(slot, stack);
        if(!stack.isEmpty() && stack.getCount() > 64) {
            stack.setCount(64);
        }
        onContentsChanged(slot);
    }
    
    @Override
    public int getSlots() {
        return stacks.size();
    }
    
    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return this.stacks.get(slot);
    }
    
    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if(stack.isEmpty() || stack.getCount() == 0)
            return ItemStack.EMPTY;
        
        validateSlotIndex(slot);
        
        ItemStack existing = this.stacks.get(slot);
        
        int limit = getStackLimit(slot, stack);
        
        if(!existing.isEmpty()) {
            if(!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;
            
            limit -= existing.getCount();
        }
        
        if(slot == 0 && !predicate.test(stack)) {
            return stack;
        }
        if(limit <= 0)
            return stack;
        
        boolean reachedLimit = stack.getCount() > limit;
        
        if(!simulate) {
            if(existing.isEmpty()) {
                this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }
        
        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }
    
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(amount == 0)
            return ItemStack.EMPTY;
        
        validateSlotIndex(slot);
        
        ItemStack existing = this.stacks.get(slot);
        
        if(existing.isEmpty())
            return ItemStack.EMPTY;
        
        int toExtract = Math.min(amount, existing.getMaxStackSize());
        
        if(existing.getCount() <= toExtract) {
            if(!simulate) {
                this.stacks.set(slot, ItemStack.EMPTY);
                onContentsChanged(slot);
            }
            return existing;
        } else {
            if(!simulate) {
                this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onContentsChanged(slot);
            }
            
            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }
    
    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }
    
    protected int getStackLimit(int slot, ItemStack stack) {
        return stack.getMaxStackSize();
    }
    
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagList nbtTagList = new NBTTagList();
        for(int i = 0; i < stacks.size(); i++) {
            if(!stacks.get(i).isEmpty()) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setInteger("Slot", i);
                stacks.get(i).writeToNBT(itemTag);
                nbtTagList.appendTag(itemTag);
            }
        }
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Items", nbtTagList);
        nbt.setInteger("Size", stacks.size());
        return nbt;
    }
    
    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        setSize(nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : stacks.size());
        NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");
            
            if(slot >= 0 && slot < stacks.size()) {
                stacks.set(slot, new ItemStack(itemTags));
            }
        }
        onLoad();
    }
    
    protected void validateSlotIndex(int slot) {
        if(slot < 0 || slot >= stacks.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
    }
    
    protected void onLoad() {
    
    }
    
    protected void onContentsChanged(int slot) {
        if(slot == 0) {
            tile.updateItem();
        }
        if(!tile.getWorld().isRemote)
            tile.markDirty();
    }
}
