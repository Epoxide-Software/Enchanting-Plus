package net.epoxide.eplus.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.common.primitives.Ints;

import net.epoxide.eplus.EnchantingPlus;
import net.epoxide.eplus.common.network.PacketSyncPlayerProperties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerProperties implements IExtendedEntityProperties {
    
    /**
     * The name of the tag used to store all player data.
     */
    public static final String PROP_NAME = "EnchantingPlusData";
    
    /**
     * An instance of the specific player that is being read and written to.
     */
    public EntityPlayer player;
    
    /**
     * A list containing the numeric IDs of all enchantments unlocked by the player.
     */
    public List<Integer> unlockedEcnahntments;
    
    /**
     * Constructs a new PlayerProperties instance, which is the wrapper used for interacting
     * with custom player data.
     * 
     * @param player: The player to create this data for.
     */
    private PlayerProperties(EntityPlayer player) {
        
        this.player = player;
        this.unlockedEcnahntments = new LinkedList<Integer>();
    }
    
    @Override
    public void saveNBTData (NBTTagCompound compound) {
        
        EnchantingPlus.printDebugMessage("Saving Enchanting Plus data");
        NBTTagCompound playerData = new NBTTagCompound();
        playerData.setIntArray("unlockedEnchantments", Ints.toArray(this.unlockedEcnahntments));
        compound.setTag(PROP_NAME, playerData);
    }
    
    @Override
    public void loadNBTData (NBTTagCompound compound) {
        
        EnchantingPlus.printDebugMessage("Loading Enchanting Plus data");
        NBTTagCompound playerData = compound.getCompoundTag(PROP_NAME);
        List<Integer> enchantments = new ArrayList();
        enchantments.addAll(Ints.asList(playerData.getIntArray("unlockedEnchantments")));
        this.unlockedEcnahntments = enchantments;
    }
    
    @Override
    public void init (Entity entity, World world) {
    
    }
    
    /**
     * Synchronises all property data between the client and server.
     */
    public void sync () {
        
        if (this.player instanceof EntityPlayerMP)
            EnchantingPlus.network.sendTo(new PacketSyncPlayerProperties(this), (EntityPlayerMP) player);
    }
    
    /**
     * Retrieves an instance of PlayerProperties from a Player.
     * 
     * @param player: The player to grab the properties from.
     * @return PlayerProperties: An instance of PlayerProperties that is specific to the
     *         specified player.
     */
    public static PlayerProperties getProperties (EntityPlayer player) {
        
        return (PlayerProperties) player.getExtendedProperties(PROP_NAME);
    }
    
    /**
     * Sets a new instance of PlayerProperties to a player.
     * 
     * @param player: The player to sent the properties to.
     * @return PlayerProperties: The newly created instance of PlayerProperties.
     */
    public static PlayerProperties setProperties (EntityPlayer player) {
        
        player.registerExtendedProperties(PROP_NAME, new PlayerProperties(player));
        return getProperties(player);
    }
    
    /**
     * A check to see if an EntityPlayer has an instance of PlayerProperties associated with it
     * or not.
     * 
     * @param player: The EntityPlayer to check for PlayerProperties.
     * @return boolean: Whether or not the EntityPlayer has an instance of PlayerProperties.
     */
    public static boolean hasProperties (EntityPlayer player) {
        
        return getProperties(player) != null;
    }
    
    /**
     * Copies all of the properties of this PlayerProperties instance to the passed
     * PlayerProperties instance. Used to by the clone event to prevent data from being deleted
     * or corrupted.
     * 
     * @param properties: A new instance of PlayerProperties which is set to replace this
     *            PlayerProperties instance.
     */
    public void copy (PlayerProperties properties) {
        
        properties.unlockedEcnahntments = this.unlockedEcnahntments;
    }
}