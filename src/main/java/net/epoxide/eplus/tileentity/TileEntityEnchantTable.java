package net.epoxide.eplus.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.Random;

public class TileEntityEnchantTable extends TileEntity {
    public int tickCount;
    public float pageFlip;
    public float pageFlipPrev;
    public float field_145932_k;
    public float pageFlipTurn;
    public float field_145930_m;
    public float field_145927_n;
    public float field_145928_o;
    public float field_145925_p;
    public float field_145924_q;
    private static Random random = new Random();
    private String customName;

    @Override
    public void writeToNBT (NBTTagCompound compound) {

        super.writeToNBT(compound);

        if (this.func_145921_b()) {
            compound.setString("CustomName", this.customName);
        }
    }

    @Override
    public void readFromNBT (NBTTagCompound compound) {

        super.readFromNBT(compound);

        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
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
            this.field_145924_q = (float) Math.atan2(d1, d0);
            this.field_145930_m += 0.1F;

            if (this.field_145930_m < 0.5F || random.nextInt(40) == 0) {
                float f1 = this.field_145932_k;

                do {
                    this.field_145932_k += (float) (random.nextInt(4) - random.nextInt(4));
                } while (f1 == this.field_145932_k);
            }
        }
        else {
            this.field_145924_q += 0.02F;
            this.field_145930_m -= 0.1F;
        }

        while (this.field_145928_o >= (float) Math.PI) {
            this.field_145928_o -= ((float) Math.PI * 2F);
        }

        while (this.field_145928_o < -(float) Math.PI) {
            this.field_145928_o += ((float) Math.PI * 2F);
        }

        while (this.field_145924_q >= (float) Math.PI) {
            this.field_145924_q -= ((float) Math.PI * 2F);
        }

        while (this.field_145924_q < -(float) Math.PI) {
            this.field_145924_q += ((float) Math.PI * 2F);
        }

        float f2;

        f2 = this.field_145924_q - this.field_145928_o;
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
        float f = (this.field_145932_k - this.pageFlip) * 0.4F;
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

    public String func_145919_a () {

        return this.func_145921_b() ? this.customName : "container.enchant";
    }

    public boolean func_145921_b () {

        return this.customName != null && this.customName.length() > 0;
    }

    public void func_145920_a (String customName) {

        this.customName = customName;
    }
}