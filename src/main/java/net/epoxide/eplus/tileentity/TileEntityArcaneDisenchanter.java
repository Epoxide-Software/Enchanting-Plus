package net.epoxide.eplus.tileentity;

import cpw.mods.fml.common.network.NetworkRegistry;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.darkhax.bookshelf.lib.util.MathsUtils;
import net.epoxide.eplus.EnchantingPlus;
import net.epoxide.eplus.common.network.PacketArcaneDisenchanterEffects;
import net.epoxide.eplus.handler.ContentHandler;
import net.epoxide.eplus.item.ItemEnchantedScroll;
import net.epoxide.eplus.modifiers.ScrollModifier;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.Random;

public class TileEntityArcaneDisenchanter extends TileEntity {

    private ItemStack enchantmentBook;
    private ScrollModifier[] modifiers;

    private final float BASE_SPEED = 0.02f;
    private final float BASE_STABILITY = 0.5f;

    private float progression = 0f;
    private ItemStack output;

    public int tickCount;
    public float pageFlip;
    public float pageFlipPrev;
    public float pageFlipRandom;
    public float pageFlipTurn;
    public float foldAmount;
    public float prevFoldAmount;
    public float rotation;
    public float prevRotation;
    public float bookRotation;
    private static Random random = new Random();

    public TileEntityArcaneDisenchanter() {

        modifiers = new ScrollModifier[2];
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

        super.readFromNBT(nbt);

        if (nbt.hasKey("enchantBook"))
            this.enchantmentBook = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("enchantBook"));

