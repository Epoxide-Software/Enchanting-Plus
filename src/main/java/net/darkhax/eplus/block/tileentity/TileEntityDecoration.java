package net.darkhax.eplus.block.tileentity;

import java.awt.Color;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityDecoration extends TileEntityWithBook {

    public float height = 0f;
    public int color = Color.WHITE.getRGB();
    public int variant;

    public void decreaseHeight () {

        this.height -= 0.05f;

        if (this.height < -0.35f) {
            this.height = -0.35f;
        }
    }

    public void increaseHeight () {

        this.height += 0.05f;

        if (this.height > 0.35f) {
            this.height = 0.35f;
        }
    }

    @Override
    public void readNBT (NBTTagCompound dataTag) {

        this.height = dataTag.getFloat("Height");
        this.color = dataTag.getInteger("Color");
        this.variant = dataTag.getInteger("Variant");
    }

    @Override
    public void writeNBT (NBTTagCompound dataTag) {

        dataTag.setFloat("Height", this.height);
        dataTag.setInteger("Color", this.color);
        dataTag.setInteger("Variant", this.variant);
    }
}