package eplus.inventory;

import net.minecraft.entity.player.EntityPlayer;
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

    public void readCustomNBT(NBTTagCompound tags)
    {
        NBTTagList nbtTagList = tags.getTagList("Item");

        for (int i = 0; i < nbtTagList.tagCount(); i++) {
            NBTTagCompound tagCompound = (NBTTagCompound) nbtTagList.tagAt(i);
            itemInTable = ItemStack.loadItemStackFromNBT(tagCompound);
        }
    }

    public void writeCustomNBT(NBTTagCompound tags)
    {

        NBTTagList nbtTagList = new NBTTagList();
        if (itemInTable != null) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            itemInTable.writeToNBT(tagCompound);
            nbtTagList.appendTag(tagCompound);
        }

        tags.setTag("Item", nbtTagList);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        this.bookRotationPrev = this.bookRotation2;
        EntityPlayer entityplayer = this.worldObj.getClosestPlayer((double) ((float) this.xCoord + 0.5F), (double) ((float) this.yCoord + 0.5F), (double) ((float) this.zCoord + 0.5F), 3.0D);

        if (entityplayer != null) {
            double d0 = entityplayer.posX - (double) ((float) this.xCoord + 0.5F);
            double d1 = entityplayer.posZ - (double) ((float) this.zCoord + 0.5F);
            this.bookRotation = (float) Math.atan2(d1, d0);
        } else {
            this.bookRotation += 0.02F;
        }

        while (this.bookRotation2 >= (float) Math.PI) {
            this.bookRotation2 -= ((float) Math.PI * 2F);
        }

        while (this.bookRotation2 < -(float) Math.PI) {
            this.bookRotation2 += ((float) Math.PI * 2F);
        }

        while (this.bookRotation >= (float) Math.PI) {
            this.bookRotation -= ((float) Math.PI * 2F);
        }

        while (this.bookRotation < -(float) Math.PI) {
            this.bookRotation += ((float) Math.PI * 2F);
        }

        float f1;

        for (f1 = this.bookRotation - this.bookRotation2; f1 >= (float) Math.PI; f1 -= ((float) Math.PI * 2F)) {
            ;
        }

        while (f1 < -(float) Math.PI) {
            f1 += ((float) Math.PI * 2F);
        }

        this.bookRotation2 += f1 * 0.4F;
        ++this.tickCount;
    }
}
