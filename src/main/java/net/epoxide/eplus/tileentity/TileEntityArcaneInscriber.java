package net.epoxide.eplus.tileentity;

import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.epoxide.eplus.handler.ContentHandler;
import net.epoxide.eplus.modifiers.ScrollModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityArcaneInscriber extends TileEntity {
    
    private ItemStack enchantmentBook;
    private ItemStack output;
    private ScrollModifier[] modifiers;
    private float progression;
    
    public void updateTileInfo () {
        
        this.enchantmentBook = null;
        this.output = null;
        this.modifiers = new ScrollModifier[2];
        this.progression = 0f;
        this.markDirty();
    }
    
    @Override
    public void readFromNBT (NBTTagCompound nbt) {
        
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
    public void writeToNBT (NBTTagCompound nbt) {
        
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
    
    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity pkt) {
        
        this.readFromNBT(pkt.func_148857_g());
        this.worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }
    
    @Override
    public Packet getDescriptionPacket () {
        
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
    }
    
    /**
     * Retrieves the EnchantmentBook ItemStack.
     * 
     * @return ItemStack: The ItemStack used as an EnchantmentBook. May not be an Enchantment
     *         Book.
     */
    public ItemStack getEnchantmentBook () {
        
        return this.enchantmentBook;
    }
    
    /**
     * Sets the ItemStack in the EnchantmentBook slot. This can not be used to set the item to
     * null.
     * 
     * @param stack: The ItemStack to set as the Enchantment Book item.
     * @return boolean: Whether or not the item was successfully added.
     */
    public boolean setEnchantmentBook (ItemStack stack) {
        
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
    public ItemStack getOutput () {
        
        return this.output;
    }
    
    /**
     * Sets the ItemStack in the output slot. This can not be used to set the ItemStack to
     * null.
     * 
     * @param output: The ItemStack to set in the output slot.
     * @return boolean: Whether or not the output was set.
     */
    public boolean setOutput (ItemStack output) {
        
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
    public ScrollModifier[] getModifiers () {
        
        return this.modifiers;
    }
    
    /**
     * Retrieves the first modifier.
     * 
     * @return ItemStack: The modifier in the first slot.
     */
    public ScrollModifier getFirstModifier () {
        
        return this.modifiers[0];
    }
    
    /**
     * Sets the first modifier slot to the modifier passed.
     * 
     * @param modifier: The modifier to apply in the first slot.
     */
    public void setFirstModifier (ScrollModifier modifier) {
        
        this.modifiers[0] = modifier;
    }
    
    /**
     * Retrieves the second modifier.
     * 
     * @return ItemStack: The modifier in the second slot.
     */
    public ScrollModifier getSecondModifier () {
        
        return this.modifiers[1];
    }
    
    /**
     * Sets the second modifier slot to the modifier passed.
     * 
     * @param modifier: The modifier to apply in the second slot.
     */
    public void setSecondModifier (ScrollModifier modifier) {
        
        this.modifiers[1] = modifier;
    }
    
    /**
     * Completely clears all modifiers, and sets the array to a new one.
     */
    public void clearModifiers () {
        
        this.modifiers = new ScrollModifier[2];
    }
    
    /**
     * Retrieves the progression of the inscription process. Progress is represented as a
     * float, where 0.0 is 0%, and 1.0 is 100%. The outcome is modified to this range.
     * 
     * @return float: The progression of the inscription process. If the progression is less
     *         than 0, 0 will be returned. If the progression is greater than 1, 1 will be
     *         returned.
     */
    public float getProgression () {
        
        return (this.progression > 1f) ? 1f : (this.progression < 0f) ? 0f : this.progression;
    }
    
    /**
     * Sets the progression of the inscription process. Progress is represented as a float,
     * where 0.0 is 0% and 1.0 is 100%. The value you pass will automatically be modified to
     * fit within this range.
     * 
     * @param time: The new progression value for the process. If this value is less then 0, it
     *            will be set to 0. If this value is greater than 1, it will be set to 1.
     */
    public void setProgression (float time) {
        
        this.progression = (time > 1f) ? 1f : (time < 0f) ? 0f : time;
    }
}