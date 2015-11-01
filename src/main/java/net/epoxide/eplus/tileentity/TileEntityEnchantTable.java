package net.epoxide.eplus.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.Random;

public class TileEntityEnchantTable extends TileEntity {
    public int tickCount;
    public float pageFlip;
    public float pageFlipPrev;
    public float pageFlipRandom;
    public float pageFlipTurn;
    public float field_145930_m;
    public float field_145927_n;
    public float field_145928_o;
    public float field_145925_p;
    public float bookRotation;
    private static Random random = new Random();
    private String customName;
    public ItemStack itemInTable;

    @Override
    public Packet getDescriptionPacket () {

        final NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity pkt) {

        readCustomNBT(pkt.func_148857_g());
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    private void writeCustomNBT (NBTTagCompound compound) {
        final NBTTagList nbtTagList = new NBTTagList();
        if (itemInTable != null)
        {
            final NBTTagCompound tagCompound = new NBTTagCompound();
            itemInTable.writeToNBT(tagCompound);
            nbtTagList.appendTag(tagCompound);
        }

        compound.setTag("Item", nbtTagList);
    }
    @Override
    public void writeToNBT (NBTTagCompound compound) {

        super.writeToNBT(compound);

        if (this.hasCustomInventoryName()) {
            compound.setString("CustomName", this.customName);
        }
        writeCustomNBT(compound);
    }

    private void readCustomNBT (NBTTagCompound compound) {

        final NBTTagList nbtTagList = compound.getTagList("Item", 10);

        itemInTable = null;
        for (int i = 0; i < nbtTagList.tagCount(); i++) {
            final NBTTagCompound tagCompound = nbtTagList.getCompoundTagAt(i);
            itemInTable = ItemStack.loadItemStackFromNBT(tagCompound);
        }
    }

    @Override
    public void readFromNBT (NBTTagCompound compound) {

        super.readFromNBT(compound);

        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
        readCustomNBT(compound);
    }

    @Override
    public void updateEntity () {

        super.updateEntity();
        this.field_145927_n = this.field_145930_m;
        this.field_145925_p = this.field_145928_o;
        EntityPlayer entityplayer = this.worldObj.getClosestPlayer((double) ((float) this.xCoord + 0.5F), (double) ((float) this.yCoord + 0.5F), (double) ((float) this.zCoord + 0.5F), 3.0D);

        if (entityplayer != null) {
            double d0 = entityplayer.posX - (double) ((float) this.xCoord + 0.5F);
            double d1 = entityplayer.posZ - (double) ((float) this.zCoord + 0.5F);
            this.bookRotation = (float) Math.atan2(d1, d0);
            this.field_145930_m += 0.1F;

            if (this.field_145930_m < 0.5F || random.nextInt(40) == 0) {
                float f1 = this.pageFlipRandom;

                do {
                    this.pageFlipRandom += (float) (random.nextInt(4) - random.nextInt(4));
                } while (f1 == this.pageFlipRandom);
            }
        }
        else {
            this.bookRotation += 0.02F;
            this.field_145930_m -= 0.1F;
        }

        while (this.field_145928_o >= (float) Math.PI) {
            this.field_145928_o -= ((float) Math.PI * 2F);
        }

        while (this.field_145928_o < -(float) Math.PI) {
            this.field_145928_o += ((float) Math.PI * 2F);
        }

        while (this.bookRotation >= (float) Math.PI) {
            this.bookRotation -= ((float) Math.PI * 2F);
        }

        while (this.bookRotation < -(float) Math.PI) {
            this.bookRotation += ((float) Math.PI * 2F);
        }

        float f2;

        f2 = this.bookRotation - this.field_145928_o;
        while (f2 >= (float) Math.PI) {
            f2 -= ((float) Math.PI * 2F);
        }

        while (f2 < -(float) Math.PI) {
            f2 += ((float) Math.PI * 2F);
        }

        this.field_145928_o += f2 * 0.4F;

        if (this.field_145930_m < 0.0F) {
            this.field_145930_m = 0.0F;
        }

        if (this.field_145930_m > 1.0F) {
            this.field_145930_m = 1.0F;
        }

        ++this.tickCount;
        this.pageFlipPrev = this.pageFlip;
        float f = (this.pageFlipRandom - this.pageFlip) * 0.4F;
        float f3 = 0.2F;

        if (f < -f3) {
            f = -f3;
        }

        if (f > f3) {
            f = f3;
        }

        this.pageFlipTurn += (f - this.pageFlipTurn) * 0.9F;
        this.pageFlip += this.pageFlipTurn;
    }

    public boolean hasCustomInventoryName () {

        return this.customName != null && this.customName.length() > 0;
    }
}