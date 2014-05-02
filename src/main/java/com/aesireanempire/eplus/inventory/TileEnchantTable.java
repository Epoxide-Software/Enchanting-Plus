package com.aesireanempire.eplus.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;

/**
 * Enchanting Plus
 * 
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class TileEnchantTable extends TileEntityEnchantmentTable
{

    public ItemStack itemInTable;

    @Override
    public Packet getDescriptionPacket()
    {
        final NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        readCustomNBT(pkt.func_148857_g());
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    void readCustomNBT(NBTTagCompound tags)
    {
        final NBTTagList nbtTagList = tags.getTagList("Item", 10);

        itemInTable = null;
        for (int i = 0; i < nbtTagList.tagCount(); i++)
        {
            final NBTTagCompound tagCompound = (NBTTagCompound) nbtTagList.getCompoundTagAt(i);
            itemInTable = ItemStack.loadItemStackFromNBT(tagCompound);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        readCustomNBT(par1NBTTagCompound);
    }

    void writeCustomNBT(NBTTagCompound tags)
    {

        final NBTTagList nbtTagList = new NBTTagList();
        if (itemInTable != null)
        {
            final NBTTagCompound tagCompound = new NBTTagCompound();
            itemInTable.writeToNBT(tagCompound);
            nbtTagList.appendTag(tagCompound);
        }

        tags.setTag("Item", nbtTagList);
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        writeCustomNBT(par1NBTTagCompound);
    }
}
