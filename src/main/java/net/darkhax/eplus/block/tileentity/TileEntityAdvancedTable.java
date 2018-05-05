package net.darkhax.eplus.block.tileentity;

import net.darkhax.bookshelf.lib.EnchantData;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.inventory.ItemStackHandlerEnchant;
import net.darkhax.eplus.network.messages.*;
import net.darkhax.eplus.network.packet.PacketTableSync;
import net.minecraft.enchantment.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.*;

public class TileEntityAdvancedTable extends TileEntityWithBook implements IInteractionObject {
    
    public ItemStackHandlerEnchant inventory;
    
    public TileEntityAdvancedTable() {
        
        this.inventory = new ItemStackHandlerEnchant(this, 1);
    }
    
    /**
     * Valid enchantments for the item (not applied to item already)
     */
    public List<Enchantment> validEnchantments = new ArrayList<>();
    
    /**
     * Current enchantments on the item (already applied to item) and ones that will be applied
     */
    public List<EnchantData> existingEnchantments = new ArrayList<>();
    
    /**
     * Last update of the existingEnchantments list, used to see which enchantments are new
     */
    public List<EnchantData> existingEnchantmentsCache = new ArrayList<>();
    
    
    /**
     * map containing the position of the bookshelves around the table and their enchantability (since it can be more than 1)
     */
    public Map<BlockPos, Float> bookshelves = new HashMap<>();
    
    /**
     * tells the gui to update. Client side only
     */
    public boolean updateGui = false;
    
    public void updateItem() {
        
        final ItemStack stack = this.inventory.getStackInSlot(0);
        this.validEnchantments.clear();
        this.existingEnchantments.clear();
        this.existingEnchantmentsCache.clear();
        
        if(!stack.isEmpty()) {
            if(stack.isItemEnchantable() || stack.isItemEnchanted()) {
                for(final Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
                    this.existingEnchantments.add(new EnchantData(entry.getKey(), entry.getValue()));
                    this.existingEnchantmentsCache.add(new EnchantData(entry.getKey(), entry.getValue()));
                }
                this.validEnchantments.addAll(this.getEnchantmentsForItem(stack));
                
            }
        }
        if(!this.world.isRemote) {
            EnchantingPlus.NETWORK.sendToAllAround(new PacketTableSync(this), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), 128D));
            this.markDirty();
        }
    }
    
    private List<Enchantment> getEnchantmentsForItem(ItemStack stack) {
        
        final List<Enchantment> enchList = new ArrayList<>();
        
        for(final Enchantment enchantment : Enchantment.REGISTRY) {
            
            if(stack.isItemEnchanted()) {
                
                if(enchantment.canApply(stack) && !enchantment.isCurse() && !enchantment.isTreasureEnchantment()) {
                    
                    enchList.add(enchantment);
                }
            } else {
                
                if(enchantment.type.canEnchantItem(stack.getItem()) && !enchantment.isCurse() && !enchantment.isTreasureEnchantment()) {
                    
                    enchList.add(enchantment);
                }
            }
        }
        return enchList;
    }
    
    public int getCurrentLevelForEnchant(Enchantment enchant) {
        
        for(final EnchantData data : this.existingEnchantments) {
            if(data.enchantment.getRegistryName().equals(enchant.getRegistryName())) {
                return data.enchantmentLevel;
            }
        }
        return 0;
    }
    
    
    public void enchant() {
        //TODO remove enchantments from the item first.
        ItemStack stack = inventory.getStackInSlot(0);
        EnchantmentHelper.setEnchantments(new HashMap<>(), stack);
        //
        //        TODO apply new enchantments to it.
        for(EnchantData data : existingEnchantments) {
            //TODO the current code will recalculate costs for preexisting enchantments, this needs to change, maybe a list made in updateItem?
            stack.addEnchantment(data.enchantment, data.enchantmentLevel);
        }
        if(!world.isRemote) {
            EnchantingPlus.NETWORK.sendToAllAround(new MessageEnchantSync(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
            markDirty();
            updateItem();
        }
    }
    
    public int getEnchantmentCosts() {
        int cost = 0;
        return cost;
    }
    
    
    public static int calculateNewEnchCost(Enchantment enchantment, int level) {
        
        // Base cost is equal to roughly 2.5 levels of EXP.
        int cost = 25;
        
        // Cost is multiplied up to 10, based on rarity of the enchant.
        // Rarer the enchant, higher the cost.
        cost *= Math.max(11 - enchantment.getRarity().getWeight(), 1);
        
        // Linear cost increase based on level.
        cost *= level;
        
        // The cost factor is applied. Default is 1.5.
        // TODO make configurable
        cost *= 1.5f;
        
        // Curses cost even more to apply
        if(enchantment.isCurse()) {
            
            // TODO make configurable
            cost *= 1.5f;
        }
        
        // Treasures cost more to apply
        if(enchantment.isTreasureEnchantment()) {
            
            // TODO make configurable
            cost *= 1.5f;
        }
        
        return cost;
    }
    
    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        
        return new ContainerEnchantment(playerInventory, this.world, this.pos);
    }
    
    @Override
    public ITextComponent getDisplayName() {
        
        return new TextComponentString(this.getName());
    }
    
    @Override
    public String getGuiID() {
        
        return "eplus:enchanting_table";
    }
    
    @Override
    public String getName() {
        
        return "container.enchant";
    }
    
    @Override
    public boolean hasCustomName() {
        
        return false;
    }
    
    @Override
    public void writeNBT(NBTTagCompound dataTag) {
        
        dataTag.setTag("inventory", this.inventory.serializeNBT());
    }
    
    @Override
    public void readNBT(NBTTagCompound dataTag) {
        
        this.inventory.deserializeNBT((NBTTagCompound) dataTag.getTag("inventory"));
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        
        return this.getCapability(capability, facing) != null || super.hasCapability(capability, facing);
    }
    
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.inventory;
        }
        return super.getCapability(capability, facing);
    }
    
    
}