package net.darkhax.eplus.block.tileentity;

import net.darkhax.bookshelf.block.tileentity.TileEntityBasicTickable;
import net.darkhax.bookshelf.lib.Constants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;

public class TileEntityAdvancedTable extends TileEntityBasicTickable implements IInteractionObject {

    public ItemStack stack = ItemStack.EMPTY;

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

    @Override
    public Container createContainer (InventoryPlayer playerInventory, EntityPlayer playerIn) {

        return new ContainerEnchantment(playerInventory, this.world, this.pos);
    }

    @Override
    public ITextComponent getDisplayName () {

        return new TextComponentString(this.getName());
    }

    @Override
    public String getGuiID () {

        return "eplus:enchanting_table";
    }

    @Override
    public String getName () {

        return "container.enchant";
    }

    @Override
    public boolean hasCustomName () {

        return false;
    }

    @Override
    public void writeNBT (NBTTagCompound dataTag) {

        dataTag.setTag("HeldItem", this.stack.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readNBT (NBTTagCompound dataTag) {

        this.stack = new ItemStack(dataTag.getCompoundTag("HeldItem"));
    }

    @Override
    public void onEntityUpdate () {

        this.bookSpreadPrev = this.bookSpread;
        this.bookRotationPrev = this.bookRotation;
        final EntityPlayer entityplayer = this.world.getClosestPlayer(this.pos.getX() + 0.5F, this.pos.getY() + 0.5F, this.pos.getZ() + 0.5F, 3.0D, false);

        if (entityplayer != null) {
            final double d0 = entityplayer.posX - (this.pos.getX() + 0.5F);
            final double d1 = entityplayer.posZ - (this.pos.getZ() + 0.5F);
            this.offset = (float) MathHelper.atan2(d1, d0);
            this.bookSpread += 0.1F;

            if (this.bookSpread < 0.5F || Constants.RANDOM.nextInt(40) == 0) {
                final float f1 = this.flipRandom;

                while (true) {
                    this.flipRandom += Constants.RANDOM.nextInt(4) - Constants.RANDOM.nextInt(4);

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