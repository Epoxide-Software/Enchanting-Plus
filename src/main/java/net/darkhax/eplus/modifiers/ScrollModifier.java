package net.darkhax.eplus.modifiers;

import net.darkhax.bookshelf.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ScrollModifier {
    
    /**
     * The ItemStack to associate with this modifier.
     */
    public ItemStack stack;
    
    /**
     * The amount of additional stability this modifier will add. This can be 0, or a negative.
     */
    public float stability;
    
    /**
     * The amount of additional speed to add to the inscription process. This can be 0, or a
     * negative.
     */
    public float speed;
    
    /**
     * A flag which determines whether or not onInscription should be called.
     */
    public boolean handleInscriptions;
    
    /**
     * Constructs a new ScrollModifier. ScrollModifiers are a basic object which are used with
     * the Arcane Inscriber. A ScrollModifier can do several things, such as increase the
     * stability of the inscription process, or increase the speed on the inscription process.
     * They can also be used to change how the outcome of the inscription process.
     * 
     * @param stack The ItemStack to declare as the modifier. When an ItemStack with the same
     *        ID and meta as this one is added to the inscription process, this modifier will
     *        be applied.
     * @param stability The amount of stability that this modifier will bring to the
     *        inscription process. The float represents a direct percentage, where 0.05 is 5%
     *        stability. Stability is used to calculate whether or not the process will fail.
     *        You can also use a negative float, to reduce stability.
     * @param speed The amount of speed which this modifier will add to the recipe. Progress is
     *        handled by a float, where 0.0 is 0% complete, and 1.0 is 100% complete. Every
     *        update, the speed float will be added to the recipe progression float. You can
     *        use a negative float to make the recipe slower.
     * @param handleInscriptions A flag to enable/disable the onInscription method. If you have
     *        no need for the method, this can be set to false, which will save the user a
     *        negligible amount of cpu and memory.
     */
    public ScrollModifier(ItemStack stack, float stability, float speed, boolean handleInscriptions) {
        
        this.stack = stack;
        this.stability = stability;
        this.speed = speed;
        this.handleInscriptions = handleInscriptions;
    }
    
    /**
     * Called when the inscription process is completed. This method allows for special
     * behavior to happen when your modifier is being used. It can also be used to alter the
     * outcome of the inscription process.
     * 
     * @param world The instance of World in which the inscription process took place.
     * @param x The X coordinate of the arcane inscriber.
     * @param y The Y coordinate of the arcane inscriber.
     * @param z The Z coordinate of the arcane inscriber.
     * @param output The ItemStack that has been created by the inscription process. This will
     *        be null if the process failed.
     * @param input The ItemStack that was used as an input for the process. Highly likely to
     *        be an Enchanted Book. Might be null, if set so by another modifier.
     * @param firstModifier The first modifier to be used in the recipe. This may be null.
     * @param secondModifier The second modifier to be used in the recipe. This may be null.
     * @return ItemStack The ItemStack that should be returned as the output for the
     *         inscription.
     */
    public ItemStack onInscription (World world, int x, int y, int z, ItemStack output, ItemStack input, ItemStack firstModifier, ItemStack secondModifier) {
        
        return output;
    }
    
    /**
     * Writes the ScrollModifier to an NBTTagCompound. The ItemStack, stability, and speed are
     * all stored.
     * 
     * @param tag The NBTTagCompound to write the ScrollModifier to.
     * @param tagName The name to store the tag under.
     */
    public void writeModiferToNBT (NBTTagCompound tag, String tagName) {
        
        final NBTTagCompound modifierTag = new NBTTagCompound();
        modifierTag.setFloat("speed", this.speed);
        modifierTag.setFloat("stability", this.stability);
        StackUtils.writeStackToTag(this.stack, tag, "stackTag");
        tag.setTag(tagName, modifierTag);
    }
}