package net.darkhax.eplus.block.tileentity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.darkhax.eplus.inventory.ItemStackHandlerEnchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class TileEntityAdvancedTable extends TileEntityWithBook {

    private final Map<UUID, ItemStackHandlerEnchant> inventories = new HashMap<>();

    public ItemStackHandlerEnchant getInventory (EntityPlayer player) {

        ItemStackHandlerEnchant inventory = inventories.getOrDefault(player.getPersistentID(), new ItemStackHandlerEnchant());
        inventories.put(player.getPersistentID(), inventory);
        return inventory;
    }

    @Override
    public void writeNBT (NBTTagCompound dataTag) {

        NBTTagList list = new NBTTagList();
        
        for (Entry<UUID, ItemStackHandlerEnchant> inventory : this.inventories.entrySet()) {
            
            NBTTagCompound invTag = new NBTTagCompound();
            invTag.setUniqueId("Owner", inventory.getKey());
            invTag.setTag("Inventory", inventory.getValue().serializeNBT());
            list.appendTag(invTag);
        }
        
        dataTag.setTag("InvList", list);
    }

    @Override
    public void readNBT (NBTTagCompound dataTag) {

        this.inventories.clear();

        NBTTagList list = dataTag.getTagList("InvList", NBT.TAG_COMPOUND);
        
        for (int i = 0; i < list.tagCount(); i++) {
            
            final NBTTagCompound tag = list.getCompoundTagAt(i);
            
            if (tag != null) {
                
                UUID owner = tag.getUniqueId("Owner");
                ItemStackHandlerEnchant inv = new ItemStackHandlerEnchant();
                inv.deserializeNBT(tag.getCompoundTag("Inventory"));
                this.inventories.put(owner, inv);
            }
        }
    }
}