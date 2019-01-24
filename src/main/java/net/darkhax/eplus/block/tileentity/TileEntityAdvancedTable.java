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

        final ItemStackHandlerEnchant inventory = this.inventories.getOrDefault(player.getPersistentID(), new ItemStackHandlerEnchant(this));
        this.inventories.put(player.getPersistentID(), inventory);
        return inventory;
    }

    public Map<UUID, ItemStackHandlerEnchant> getInveotries () {

        return this.inventories;
    }

    @Override
    public void writeNBT (NBTTagCompound dataTag) {

        final NBTTagList list = new NBTTagList();

        for (final Entry<UUID, ItemStackHandlerEnchant> inventory : this.inventories.entrySet()) {

            final NBTTagCompound invTag = new NBTTagCompound();
            invTag.setUniqueId("Owner", inventory.getKey());
            invTag.setTag("Inventory", inventory.getValue().serializeNBT());
            list.appendTag(invTag);
        }

        dataTag.setTag("InvList", list);
    }

    @Override
    public void readNBT (NBTTagCompound dataTag) {

        this.inventories.clear();

        final NBTTagList list = dataTag.getTagList("InvList", NBT.TAG_COMPOUND);

        for (int i = 0; i < list.tagCount(); i++) {

            final NBTTagCompound tag = list.getCompoundTagAt(i);

            if (tag != null) {

                final UUID owner = tag.getUniqueId("Owner");
                final ItemStackHandlerEnchant inv = new ItemStackHandlerEnchant(this);
                inv.deserializeNBT(tag.getCompoundTag("Inventory"));
                this.inventories.put(owner, inv);
            }
        }
    }
}