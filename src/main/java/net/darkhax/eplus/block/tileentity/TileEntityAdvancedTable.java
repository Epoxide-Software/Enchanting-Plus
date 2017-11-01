package net.darkhax.eplus.block.tileentity;

import net.darkhax.bookshelf.util.EnchantmentUtils;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.common.network.packet.n.PacketTableSync;
import net.darkhax.eplus.inventory.ItemStackHandlerEnchant;
import net.darkhax.eplus.libs.EnchantData;
import net.minecraft.block.BlockAnvil;
import net.minecraft.command.CommandEnchant;
import net.minecraft.enchantment.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.*;

import javax.annotation.Nullable;
import java.util.*;

public class TileEntityAdvancedTable extends TileEntityWithBook implements IInteractionObject {
    
    public ItemStackHandlerEnchant inventory;
    
    public TileEntityAdvancedTable() {
        this.inventory = new ItemStackHandlerEnchant(this, 1);
    }
    
    public List<Enchantment> enchantmentsValid = new ArrayList<>();
    
    public List<EnchantmentData> enchantmentsCurrent = new ArrayList<>();
    
    public List<EnchantmentData> enchantmentsNew = new ArrayList<>();
    
    public void updateItem() {
        ItemStack stack = inventory.getStackInSlot(0);
        enchantmentsValid.clear();
        enchantmentsCurrent.clear();
        enchantmentsNew.clear();
        if(!stack.isEmpty()) {
            if(stack.isItemEnchantable() || stack.isItemEnchanted()) {
                for(Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet()) {
                    enchantmentsCurrent.add(new EnchantData(entry.getKey(), entry.getValue()));
                }
                enchantmentsValid.addAll(getEnchantmentsForItem(stack));
                
            }
        }
        if(!world.isRemote) {
            EnchantingPlus.NETWORK.sendToAllAround(new PacketTableSync(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 128D));
            markDirty();
        }
    }
    
    @Override
    public void markDirty() {
        super.markDirty();
    }
    
    private List<Enchantment> getEnchantmentsForItem(ItemStack stack) {
        List<Enchantment> enchList = new ArrayList<>();
        for(Enchantment enchantment : Enchantment.REGISTRY) {
            
            if(stack.isItemEnchanted()) {
                if(enchantment.canApply(stack)) {
                    enchList.add(enchantment);
                }
            } else {
                if(enchantment.type.canEnchantItem(stack.getItem()))
                    enchList.add(enchantment);
            }
        }
        return enchList;
    }
    
    public int getCurrentLevelForEnchant(Enchantment enchant) {
        for(EnchantmentData data : enchantmentsCurrent) {
            if(data.enchantment.getRegistryName().equals(enchant.getRegistryName())) {
                return data.enchantmentLevel;
            }
        }
        return 0;
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
        dataTag.setTag("inventory", inventory.serializeNBT());
    }
    
    @Override
    public void readNBT(NBTTagCompound dataTag) {
        inventory.deserializeNBT((NBTTagCompound) dataTag.getTag("inventory"));
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        
        return getCapability(capability, facing) != null || super.hasCapability(capability, facing);
    }
    
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) inventory;
        }
        return super.getCapability(capability, facing);
    }
}