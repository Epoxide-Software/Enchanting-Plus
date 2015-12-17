package net.epoxide.eplus.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeHooks;

import net.darkhax.bookshelf.lib.util.EnchantmentUtils;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.darkhax.bookshelf.lib.util.Utilities;

import net.epoxide.eplus.handler.ConfigurationHandler;
import net.epoxide.eplus.handler.ContentHandler;
import net.epoxide.eplus.lib.Constants;
import net.epoxide.eplus.tileentity.TileEntityEnchantTable;

public class ContainerEnchantTable extends Container {
    
    public final World world;
    private final TileEntityEnchantTable tileEnchantTable;
    private final int x;
    private final int y;
    private final int z;
    private final EntityPlayer player;
    private Map<Integer, Integer> enchantments = new HashMap<Integer, Integer>();
    public final IInventory tableInventory = new SlotEnchantTable(this, "Enchant", true, 1);
    
    public ContainerEnchantTable(final InventoryPlayer inventoryPlayer, World world, int x, int y, int z, TileEntityEnchantTable tileEntityTable) {
        
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        
        this.tileEnchantTable = tileEntityTable;
        
        this.player = inventoryPlayer.player;
        
        int guiOffest = 26;
        
        addSlotToContainer(new SlotEnchant(this, tableInventory, 0, 11 + guiOffest, 17));
        
        for (int l = 0; l < 3; ++l) {
            for (int i1 = 0; i1 < 9; ++i1) {
                addSlotToContainer(new Slot(inventoryPlayer, i1 + l * 9 + 9, 17 + i1 * 18 + guiOffest, 91 + l * 18));
            }
        }
        
        for (int l = 0; l < 9; ++l) {
            addSlotToContainer(new Slot(inventoryPlayer, l, 17 + l * 18 + guiOffest, 149));
        }
        
        for (int k = 0; k < 4; k++) {
            final int armorType = k;
            addSlotToContainer(new Slot(inventoryPlayer, 39 - k, 7, 24 + k * 19) {
                @Override
                public int getSlotStackLimit () {
                    
                    return 1;
                }
                
                @Override
                public boolean isItemValid (ItemStack par1ItemStack) {
                    
                    Item item = (par1ItemStack == null ? null : par1ItemStack.getItem());
                    return item != null && item.isValidArmor(par1ItemStack, armorType, player);
                }
            });
        }
        
        if (tileEnchantTable.itemInTable != null) {
            player.entityDropItem(tileEnchantTable.itemInTable, 0.2f);
            tileEnchantTable.itemInTable.stackSize = 0;
        }
    }
    
