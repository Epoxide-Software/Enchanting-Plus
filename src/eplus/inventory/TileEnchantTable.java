package eplus.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntityEnchantmentTable;

import java.util.Random;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class TileEnchantTable extends TileEntityEnchantmentTable {

    private static Random rand = new Random();
    public ItemStack itemInTable;

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        writeCustomNBT(par1NBTTagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        readCustomNBT(par1NBTTagCompound);
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
    {
        readCustomNBT(pkt.customParam1);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }

    void readCustomNBT(NBTTagCompound tags)
    {
        NBTTagList nbtTagList = tags.getTagList("Item");

        itemInTable = null;
        for (int i = 0; i < nbtTagList.tagCount(); i++) {
            NBTTagCompound tagCompound = (NBTTagCompound) nbtTagList.tagAt(i);
            itemInTable = ItemStack.loadItemStackFromNBT(tagCompound);
        }
    }

    void writeCustomNBT(NBTTagCompound tags)
    {

        NBTTagList nbtTagList = new NBTTagList();
        if (itemInTable != null) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            itemInTable.writeToNBT(tagCompound);
            nbtTagList.appendTag(tagCompound);
        }

        tags.setTag("Item", nbtTagList);
    }
}
