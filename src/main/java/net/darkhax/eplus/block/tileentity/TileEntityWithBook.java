package net.darkhax.eplus.block.tileentity;

import java.util.Random;

import net.darkhax.bookshelf.block.tileentity.TileEntityBasicTickable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public abstract class TileEntityWithBook extends TileEntityBasicTickable {

    private static Random rand = new Random();

    public int tickCount;
    public float pageFlip;
    public float pageFlipPrev;
    public float flipRandom;
    public float flipTurn;
    public float bookSpread;
    public float bookSpreadPrev;
    public float bookRotation;
    public float bookRotationPrev;
    public float offset;

    public boolean isOpen () {

        return this.bookSpread >= 1;
    }

    @Override
    public void onEntityUpdate () {

        if (this.bookSpreadPrev != this.bookSpread && (this.bookSpread == 0f || this.bookSpread == 1f)) {

            this.getWorld().notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
        }

        this.bookSpreadPrev = this.bookSpread;
        this.bookRotationPrev = this.bookRotation;
        final EntityPlayer entityplayer = this.world.getClosestPlayer(this.pos.getX() + 0.5F, this.pos.getY() + 0.5F, this.pos.getZ() + 0.5F, 3.0D, false);

        if (entityplayer != null) {
            final double d0 = entityplayer.posX - (this.pos.getX() + 0.5F);
            final double d1 = entityplayer.posZ - (this.pos.getZ() + 0.5F);
            this.offset = (float) MathHelper.atan2(d1, d0);
            this.bookSpread += 0.1F;

            if (this.bookSpread < 0.5F || rand.nextInt(40) == 0) {
                final float f1 = this.flipRandom;

                while (true) {
                    this.flipRandom += rand.nextInt(4) - rand.nextInt(4);

                    if (f1 != this.flipRandom) {
                        break;
                    }
                }
            }
        }
        else {
            this.offset += 0.02F;
            this.bookSpread -= 0.1F;
        }

        while (this.bookRotation >= (float) Math.PI) {
            this.bookRotation -= (float) Math.PI * 2F;
        }

        while (this.bookRotation < -(float) Math.PI) {
            this.bookRotation += (float) Math.PI * 2F;
        }

        while (this.offset >= (float) Math.PI) {
            this.offset -= (float) Math.PI * 2F;
        }

        while (this.offset < -(float) Math.PI) {
            this.offset += (float) Math.PI * 2F;
        }

        float f2;

        for (f2 = this.offset - this.bookRotation; f2 >= (float) Math.PI; f2 -= (float) Math.PI * 2F) {
            ;
        }

        while (f2 < -(float) Math.PI) {
            f2 += (float) Math.PI * 2F;
        }

        this.bookRotation += f2 * 0.4F;
        this.bookSpread = MathHelper.clamp(this.bookSpread, 0.0F, 1.0F);
        ++this.tickCount;
        this.pageFlipPrev = this.pageFlip;
        float f = (this.flipRandom - this.pageFlip) * 0.4F;
        final float f3 = 0.2F;
        f = MathHelper.clamp(f, -f3, f3);
        this.flipTurn += (f - this.flipTurn) * 0.9F;
        this.pageFlip += this.flipTurn;
    }
}
