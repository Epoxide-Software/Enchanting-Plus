package net.darkhax.eplus.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.darkhax.bookshelf.inventory.SlotArmor;
import net.darkhax.bookshelf.lib.util.EnchantmentUtils;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.darkhax.bookshelf.lib.util.Utilities;
import net.darkhax.eplus.common.PlayerHandler;
import net.darkhax.eplus.handler.ConfigurationHandler;
import net.darkhax.eplus.handler.ContentHandler;
import net.darkhax.eplus.libs.Constants;
import net.darkhax.eplus.tileentity.TileEntityAdvancedTable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ContainerAdvancedTable extends Container {
    
    private final World world;
    private final TileEntityAdvancedTable tileEnchantTable;
    private final BlockPos pos;
    private final EntityPlayer player;
    public final IInventory tableInventory = new SlotEnchantTable(this, "Enchant", true, 1);
    private Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
    
    public ContainerAdvancedTable(final InventoryPlayer player, World world, BlockPos pos, TileEntityAdvancedTable table) {
        
        this.world = world;
        this.pos = pos;
        this.tileEnchantTable = table;
        this.player = player.player;
        
        final int guiOffest = 26;
        
        // Item input
        this.addSlotToContainer(new SlotEnchant(this, this.tableInventory, 0, 11 + guiOffest, 17));
        
        // Player Inventory
        for (int invRow = 0; invRow < 3; invRow++)
            for (int slotCount = 0; slotCount < 9; slotCount++)
                this.addSlotToContainer(new Slot(player, slotCount + invRow * 9 + 9, 17 + slotCount * 18 + guiOffest, 91 + invRow * 18));
                
        // Hotbar
        for (int slotCount = 0; slotCount < 9; slotCount++)
            this.addSlotToContainer(new Slot(player, slotCount, 17 + slotCount * 18 + guiOffest, 149));
            
        // Armor Slots
        for (int slotIndex = 0; slotIndex < 4; slotIndex++)
            this.addSlotToContainer(new SlotArmor(player.player, Utilities.getEquipmentSlot(slotIndex), player, 39 - slotIndex, 7, 24 + slotIndex * 19));
            
        // TODO drop items
        /*
         * if (tileEnchantTable.itemInTable != null) {
         * player.entityDropItem(tileEnchantTable.itemInTable, 0.2f);
         * tileEnchantTable.itemInTable.stackSize = 0; }
         */
    }
    
    public float bookCases () {
        
        final int x = this.pos.getX();
        final int y = this.pos.getY();
        final int z = this.pos.getZ();
        
        float cost = ConfigurationHandler.bonusShelves;
        
        for (int zOffset = -1; zOffset <= 1; zOffset++)
            for (int xOffset = -1; xOffset <= 1; xOffset++)
                if ((zOffset != 0 || xOffset != 0) && this.world.isAirBlock(new BlockPos(x + xOffset, y, z + zOffset)) && this.world.isAirBlock(new BlockPos(x + xOffset, y + 1, z + zOffset))) {
                    
                    cost += ForgeHooks.getEnchantPower(this.world, new BlockPos(x + xOffset * 2, y, z + zOffset * 2));
                    cost += ForgeHooks.getEnchantPower(this.world, new BlockPos(x + xOffset * 2, y + 1, z + zOffset * 2));
                    
                    if (xOffset != 0 && zOffset != 0) {
                        cost += ForgeHooks.getEnchantPower(this.world, new BlockPos(x + xOffset * 2, y, z + zOffset));
                        cost += ForgeHooks.getEnchantPower(this.world, new BlockPos(x + xOffset * 2, y + 1, z + zOffset));
                        cost += ForgeHooks.getEnchantPower(this.world, new BlockPos(x + xOffset, y, z + zOffset * 2));
                        cost += ForgeHooks.getEnchantPower(this.world, new BlockPos(x + xOffset, y + 1, z + zOffset * 2));
                    }
                }
                
        return cost * 2;
    }
    
    @Override
    public boolean canInteractWith (EntityPlayer entityPlayer) {
        
        return entityPlayer.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D && !entityPlayer.isDead;
    }
    
    /**
     * Checks if a player can purchase an enchantment through the table. This will only return
     * true if the player is in creative mode, or they have enough EXP to buy it. If the
     * bookshelf requirement is turned on in configs, they must also have the amount of
     * required bookshelves.
     * 
     * @param player The player that is buying.
     * @param cost The cost of the enchantments.
     * @return boolean Whether or not they can afford the cost.
     */
    public boolean canPurchase (EntityPlayer player, int cost) {
        
        if (player.capabilities.isCreativeMode)
            return true;
            
        final int levelCost = EnchantmentUtils.getLevelsFromExperience(cost);
        
        if (ConfigurationHandler.needsBookShelves)
            if (levelCost > this.bookCases()) {
                
                player.addChatMessage(new TextComponentTranslation("chat.eplus.morebooks" + " " + levelCost));
                return false;
            }
            
        if (player.experienceLevel < levelCost) {
            
            player.addChatMessage(new TextComponentTranslation("chat.eplus.morelevels" + " " + levelCost));
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
    public int getRebate (Enchantment enchantment, int enchantmentLevel, Integer existingLevel) {
        
        final ItemStack stack = this.tableInventory.getStackInSlot(0);
        
        if (!ItemStackUtils.isValidStack(stack) && enchantmentLevel > enchantment.getMaxLevel())
            return 0;
            
        final int oldCost = (int) ((enchantment.getMaxEnchantability(existingLevel) - stack.getItem().getItemEnchantability(stack)) / 2 * ConfigurationHandler.costFactor);
        final int newCost = (int) ((enchantment.getMaxEnchantability(enchantmentLevel) - stack.getItem().getItemEnchantability(stack)) / 2 * ConfigurationHandler.costFactor);
        final int returnAmount = (oldCost - newCost) / 2;
        return -EnchantmentUtils.getExperienceFromLevel(returnAmount > 0 ? returnAmount : 0);
    }
    
    /**
     * Updates the ItemStack in the enchantment slot. This method handles the update process
     * and charges the player.
     *
     * @param player: The player enchanting the item.
     * @param map: A map of Integer. Used to hold the the level for the various enchantments.
     * @param cost: The cost for the current update.
     */
    public void updateItemStack (EntityPlayer player, HashMap<Enchantment, Integer> map, int cost) {
        
        final ItemStack itemstack = this.tableInventory.getStackInSlot(0);
        final ArrayList<Enchantment> toRemove = new ArrayList<Enchantment>();
        
        int serverCost = 0;
        
        if (itemstack == null)
            return;
            
        for (final Enchantment enchantment : map.keySet()) {
            
            final Integer level = map.get(enchantment);
            final Integer startingLevel = this.enchantments.get(enchantment);
            
            if (level > startingLevel)
                serverCost += this.enchantmentCost(enchantment, level, startingLevel);
                
            else if (level < startingLevel)
                serverCost += this.getRebate(enchantment, level, startingLevel);
        }
        
        if (cost != serverCost) {
            
            Constants.LOG.warn(player.getDisplayNameString() + " tried to enchant " + itemstack.getDisplayName() + " but the costs were not in sync!");
            return;
        }
        
        for (final Enchantment enchantment : this.enchantments.keySet()) {
            
            final Integer level = this.enchantments.get(enchantment);
            
            if (level != 0)
                if (!map.containsKey(enchantment))
                    map.put(enchantment, level);
        }
        
        for (final Enchantment enchantment : map.keySet()) {
            
            final Integer level = map.get(enchantment);
            
            if (level == 0)
                toRemove.add(enchantment);
        }
        
        for (final Enchantment enchantment : toRemove)
            map.remove(enchantment);
            
        if (this.canPurchase(player, serverCost)) {
            
            final List<EnchantmentData> enchantmentDataList = new ArrayList<EnchantmentData>();
            
            for (final Enchantment enchantment : map.keySet())
                enchantmentDataList.add(new EnchantmentData(enchantment, map.get(enchantment)));
                
            if (!player.capabilities.isCreativeMode)
                if (serverCost < 0)
                    player.addExperience(-serverCost);
                    
                else
                    player.addExperienceLevel(-EnchantmentUtils.getLevelsFromExperience(serverCost));
                    
            final ItemStack itemStack = updateEnchantments(enchantmentDataList, itemstack, player, cost);
            this.tableInventory.setInventorySlotContents(0, itemStack);
        }
        
        this.onCraftMatrixChanged(this.tableInventory);
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
        
        final ItemStack itemStack = this.tableInventory.getStackInSlot(0);
        
        if (itemStack == null || enchantmentLevel > enchantment.getMaxLevel())
            return 0;
            
        return EnchantmentUtils.getExperienceFromLevel(calculateEnchantmentCost(enchantment, enchantmentLevel + level, itemStack));
    }
    
    public Map<Enchantment, Integer> getEnchantments () {
        
        return this.enchantments;
    }
    
    @Override
    public void onContainerClosed (EntityPlayer player) {
        
        super.onContainerClosed(player);
        
        for (int i = 0; i < this.tableInventory.getSizeInventory(); i++) {
            final ItemStack stack = this.tableInventory.getStackInSlot(i);
            
            if (ItemStackUtils.isValidStack(stack))
                if (!player.inventory.addItemStackToInventory(stack))
                    player.entityDropItem(stack, 0.2f);
        }
    }
    
    @Override
    public void onCraftMatrixChanged (IInventory par1IInventory) {
        
        super.onCraftMatrixChanged(par1IInventory);
        
        final IBlockState state = this.world.getBlockState(this.pos);
        this.world.notifyBlockUpdate(this.pos, state, state, 8);
        this.readItems();
    }
    
    /**
     * Will read the enchantments on the items and ones the can be added to the items
     */
    private void readItems () {
        
        final ItemStack itemStack = this.tableInventory.getStackInSlot(0);
        
        final HashMap<Enchantment, Integer> temp = new LinkedHashMap<Enchantment, Integer>();
        final HashMap<Enchantment, Integer> temp2 = new LinkedHashMap<Enchantment, Integer>();
        
        if (ItemStackUtils.isValidStack(itemStack) || ContentHandler.isItemBlacklisted(itemStack.getItem())) {
            
            this.enchantments = temp;
            return;
        }
        
        if (!ConfigurationHandler.allowUnownedModifications && !hasRestriction(itemStack) && itemStack.isItemEnchanted() || ConfigurationHandler.secureItems && hasRestriction(itemStack) && !isValidOwner(itemStack, this.player))
            return;
            
        if (EnchantmentUtils.isItemEnchantable(itemStack))
            this.addAllEnchatments(itemStack, temp);
            
        else if (itemStack.isItemEnchanted()) {
            
            temp.putAll(EnchantmentHelper.getEnchantments(itemStack));
            
            for (final Enchantment potentialEnchant : Enchantment.REGISTRY) {
                
                if (potentialEnchant == null)
                    continue;
                    
                for (final Enchantment existingEnchant : temp.keySet())
                    if (existingEnchant != null && EnchantmentUtils.areEnchantmentsCompatible(existingEnchant, potentialEnchant))
                        this.addEnchantment(itemStack, temp2, potentialEnchant);
            }
            
            temp.putAll(temp2);
        }
        
        if (this.enchantments != temp)
            this.enchantments = temp;
    }
    
    private void addAllEnchatments (ItemStack itemStack, HashMap<Enchantment, Integer> validEnchantments) {
        
        for (final Enchantment enchantment : Enchantment.REGISTRY)
            this.addEnchantment(itemStack, validEnchantments, enchantment);
    }
    
    private void addEnchantment (ItemStack itemStack, HashMap<Enchantment, Integer> validEnchantments, Enchantment enchantment) {
        
        if (isEnchantmentValid(enchantment, this.player) && !ContentHandler.isEnchantmentBlacklisted(enchantment) && (itemStack.getItem() == Items.BOOK || itemStack.getItem() == Items.ENCHANTED_BOOK || enchantment.canApplyAtEnchantingTable(itemStack)))
            validEnchantments.put(enchantment, 0);
    }
    
    public void repair (EntityPlayer player, int cost) throws Exception {
        
        player.addStat(ContentHandler.achievementRepair);
        final ItemStack itemStack = this.tableInventory.getStackInSlot(0);
        
        if (itemStack == null)
            return;
            
        final boolean flag = !itemStack.hasTagCompound() || !itemStack.getTagCompound().hasKey("charge");
        
        if ((!itemStack.isItemEnchanted() || cost == 0) && flag)
            return;
            
        if (this.canPurchase(player, cost)) {
            
            final int maxCost = this.getRepairCost();
            final double percAmnt = cost / (double) maxCost;
            
            final int remain = itemStack.getItemDamage();
            double newDamage = remain - remain * percAmnt;
            newDamage = newDamage <= 0 ? 0 : newDamage;
            
            itemStack.setItemDamage((int) newDamage);
            
            if (!player.capabilities.isCreativeMode)
                player.addExperienceLevel(-cost);
        }
        
        this.onCraftMatrixChanged(this.tableInventory);
    }
    
    public int getRepairCost () {
        
        final ItemStack itemStack = this.tableInventory.getStackInSlot(0);
        int cost = 0;
        
        if (itemStack == null || !itemStack.isItemEnchanted() || !itemStack.isItemDamaged())
            return cost;
            
        final Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
        
        for (final Enchantment enchantment : enchantments.keySet()) {
            
            final Integer level = enchantments.get(enchantment);
            cost += this.enchantmentCost(enchantment, level, 0);
        }
        
        int enchantability = itemStack.getItem().getItemEnchantability(itemStack);
        
        if (enchantability <= 1)
            enchantability = 10;
            
        final double percentDamage = 1 - (itemStack.getMaxDamage() - itemStack.getItemDamage()) / (double) itemStack.getMaxDamage();
        
        double totalCost = percentDamage * cost / enchantability;
        
        totalCost *= 2 * ConfigurationHandler.repairFactor;
        
        return (int) Math.max(1, totalCost);
    }
    
    @Override
    public ItemStack transferStackInSlot (EntityPlayer entityPlayer, int slotIndex) {
        
        ItemStack itemStack = null;
        final Slot slot = this.inventorySlots.get(slotIndex);
        
        if (slot != null && slot.getHasStack()) {
            
            final ItemStack slotStack = slot.getStack();
            final ItemStack tempStack = slotStack.copy();
            itemStack = slotStack.copy();
            tempStack.stackSize = 1;
            
            if (slotIndex != 0) {
                
                final Slot firstSlot = this.inventorySlots.get(0);
                
                if (!firstSlot.getHasStack() && firstSlot.isItemValid(tempStack) && this.mergeItemStack(tempStack, 0, 1, false)) {
                    
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
    
    /**
     * Checks if an enchantment is valid and can be applied. Checks if quest mode is on and
     * that player has unlocked the enchantment.
     * 
     * @param enchantment The enchantment to check for.
     * @param player The player to check for.
     * @return boolean Whether or not the enchantment is valid.
     */
    public static boolean isEnchantmentValid (Enchantment enchantment, EntityPlayer player) {
        
        return enchantment != null && ((ConfigurationHandler.useQuestMode ? PlayerHandler.knowsEnchantment(player, enchantment) : true) || player.capabilities.isCreativeMode);
    }
    
    /**
     * Updates the enchantments of an ItemStack.
     * 
     * @param enchantmentData: A List of EnchantmentData being set to the ItemStack.
     * @param itemStack: The ItemStack being updated.
     * @param player: The player doing the enchanting.
     * @param cost: The cost of the enchanting.
     * @return ItemStack: The enchanted ItemStack.
     */
    public static ItemStack updateEnchantments (List<EnchantmentData> enchantmentData, ItemStack itemStack, EntityPlayer player, int cost) {
        
        if (hasRestriction(itemStack) && !isValidOwner(itemStack, player))
            return itemStack;
            
        // TODO enchantmentData = BookshelfHooks.onItemEnchanted(player, itemStack, cost,
        // enchantmentData);
        
        final NBTTagList nbttaglist = new NBTTagList();
        
        for (final EnchantmentData data : enchantmentData) {
            
            final NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setString("id", data.enchantmentobj.getRegistryName().toString());
            nbttagcompound.setInteger("lvl", data.enchantmentLevel);
            nbttaglist.appendTag(nbttagcompound);
        }
        
        if (itemStack.getItem() == Items.BOOK)
            itemStack = new ItemStack(Items.ENCHANTED_BOOK);
            
        if (itemStack.getItem() == Items.ENCHANTED_BOOK) {
            
            if (nbttaglist.tagCount() > 0)
                itemStack.setTagInfo("StoredEnchantments", nbttaglist);
                
            else if (itemStack.hasTagCompound()) {
                
                itemStack.getTagCompound().removeTag("StoredEnchantments");
                itemStack.setTagCompound(new NBTTagCompound());
                itemStack = new ItemStack(Items.BOOK);
            }
        }
        else if (nbttaglist.tagCount() > 0)
            itemStack.setTagInfo("ench", nbttaglist);
            
        else if (itemStack.hasTagCompound()) {
            
            itemStack.getTagCompound().removeTag("ench");
            itemStack.getTagCompound().removeTag("enchantedOwnerUUID");
        }
        
        return itemStack;
    }
    
    /**
     * Checks to see if an ItemStack has a restriction set on it. A restriction is classified
     * as a populated enchantedOwnerUUID tag.
     * 
     * @param itemStack: The ItemStack to check.
     * @return boolean: Whether or not the passed ItemStack has a restriction on it.
     */
    public static boolean hasRestriction (ItemStack itemStack) {
        
        if (itemStack.hasTagCompound()) {
            
            final String enchantedOwner = itemStack.getTagCompound().getString("enchantedOwnerUUID");
            return !enchantedOwner.equals("");
        }
        
        return false;
    }
    
    /**
     * Checks to see if a player is the valid owner of an ItemStack.
     * 
     * @param itemStack: The ItemStack to check against.
     * @param player: The player to check.
     * @return boolean: Whether or not the player passed is a valid owner for the ItemStack.
     */
    public static boolean isValidOwner (ItemStack itemStack, EntityPlayer player) {
        
        final String enchantedOwner = itemStack.getTagCompound().getString("enchantedOwnerUUID");
        return player.getUniqueID().toString().equals(enchantedOwner);
    }
    
    /**
     * Calculates the amount of levels that an enchantment should cost. This factors in the
     * enchantability of the enchantment, the level of the enchantment, the enchantability of
     * the Item, and the enchantment factor from the config. If the ItemStack passed already
     * has the enchantment on it, the cost will be adjusted to an upgrade price.
     *
     * @param enchant: The enchantment being applied.
     * @param level: The level of the enchantment being applied.
     * @param stack: The ItemStack being enchanted.
     * @return int: The amount of experience levels that should be charged for the enchantment.
     */
    public static int calculateEnchantmentCost (Enchantment enchant, int level, ItemStack stack) {
        
        final int existingLevel = EnchantmentHelper.getEnchantmentLevel(enchant, stack);
        int enchantability = enchant.getMaxEnchantability(level);
        
        if (existingLevel > 0 && existingLevel != level)
            enchantability -= enchant.getMaxEnchantability(existingLevel);
            
        return (int) ((enchantability - stack.getItem().getItemEnchantability(stack)) / 2 * ConfigurationHandler.costFactor);
    }
}