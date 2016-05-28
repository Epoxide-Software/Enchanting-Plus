package net.darkhax.eplus.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.common.io.Files;

import net.darkhax.eplus.libs.Constants;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class PlayerHandler {
    
    /**
     * A map of all unlocked enchantment data. The key is the UUID of the player, and the value
     * is a List of Enchantment which have been unlocked.
     */
    private static HashMap<UUID, List<Enchantment>> playerEnchantments = new HashMap<UUID, List<Enchantment>>();
    
    /**
     * Clears all enchantments that are unlocked for a specific player.
     * 
     * @param player The EntityPlayer to clear the data of.
     * @return List<Enchantment> A list of the cleared enchantments.
     */
    public static List<Enchantment> clearEnchantments (EntityPlayer player) {
        
        return playerEnchantments.remove(player.getUniqueID());
    }
    
    /**
     * Gets all enchantments that are unlocked for a specific player.
     * 
     * @param player The EntityPlayer to get the unlocked enchantments of.
     * @return List<Enchantment> A list of the cleared enchantments.
     */
    public static List<Enchantment> getEnchantments (EntityPlayer player) {
        
        if (!playerEnchantments.containsKey(player.getUniqueID()))
            playerEnchantments.put(player.getUniqueID(), new ArrayList<Enchantment>());
            
        return playerEnchantments.get(player.getUniqueID());
    }
    
    /**
     * Checks if a player knows an enchantment.
     * 
     * @param player The player to check for.
     * @param enchant The enchantment to check for.
     * @return boolean Whether or not the player has access to the enchantment.
     */
    public static boolean knowsEnchantment (EntityPlayer player, Enchantment enchant) {
        
        return getEnchantments(player).contains(enchant);
    }
    
    /**
     * Loads the data for a player. Attempts to load from the dataFile, if that fails a backup
     * data file will be used. If the backup file is loaded, an attempt will be made to save
     * the file again.
     * 
     * @param player The player to load data for.
     * @param dataFile The data file to load data from.
     * @param backupData The backup data file, in case of emergency.
     */
    public static void loadPlayerData (EntityPlayer player, File dataFile, File backupData) {
        
        if (player != null && !player.worldObj.isRemote)
            try {
                
                NBTTagCompound dataTag = loadPlayerFile(player, dataFile, false);
                boolean shouldSave = false;
                
                if (dataTag == null || dataTag.hasNoTags()) {
                    
                    Constants.LOG.warn("Attempting to load backup data for " + player.getDisplayNameString());
                    dataTag = loadPlayerFile(player, backupData, true);
                    shouldSave = true;
                }
                
                if (dataTag != null && dataTag.hasKey("UnlockedEnchants")) {
                    
                    final List<Enchantment> enchantments = new ArrayList<Enchantment>();
                    final NBTTagList enchIDs = dataTag.getTagList("UnlockedEnchants", 8);
                    
                    for (int index = 0; index < enchIDs.tagCount(); index++) {
                        
                        final Enchantment enchantment = Enchantment.getEnchantmentByLocation(enchIDs.getStringTagAt(index));
                        
                        if (enchantment != null && !enchantments.contains(enchantment))
                            enchantments.add(enchantment);
                    }
                    
                    playerEnchantments.put(player.getUniqueID(), enchantments);
                    
                    if (shouldSave)
                        savePlayerData(player, dataFile, backupData);
                }
            }
            catch (final Exception exception) {
                
                Constants.LOG.fatal("Could not load data for " + player.getDisplayNameString());
                exception.printStackTrace();
            }
    }
    
    /**
     * Loads a player NBTTagCompound data file for a player.
     * 
     * @param player The EntityPlayer to load data for.
     * @param dataFile The file to load the data from.
     * @param isBackup Whether or not a data backup is being loaded.
     * @return NBTTagCompound The data tag that was read.
     */
    public static NBTTagCompound loadPlayerFile (EntityPlayer player, File dataFile, boolean isBackup) {
        
        try {
            
            final FileInputStream fileStream = new FileInputStream(dataFile);
            final NBTTagCompound dataTag = CompressedStreamTools.readCompressed(fileStream);
            fileStream.close();
            return dataTag;
        }
        
        catch (final Exception exception) {
            
            Constants.LOG.warn("Error loading " + (isBackup ? "backup" : "primary") + " player data for " + player.getDisplayNameString());
            exception.printStackTrace();
            return null;
        }
    }
    
    /**
     * Locks an enchantment by removing it from the players list of unlocked enchantments.
     * 
     * @param player The player to lock the enchantment for.
     * @param enchant The enchantment to lock.
     */
    public static void lockEnchantment (EntityPlayer player, Enchantment enchant) {
        
        getEnchantments(player).remove(enchant);
    }
    
    /**
     * Saves data for a player to a file. This will save the file to disk, and attempt to save
     * it to a backup in case the save fails.
     * 
     * @param player The player to save data for.
     * @param dataFile The data file to save to.
     * @param backupData The backup data file to save to.
     */
    public static void savePlayerData (EntityPlayer player, File dataFile, File backupData) {
        
        if (player != null && !player.worldObj.isRemote)
            try {
                
                if (dataFile != null && dataFile.exists())
                    try {
                        
                        Files.copy(dataFile, backupData);
                    }
                    
                    catch (final Exception exception) {
                        
                        Constants.LOG.warn("Could not write backup file for " + player.getDisplayNameString());
                    }
                    
                try {
                    if (dataFile != null) {
                        
                        final NBTTagCompound dataTag = new NBTTagCompound();
                        final NBTTagList enchantments = new NBTTagList();
                        
                        for (final Enchantment enchant : getEnchantments(player))
                            enchantments.appendTag(new NBTTagString(enchant.getRegistryName().toString()));
                            
                        dataTag.setTag("UnlockedEnchants", enchantments);
                        
                        final FileOutputStream fileStream = new FileOutputStream(dataFile);
                        CompressedStreamTools.writeCompressed(dataTag, fileStream);
                        fileStream.close();
                    }
                }
                
                catch (final Exception exception) {
                    
                    Constants.LOG.warn("Could not save data file for " + player.getDisplayNameString());
                    exception.printStackTrace();
                    
                    if (dataFile.exists())
                        dataFile.delete();
                }
            }
            
            catch (final Exception exception) {
                
                Constants.LOG.fatal("Data saving failed for " + player.getDisplayNameString());
                exception.printStackTrace();
            }
    }
    
    /**
     * Unlocks an enchantment for a player.
     * 
     * @param player The player to unlock the enchantment for.
     * @param enchant The enchantment to ublock.
     */
    public static void unlockEnchantment (EntityPlayer player, Enchantment enchant) {
        
        final List<Enchantment> enchants = getEnchantments(player);
        
        if (!enchants.contains(enchants))
            enchants.add(enchant);
    }
}