    public float bookCases () {
        
        float temp = ConfigurationHandler.minimumBookshelfs;
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                if ((j != 0 || k != 0) && this.world.isAirBlock(this.x + k, this.y, this.z + j) && this.world.isAirBlock(this.x + k, this.y + 1, this.z + j)) {
                    temp += ForgeHooks.getEnchantPower(world, x + k * 2, y, z + j * 2);
                    temp += ForgeHooks.getEnchantPower(world, x + k * 2, y + 1, z + j * 2);
                    
                    if (k != 0 && j != 0) {
                        temp += ForgeHooks.getEnchantPower(world, x + k * 2, y, z + j);
                        temp += ForgeHooks.getEnchantPower(world, x + k * 2, y + 1, z + j);
                        temp += ForgeHooks.getEnchantPower(world, x + k, y, z + j * 2);
                        temp += ForgeHooks.getEnchantPower(world, x + k, y + 1, z + j * 2);
                    }
                }
            }
        }
        
        return temp * 2;
    }
    
    @Override
    public boolean canInteractWith (EntityPlayer entityPlayer) {
        
        return entityPlayer.getDistanceSq((double) this.x + 0.5D, (double) this.y + 0.5D, (double) this.z + 0.5D) <= 64.0D && !entityPlayer.isDead;
    }
    
    public boolean canPurchase (EntityPlayer player, int cost) {
        
        if (player.capabilities.isCreativeMode)
            return true;
            
        int expLevel = EnchantmentUtils.getLevelsFromExperience(cost);
        if (ConfigurationHandler.needsBookShelves) {
            if (expLevel > bookCases()) {
                player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("chat.eplus.morebooks") + " " + expLevel));
                return false;
            }
        }
        
        if (player.experienceLevel < expLevel) {
            player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("chat.eplus.morelevels") + " " + expLevel));
            return false;
        }
        return true;
    }
    
    /**
     * Calculates the amount of experience to give the player for disenchanting their item.
     *
     * @param enchantment: The enchantment being removed.
     * @param enchantmentLevel: The new amount of levels for the enchantment effect.
     * @param existingLevel: The amount of levels for the enchantment effect before updating.
     * @return int: The amount of experience points to give the player.
     */
    public int disenchantmentCost (Enchantment enchantment, int enchantmentLevel, Integer existingLevel) {
        
        final ItemStack itemStack = tableInventory.getStackInSlot(0);
        
        if (!ItemStackUtils.isValidStack(itemStack) && enchantmentLevel > enchantment.getMaxLevel())
            return 0;
            
        final int oldCost = (int) (((enchantment.getMaxEnchantability(existingLevel) - itemStack.getItem().getItemEnchantability(itemStack)) / 2) * ConfigurationHandler.costFactor);
        final int newCost = (int) (((enchantment.getMaxEnchantability(enchantmentLevel) - itemStack.getItem().getItemEnchantability(itemStack)) / 2) * ConfigurationHandler.costFactor);
        final int returnAmount = (oldCost - newCost) / 2;
        return -EnchantmentUtils.getExperienceFromLevel((returnAmount > 0) ? returnAmount : 0);
    }
    
    /**
     * Updates the ItemStack in the enchantment slot. This method handles the update process
     * and charges the player.
     *
     * @param player: The player enchanting the item.
     * @param map: A map of Integer. Used to hold the the level for the various enchantments.
     * @param cost: The cost for the current update.
     */
    public void updateItemStack (EntityPlayer player, HashMap<Integer, Integer> map, int cost) {
        
        final ItemStack itemstack = tableInventory.getStackInSlot(0);
        final ArrayList<Integer> temp = new ArrayList<Integer>();
        int serverCost = 0;
        
        if (itemstack == null)
            return;
            
        for (final Integer enchantId : map.keySet()) {
            
            final Integer level = map.get(enchantId);
            final Integer startingLevel = enchantments.get(enchantId);
            Enchantment enchantment = Utilities.getEnchantment(enchantId);
            if (level > startingLevel)
                serverCost += enchantmentCost(enchantment, level, startingLevel);
            else if (level < startingLevel)
                serverCost += disenchantmentCost(enchantment, level, startingLevel);
        }
        
        if (cost != serverCost) {
            
            Constants.LOG.warn(player.getCommandSenderName() + " tried to enchant " + itemstack.getDisplayName() + " but the costs were not in sync!");
            return;
        }
        
        for (final Integer enchantId : enchantments.keySet()) {
            
            final Integer level = enchantments.get(enchantId);
            
            if (level != 0)
                if (!map.containsKey(enchantId))
                    map.put(enchantId, level);
        }
        
        for (final Integer enchantId : map.keySet()) {
            
            final Integer level = map.get(enchantId);
            
            if (level == 0)
                temp.add(enchantId);
        }
        
        for (Integer object : temp) {
            
            map.remove(object);
        }
        
        if (canPurchase(player, serverCost)) {
            
            List<EnchantmentData> enchantmentDataList = new ArrayList<EnchantmentData>();
            
            for (Integer i : map.keySet())
                enchantmentDataList.add(new EnchantmentData(i, map.get(i)));
                
            if (!player.capabilities.isCreativeMode) {
                
                if (serverCost < 0)
                    player.addExperience(-serverCost);
                    
                else
                    player.addExperienceLevel(-EnchantmentUtils.getLevelsFromExperience(serverCost));
            }
            
            ItemStack itemStack = EnchantHelper.updateEnchantments(enchantmentDataList, itemstack, player, cost);
            tableInventory.setInventorySlotContents(0, itemStack);
        }
        
        onCraftMatrixChanged(tableInventory);
    }
    
    /**
     * Calculates the amount of experience to charge for the upgrade.
     *
     * @param enchantment: The enchantment being bought.
     * @param enchantmentLevel: The existing level of the enchantment.
     * @param level: The amount of levels being added.
     * @return int: The amount of experience being charged for this enchantment.
     */
    public int enchantmentCost (Enchantment enchantment, int enchantmentLevel, Integer level) {
        
        final ItemStack itemStack = tableInventory.getStackInSlot(0);
        
        if (itemStack == null || enchantmentLevel > enchantment.getMaxLevel())
            return 0;
            
        return EnchantmentUtils.getExperienceFromLevel(EnchantHelper.calculateEnchantmentCost(enchantment, enchantmentLevel + level, itemStack));
    }
    
    public Map<Integer, Integer> getEnchantments () {
        
        return enchantments;
    }
    
    @Override
    public void onContainerClosed (EntityPlayer par1EntityPlayer) {
        
        super.onContainerClosed(par1EntityPlayer);
        
        for (int i = 0; i < tableInventory.getSizeInventory(); i++) {
            final ItemStack stack = tableInventory.getStackInSlot(i);
            if (stack != null) {
                if (!par1EntityPlayer.inventory.addItemStackToInventory(stack)) {
                    par1EntityPlayer.entityDropItem(stack, 0.2f);
                }
            }
        }
    }
    
    @Override
    public void onCraftMatrixChanged (IInventory par1IInventory) {
        
        super.onCraftMatrixChanged(par1IInventory);
        
        tileEnchantTable.getWorldObj().markBlockForUpdate(tileEnchantTable.xCoord, tileEnchantTable.yCoord, tileEnchantTable.zCoord);
        readItems();
    }
    
    /**
     * Will read the enchantments on the items and ones the can be added to the items
     */
    private void readItems () {
        
        final ItemStack itemStack = tableInventory.getStackInSlot(0);
        
        final HashMap<Integer, Integer> temp = new LinkedHashMap<Integer, Integer>();
        final HashMap<Integer, Integer> temp2 = new LinkedHashMap<Integer, Integer>();
        
        if (itemStack == null || ContentHandler.isBlacklisted(itemStack.getItem())) {
            enchantments = temp;
            return;
        }
        
        if ((!ConfigurationHandler.allowUnownedModifications && !EnchantHelper.hasRestriction(itemStack) && EnchantmentUtils.isStackEnchanted(itemStack)) || (ConfigurationHandler.secureItems && EnchantHelper.hasRestriction(itemStack) && !EnchantHelper.isValidOwner(itemStack, player)))
            return;
            
        if (EnchantmentUtils.isItemEnchantable(itemStack)) {
            addEnchantsFor(itemStack, temp);
        }
        
        else if (EnchantmentUtils.isStackEnchanted(itemStack) && EnchantHelper.isNewItemEnchantable(itemStack.getItem())) {
            temp.putAll(EnchantmentHelper.getEnchantments(itemStack));
            
            for (final Enchantment enchant : Enchantment.enchantmentsList) {
                if (enchant == null)
                    continue;
                    
                boolean add = true;
                for (final Integer enc : temp.keySet()) {
                    
                    final Enchantment enchantment = Utilities.getEnchantment(enc);
                    if (enchantment == null)
                        continue;
                        
                    if (!EnchantmentUtils.areEnchantmentsCompatible(enchantment, enchant)) {
                        add = false;
                    }
                }
                if (add) {
                    addEnchantFor(itemStack, temp2, enchant);
                }
            }
            temp.putAll(temp2);
        }
        
        if (enchantments != temp) {
            enchantments = temp;
        }
    }
    
    private void addEnchantsFor (ItemStack itemStack, HashMap<Integer, Integer> temp) {
        
        for (final Enchantment obj : Enchantment.enchantmentsList) {
            addEnchantFor(itemStack, temp, obj);
        }
    }
    
    private void addEnchantFor (ItemStack itemStack, HashMap<Integer, Integer> temp, Enchantment obj) {
        
        if (EnchantHelper.isEnchantmentValid(obj, player) && !ContentHandler.isBlacklisted(obj) && (itemStack.getItem() == Items.book || itemStack.getItem() == Items.enchanted_book || obj.canApplyAtEnchantingTable(itemStack))) {
            temp.put(obj.effectId, 0);
        }
    }
    
    public void repair (EntityPlayer player, int cost) throws Exception {
        
        player.triggerAchievement(ContentHandler.achievementRepair);
        final ItemStack itemStack = tableInventory.getStackInSlot(0);
        
        if (itemStack == null)
            return;
            
        boolean flag = !itemStack.hasTagCompound() || !itemStack.getTagCompound().hasKey("charge");
        
        if ((!itemStack.isItemEnchanted() || cost == 0) && flag)
            return;
            
        if (canPurchase(player, cost)) {
            int maxCost = repairCostMax();
            double percAmnt = cost / (double) maxCost;
            
            int remain = itemStack.getItemDamageForDisplay();
            double newDamage = remain - remain * percAmnt;
            newDamage = (newDamage <= 0) ? 0 : newDamage;
            
            itemStack.setItemDamage((int) newDamage);
            if (!player.capabilities.isCreativeMode)
                player.addExperienceLevel(-cost);
                
        }
        
        onCraftMatrixChanged(tableInventory);
    }
    
    public int repairCostMax () {
        
        final ItemStack itemStack = tableInventory.getStackInSlot(0);
        if (itemStack == null)
            return 0;
            
        if (!itemStack.isItemEnchanted() || !itemStack.isItemDamaged())
            return 0;
            
        int cost = 0;
        
        final Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
        
        for (final Integer enchantment : enchantments.keySet()) {
            final Integer enchantmentLevel = enchantments.get(enchantment);
            
            cost += enchantmentCost(Utilities.getEnchantment(enchantment), enchantmentLevel, 0);
        }
        
        final int maxDamage = itemStack.getMaxDamage();
        final int displayDamage = itemStack.getItemDamageForDisplay();
        int enchantability = itemStack.getItem().getItemEnchantability(itemStack);
        
        if (enchantability <= 1) {
            enchantability = 10;
        }
        
        final double percentDamage = 1 - (maxDamage - displayDamage) / (double) maxDamage;
        
        double totalCost = (percentDamage * cost) / enchantability;
        
        totalCost *= 2 * ConfigurationHandler.repairFactor;
        
        return (int) Math.max(1, totalCost);
    }
    
    @Override
    public ItemStack transferStackInSlot (EntityPlayer entityPlayer, int slotIndex) {
        
        ItemStack itemStack = null;
        final Slot slot = (Slot) inventorySlots.get(slotIndex);
        
        if (slot != null && slot.getHasStack()) {
            
            final ItemStack slotStack = slot.getStack();
            final ItemStack tempStack = slotStack.copy();
            itemStack = slotStack.copy();
            tempStack.stackSize = 1;
            
            if (slotIndex != 0) {
                
                final Slot firstSlot = (Slot) inventorySlots.get(0);
                
                if (!firstSlot.getHasStack() && firstSlot.isItemValid(tempStack) && mergeItemStack(tempStack, 0, 1, false)) {
                    
                    slotStack.stackSize--;
                    itemStack = slotStack.copy();
                }
            }
            
            if (slotStack.stackSize == 0)
                slot.putStack(null);
                
            else
                slot.onSlotChanged();
                
            if (itemStack.stackSize == slotStack.stackSize)
                return null;
                
            slot.onPickupFromSlot(entityPlayer, slotStack);
        }
        
        return itemStack;
    }
}