        if (nbt.hasKey("outputStack"))
            this.output = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("outputStack"));

        if (nbt.hasKey("modifier0"))
            this.modifiers[0] = ContentHandler.findScrollModifier(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("modifier0").getCompoundTag("stackTag")));

        if (nbt.hasKey("modifier1"))
            this.modifiers[1] = ContentHandler.findScrollModifier(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("modifier1").getCompoundTag("stackTag")));

        this.progression = nbt.getFloat("progression");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {

        super.writeToNBT(nbt);

        if (ItemStackUtils.isValidStack(this.enchantmentBook))
            ItemStackUtils.writeStackToTag(this.enchantmentBook, nbt, "enchantBook");

        if (ItemStackUtils.isValidStack(this.output))
            ItemStackUtils.writeStackToTag(this.output, nbt, "outputStack");

        if (this.modifiers[0] != null)
            this.modifiers[0].writeModiferToNBT(nbt, "modifier0");

        if (this.modifiers[1] != null)
            this.modifiers[1].writeModiferToNBT(nbt, "modifier1");

        nbt.setFloat("progression", this.progression);
    }

    int ticker = 0;

    @Override
    public void updateEntity() {

        if (getEnchantmentBook() != null) {
            ticker++;
            updateBook();

            progression += getSpeed();

            if (ticker >= 20) {
                EnchantingPlus.network.sendToAllAround(new PacketArcaneDisenchanterEffects(output != null, this, progression), new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 150d));
                ticker -= 20;
            }

            if (progression >= 1) {
                if (MathsUtils.tryPercentage(getStability())) {
                    Enchantment[] enchantments = ItemStackUtils.getEnchantmentsFromStack(enchantmentBook, true);

                    ItemStack itemStack = ItemEnchantedScroll.createScroll(enchantments[((int) (Math.random() * enchantments.length))]);
                    if (getFirstModifier() != null)
                        itemStack = modifiers[0].onInscription(worldObj, xCoord, yCoord, zCoord, itemStack, enchantmentBook, modifiers[0].stack, getSecondModifier() != null ? modifiers[1].stack : null);
                    if (getSecondModifier() != null)
                        itemStack = modifiers[1].onInscription(worldObj, xCoord, yCoord, zCoord, itemStack, enchantmentBook, getFirstModifier() != null ? modifiers[0].stack : null, modifiers[1].stack);
                    setOutput(itemStack);
                }
                EnchantingPlus.network.sendToAllAround(new PacketArcaneDisenchanterEffects(output != null, this, progression), new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 150d));

                clearModifiers();
                enchantmentBook = null;

                updateTileInfo();
            }
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {

        this.readFromNBT(pkt.func_148857_g());
        this.worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

    @Override
    public Packet getDescriptionPacket() {

        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
    }

    public void updateTileInfo() {

        this.enchantmentBook = null;
        this.output = null;
        this.modifiers = new ScrollModifier[2];
        this.progression = 0f;

        tickCount = 0;
        pageFlip = 0;
        pageFlipPrev = 0;
        pageFlipRandom = 0;
        pageFlipTurn = 0;
        foldAmount = 0;
        prevFoldAmount = 0;
        rotation = 0;
        prevRotation = 0;
        bookRotation = 0;

        this.markDirty();
    }

    private void updateBook() {

        this.prevFoldAmount = this.foldAmount;
        this.prevRotation = this.rotation;
        EntityPlayer entityplayer = this.worldObj.getClosestPlayer((double) ((float) this.xCoord + 0.5F), (double) ((float) this.yCoord + 0.5F), (double) ((float) this.zCoord + 0.5F), 3.0D);

        if (entityplayer != null) {
            double d0 = entityplayer.posX - (double) ((float) this.xCoord + 0.5F);
            double d1 = entityplayer.posZ - (double) ((float) this.zCoord + 0.5F);
            this.bookRotation = (float) Math.atan2(d1, d0);
            this.foldAmount += 0.1F;

            if (this.foldAmount < 0.5F || random.nextInt(40) == 0) {
                float f1 = this.pageFlipRandom;

                do {
                    this.pageFlipRandom += (float) (random.nextInt(4) - random.nextInt(4));
                }
                while (f1 == this.pageFlipRandom);
            }
        } else {
            this.bookRotation += 0.02F;
            this.foldAmount -= 0.1F;
        }

        while (this.rotation >= (float) Math.PI) {
            this.rotation -= ((float) Math.PI * 2F);
        }

        while (this.rotation < -(float) Math.PI) {
            this.rotation += ((float) Math.PI * 2F);
        }

        while (this.bookRotation >= (float) Math.PI) {
            this.bookRotation -= ((float) Math.PI * 2F);
        }

        while (this.bookRotation < -(float) Math.PI) {
            this.bookRotation += ((float) Math.PI * 2F);
        }

        float f2;

        f2 = this.bookRotation - this.rotation;
        while (f2 >= (float) Math.PI) {
            f2 -= ((float) Math.PI * 2F);
        }

        while (f2 < -(float) Math.PI) {
            f2 += ((float) Math.PI * 2F);
        }

        this.rotation += f2 * 0.4F;

        if (this.foldAmount < 0.0F) {
            this.foldAmount = 0.0F;
        }

        if (this.foldAmount > 1.0F) {
            this.foldAmount = 1.0F;
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

    /**
     * Retrieves the EnchantmentBook ItemStack.
     *
     * @return ItemStack: The ItemStack used as an EnchantmentBook. May not be an Enchantment
     * Book.
     */
    public ItemStack getEnchantmentBook() {

        return this.enchantmentBook;
    }

    /**
     * Sets the ItemStack in the EnchantmentBook slot. This can not be used to set the item to
     * null.
     *
     * @param stack: The ItemStack to set as the Enchantment Book item.
     * @return boolean: Whether or not the item was successfully added.
     */
    public boolean setEnchantmentBook(ItemStack stack) {

        if (ItemStackUtils.isValidStack(enchantmentBook)) {

            this.enchantmentBook = stack;
            return true;
        }

        return false;
    }

    /**
     * Retrieves the item in the output slot. Likely null, or a scroll.
     *
     * @return ItemStack: The ItemStack stored in the output slot.
     */
    public ItemStack getOutput() {

        return this.output;
    }

    /**
     * Sets the ItemStack in the output slot. This can not be used to set the ItemStack to
     * null.
     *
     * @param output: The ItemStack to set in the output slot.
     * @return boolean: Whether or not the output was set.
     */
    public boolean setOutput(ItemStack output) {

        if (ItemStackUtils.isValidStack(output)) {

            this.output = output;
            return true;
        }

        return false;
    }

    /**
     * Provides access to the modifiers array.
     *
     * @return ScrollModifier[]: The array of modifiers applied to this Tile Entity.
     */
    public ScrollModifier[] getModifiers() {

        return this.modifiers;
    }

    /**
     * Retrieves the first modifier.
     *
     * @return ItemStack: The modifier in the first slot.
     */
    public ScrollModifier getFirstModifier() {

        return this.modifiers[0];
    }

    /**
     * Sets the first modifier slot to the modifier passed.
     *
     * @param modifier: The modifier to apply in the first slot.
     */
    public void setFirstModifier(ScrollModifier modifier) {

        this.modifiers[0] = modifier;
    }

    /**
     * Retrieves the second modifier.
     *
     * @return ItemStack: The modifier in the second slot.
     */
    public ScrollModifier getSecondModifier() {

        return this.modifiers[1];
    }

    /**
     * Sets the second modifier slot to the modifier passed.
     *
     * @param modifier: The modifier to apply in the second slot.
     */
    public void setSecondModifier(ScrollModifier modifier) {

        this.modifiers[1] = modifier;
    }

    /**
     * Completely clears all modifiers, and sets the array to a new one.
     */
    public void clearModifiers() {

        this.modifiers = new ScrollModifier[2];
    }

    /**
     * Retrieves the progression of the inscription process. Progress is represented as a
     * float, where 0.0 is 0%, and 1.0 is 100%. The outcome is modified to this range.
     *
     * @return float: The progression of the inscription process. If the progression is less
     * than 0, 0 will be returned. If the progression is greater than 1, 1 will be
     * returned.
     */
    public float getProgression() {

        return (this.progression > 1f) ? 1f : (this.progression < 0f) ? 0f : this.progression;
    }

    /**
     * Sets the progression of the inscription process. Progress is represented as a float,
     * where 0.0 is 0% and 1.0 is 100%. The value you pass will automatically be modified to
     * fit within this range.
     *
     * @param time: The new progression value for the process. If this value is less then 0, it
     *              will be set to 0. If this value is greater than 1, it will be set to 1.
     */
    public void setProgression(float time) {

        this.progression = (time > 1f) ? 1f : (time < 0f) ? 0f : time;
    }

    public float getSpeed() {

        float speed = (BASE_SPEED + (getFirstModifier() != null ? modifiers[0].speed : 0) + (getSecondModifier() != null ? modifiers[1].speed : 0)) / 20f;
        return speed <= 0.0001f ? 0.0001f : speed;
    }

    public float getStability() {

        return BASE_STABILITY + (getFirstModifier() != null ? modifiers[0].stability : 0) + (getSecondModifier() != null ? modifiers[1].stability : 0);
    }

    public boolean addModifiers(ScrollModifier scrollModifier) {

        if (getFirstModifier() == null) {
            setFirstModifier(scrollModifier);
            return true;
        }
        if (getSecondModifier() == null) {
            setSecondModifier(scrollModifier);
            return true;
        }
        return false;
    }
}