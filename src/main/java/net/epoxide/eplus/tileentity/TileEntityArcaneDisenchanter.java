package net.epoxide.eplus.tileentity;

import cpw.mods.fml.common.network.NetworkRegistry;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.darkhax.bookshelf.lib.util.MathsUtils;
import net.darkhax.bookshelf.tileentity.AbstractTileEntity;
import net.epoxide.eplus.EnchantingPlus;
import net.epoxide.eplus.common.network.PacketArcaneDisenchanterEffects;
import net.epoxide.eplus.handler.ContentHandler;
import net.epoxide.eplus.item.ItemEnchantedScroll;
import net.epoxide.eplus.modifiers.ScrollModifier;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Random;

public class TileEntityArcaneDisenchanter extends AbstractTileEntity {
    private ItemStack enchantmentBook;
    private ScrollModifier[] modifiers;

    private final float BASE_SPEED = 0.02f;

    private float currentPercentage = 0f;
    private ItemStack output;

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

    public TileEntityArcaneDisenchanter () {

        modifiers = new ScrollModifier[2];
    }

    @Override
    public void loadFromNBT (NBTTagCompound nbtTag) {

        NBTTagList list = nbtTag.getTagList("Item", 9);
        enchantmentBook = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(0));

        for (ScrollModifier modifier : ContentHandler.modifiers) {
            ItemStack itemStack = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(1));
            if (modifier.stack == itemStack) {
                modifiers[0] = modifier;
            }
        }
        for (ScrollModifier modifier : ContentHandler.modifiers) {
            ItemStack itemStack = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(2));
            if (modifier.stack == itemStack) {
                modifiers[1] = modifier;
            }
        }
    }

    @Override
    public void saveToNBT (NBTTagCompound nbtTag) {

        final NBTTagList nbtTagList = new NBTTagList();

        NBTTagCompound tagCompound = new NBTTagCompound();
        if (enchantmentBook != null)
            enchantmentBook.writeToNBT(tagCompound);
        nbtTagList.appendTag(tagCompound);

        tagCompound = new NBTTagCompound();
        if (modifiers[0] != null)
            modifiers[0].stack.writeToNBT(tagCompound);
        nbtTagList.appendTag(tagCompound);

        tagCompound = new NBTTagCompound();
        if (modifiers[1] != null)
            modifiers[1].stack.writeToNBT(tagCompound);
        nbtTagList.appendTag(tagCompound);

        nbtTag.setTag("Item", nbtTagList);
    }

    @Override
    public void loadClientDataFromNBT (NBTTagCompound nbtTag) {

        NBTTagList list = nbtTag.getTagList("Item", 9);
        enchantmentBook = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(0));

        for (ScrollModifier modifier : ContentHandler.modifiers) {
            ItemStack itemStack = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(1));
            if (modifier.stack == itemStack) {
                modifiers[0] = modifier;
            }
        }
        for (ScrollModifier modifier : ContentHandler.modifiers) {
            ItemStack itemStack = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(2));
            if (modifier.stack == itemStack) {
                modifiers[1] = modifier;
            }
        }
    }

    @Override
    public void saveClientDataToNBT (NBTTagCompound nbtTag) {

        final NBTTagList nbtTagList = new NBTTagList();

        NBTTagCompound tagCompound = new NBTTagCompound();
        if (enchantmentBook != null)
            enchantmentBook.writeToNBT(tagCompound);
        nbtTagList.appendTag(tagCompound);

        tagCompound = new NBTTagCompound();
        if (modifiers[0] != null)
            modifiers[0].stack.writeToNBT(tagCompound);
        nbtTagList.appendTag(tagCompound);

        tagCompound = new NBTTagCompound();
        if (modifiers[1] != null)
            modifiers[1].stack.writeToNBT(tagCompound);
        nbtTagList.appendTag(tagCompound);

        nbtTag.setTag("Item", nbtTagList);
    }

    int ticker = 0;

    @Override
    public void updateEntity () {

        if (hasEnchantmentBook()) {
            ticker++;
            updateBook();

            float speed = (BASE_SPEED + (hasModifiers(0) ? modifiers[0].speed : 0) + (hasModifiers(1) ? modifiers[1].speed : 0)) / 20f;
            currentPercentage += (speed > 0 ? speed : 0.0000001f);

            if (ticker >= 20) {
                EnchantingPlus.network.sendToAllAround(new PacketArcaneDisenchanterEffects(output != null, this, currentPercentage), new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 150d));
                ticker -= 20;
            }

            if (currentPercentage >= 1) {
                if (MathsUtils.tryPercentage(0.50d + (hasModifiers(0) ? modifiers[0].stability : 0) + (hasModifiers(1) ? modifiers[1].stability : 0))) {
                    Enchantment[] enchantments = ItemStackUtils.getEnchantmentsFromStack(enchantmentBook, true);

                    ItemStack itemStack = ItemEnchantedScroll.createScroll(enchantments[((int) (Math.random() * enchantments.length))]);
                    if (hasModifiers(0))
                        itemStack = modifiers[0].onInscription(worldObj, xCoord, yCoord, zCoord, itemStack, enchantmentBook, modifiers[0].stack, hasModifiers(1) ? modifiers[1].stack : null);
                    if (hasModifiers(1))
                        itemStack = modifiers[1].onInscription(worldObj, xCoord, yCoord, zCoord, itemStack, enchantmentBook, hasModifiers(0) ? modifiers[0].stack : null, modifiers[1].stack);
                    setOutput(itemStack);
                }
                EnchantingPlus.network.sendToAllAround(new PacketArcaneDisenchanterEffects(output != null, this, currentPercentage), new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 150d));

                clearModifiers();
                enchantmentBook = null;

                reset();
            }
        }
    }

    private void reset () {

        currentPercentage = 0;

        tickCount = 0;
        pageFlip = 0;
        pageFlipPrev = 0;
        pageFlipRandom = 0;
        pageFlipTurn = 0;
        field_145930_m = 0;
        field_145927_n = 0;
        field_145928_o = 0;
        field_145925_p = 0;
        bookRotation = 0;
    }

    private void updateBook () {

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

    public boolean hasEnchantmentBook () {

        return enchantmentBook != null;
    }

    public void setEnchantmentBook (ItemStack itemStack) {

        reset();
        this.enchantmentBook = itemStack;
    }

    public boolean addModifiers (ScrollModifier itemStack) {

        for (int i = 0; i < modifiers.length; i++) {
            if (modifiers[i] == null) {
                modifiers[i] = itemStack;
                return true;
            }
        }
        return false;
    }

    public ItemStack getEnchantmentBook () {

        return enchantmentBook;
    }

    public boolean hasModifiers () {

        return modifiers[0] != null || modifiers[1] != null;
    }

    public boolean hasModifiers (int i) {

        return modifiers[i] != null;
    }

    public ScrollModifier getModifier (int i) {

        return modifiers[i];
    }

    public void clearModifiers () {

        for (int i = 0; i < modifiers.length; i++) {
            modifiers[i] = null;
        }
    }

    public boolean hasScroll () {

        return output != null;
    }

    public void setOutput (ItemStack output) {

        this.output = output;
    }

    public ItemStack getOutput () {

        return output;
    }
}
