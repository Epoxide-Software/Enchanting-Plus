package net.darkhax.eplus.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.darkhax.bookshelf.inventory.SlotArmor;
import net.darkhax.bookshelf.lib.util.EnchantmentUtils;
import net.darkhax.bookshelf.lib.util.EntityUtils;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.darkhax.eplus.handler.ConfigurationHandler;
import net.darkhax.eplus.handler.ContentHandler;
import net.darkhax.eplus.handler.PlayerHandler;
import net.darkhax.eplus.libs.Constants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
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
    private final BlockPos pos;
    private final EntityPlayer player;
    private final IInventory tableInventory = new InventoryTable(this, "Enchant", true, 1);
    
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    
    public ContainerAdvancedTable(final InventoryPlayer inventory, World world, BlockPos pos) {
        
        this.world = world;
        this.pos = pos;
        this.player = inventory.player;
        
        final int guiOffest = 26;
        
        // Item input
        this.addSlotToContainer(new SlotEnchant(this, this.tableInventory, 0, 11 + guiOffest, 17));
        
        // Player Inventory
        for (int invRow = 0; invRow < 3; invRow++)
            for (int slotCount = 0; slotCount < 9; slotCount++)
                this.addSlotToContainer(new Slot(inventory, slotCount + invRow * 9 + 9, 17 + slotCount * 18 + guiOffest, 91 + invRow * 18));
            
        // Hotbar
        for (int slotCount = 0; slotCount < 9; slotCount++)
            this.addSlotToContainer(new Slot(inventory, slotCount, 17 + slotCount * 18 + guiOffest, 149));
        
        // Armor Slots
        for (int slotIndex = 0; slotIndex < 4; slotIndex++)
            this.addSlotToContainer(new SlotArmor(inventory.player, EntityUtils.getEquipmentSlot(slotIndex), inventory, 39 - slotIndex, 7, 24 + slotIndex * 19));
    }
    
    @Override
    public boolean canInteractWith (EntityPlayer entityPlayer) {
        
        return entityPlayer.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D && !entityPlayer.isDead;
    }
    
    @Override
    public void onContainerClosed (EntityPlayer player) {
        
        super.onContainerClosed(player);
        
        for (int i = 0; i < this.tableInventory.getSizeInventory(); i++) {
            
            final ItemStack stack = this.tableInventory.getStackInSlot(i);
            
            if (ItemStackUtils.isValidStack(stack))
                if (!player.inventory.addItemStackToInventory(stack))
                    if (!player.worldObj.isRemote)
                        player.entityDropItem(stack, 0.2f);
        }
    }
    
    @Override
    public void onCraftMatrixChanged (IInventory par1IInventory) {
        
        super.onCraftMatrixChanged(par1IInventory);
        
        final IBlockState state = this.world.getBlockState(this.pos);
        this.world.notifyBlockUpdate(this.pos, state, state, 8);
        this.readItemStack();
    }
    
    /**
     * Reads the held ItemStack for enchantment data and then populates the default maps with
     * that info.
     */
    private void readItemStack () {
        
        final ItemStack itemStack = this.tableInventory.getStackInSlot(0);
        
        final HashMap<Enchantment, Integer> temp = new LinkedHashMap<>();
        final HashMap<Enchantment, Integer> temp2 = new LinkedHashMap<>();
        
        if (!ItemStackUtils.isValidStack(itemStack) || ContentHandler.isItemBlacklisted(itemStack.getItem())) {
            
            this.enchantments = temp;
            return;
        }
        
        if (EnchantmentUtils.isItemEnchantable(itemStack))
            this.addAllEnchatments(itemStack, temp);
        
        else if (itemStack.isItemEnchanted()) {
            
            temp.putAll(EnchantmentHelper.getEnchantments(itemStack));
            
            for (final Enchantment potentialEnchant : Enchantment.REGISTRY)
                for (final Enchantment existingEnchant : temp.keySet())
                    if (existingEnchant != null && EnchantmentUtils.areEnchantmentsCompatible(existingEnchant, potentialEnchant))
                        this.addEnchantment(itemStack, temp2, potentialEnchant);
                    
            temp.putAll(temp2);
        }
        
        if (this.enchantments != temp)
            this.enchantments = temp;
    }
    
    public void repair (EntityPlayer player, int cost) {
        
        final ItemStack itemStack = this.tableInventory.getStackInSlot(0);
        
        if (itemStack == null || !itemStack.isItemEnchanted() || cost == 0)
            return;
        
        if (this.canPurchase(player, cost)) {
            
            itemStack.setItemDamage(0);
            
            if (!player.capabilities.isCreativeMode)
                player.addExperience(-cost);
        }
        
        this.onCraftMatrixChanged(this.tableInventory);
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
     * Updates the ItemStack in the enchantment slot. This method handles the update process
     * and charges the player.
     *
     * @param player: The player enchanting the item.
     * @param map: A map of Integer. Used to hold the the level for the various enchantments.
     * @param clientCost: The cost for the current update.
     */
    public void updateItemStack (EntityPlayer player, HashMap<Enchantment, Integer> map, int clientCost) {
        
        final ItemStack itemstack = this.tableInventory.getStackInSlot(0);
        final ArrayList<Enchantment> toRemove = new ArrayList<>();
        
        final int serverCost = this.getTotalEnchantmentCost(map);
        
        if (itemstack == null)
            return;
        
        if (clientCost != serverCost) {
            
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
            
            final List<EnchantmentData> enchantmentDataList = new ArrayList<>();
            
            for (final Enchantment enchantment : map.keySet())
                enchantmentDataList.add(new EnchantmentData(enchantment, map.get(enchantment)));
            
            if (!player.capabilities.isCreativeMode)
                if (serverCost < 0)
                    player.addExperience(-serverCost);
                
                else
                    player.addExperienceLevel(-EnchantmentUtils.getLevelsFromExperience(serverCost));
                
            this.tableInventory.setInventorySlotContents(0, this.applyChanges(enchantmentDataList, itemstack, player, clientCost));
        }
        
        this.onCraftMatrixChanged(this.tableInventory);
    }
    
    /**
     * Calculates the total enchantment cost.
     * 
     * @param enchants The enchantments being applied.
     * @return The total cost.
     */
    private int getTotalEnchantmentCost (Map<Enchantment, Integer> enchants) {
        
        int cost = 0;
        
        for (final Enchantment enchantment : enchants.keySet()) {
            
            final Integer level = enchants.get(enchantment);
            final Integer startingLevel = this.enchantments.get(enchantment);
            
            if (level > startingLevel)
                cost += this.enchantmentCost(enchantment, level, startingLevel);
            
            else if (level < startingLevel)
                cost += this.getRebate(enchantment, level, startingLevel);
        }
        
        return cost;
    }
    
    /**
     * Calculates the amount of levels that an enchantment should cost. This factors in the
     * enchantability of the enchantment, the level of the enchantment, the enchantability of
     * the Item, and the enchantment factor from the config. If the ItemStack passed already
     * has the enchantment on it, the cost will be adjusted to an upgrade price.
     *
     * @param enchantment: The enchantment being applied.
     * @param level: The level of the enchantment being applied.
     * @param stack: The ItemStack being enchanted.
     * @return int: The amount of experience levels that should be charged for the enchantment.
     */
    public static int calculateEnchantmentCost (Enchantment enchantment, int level) {
        
        int cost = (int) Math.floor(Math.max(1F, 1F + 2F * level * ((float) level / enchantment.getMaxLevel()) + (10 - enchantment.getRarity().getWeight()) * 0.2F));
        cost = cost + (int) (cost * ConfigurationHandler.costFactor);
        cost = cost + (enchantment.getRarity() == Rarity.COMMON ? 1 : enchantment.getRarity() == Rarity.UNCOMMON ? 5 : enchantment.getRarity() == Rarity.RARE ? 10 : 20);
        return cost;
    }
    
    /**
     * Checks if an enchantment is valid and can be applied. Checks if quest mode is on and
     * that player has unlocked the enchantment.
     * 
     * @param enchantment The enchantment to check for.
     * @param player The player to check for.
     * @return boolean Whether or not the enchantment is valid.
     */
    private boolean isEnchantmentValid (Enchantment enchantment, EntityPlayer player) {
        
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
    private ItemStack applyChanges (List<EnchantmentData> enchantmentData, ItemStack itemStack, EntityPlayer player, int cost) {
        
        final NBTTagList nbttaglist = new NBTTagList();
        
        for (final EnchantmentData data : enchantmentData) {
            
            final NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setShort("id", (short) Enchantment.getEnchantmentID(data.enchantmentobj));
            nbttagcompound.setInteger("lvl", data.enchantmentLevel);
            nbttaglist.appendTag(nbttagcompound);
        }
        
        if (itemStack.getItem() == Items.BOOK)
            itemStack.setItem(Items.ENCHANTED_BOOK);
        
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
        
        else if (itemStack.hasTagCompound())
            itemStack.getTagCompound().removeTag("ench");
        
        return itemStack;
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
        
        if (levelCost > this.getEnchantingPower()) {
            
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
        
        return EnchantmentUtils.getExperienceFromLevel(calculateEnchantmentCost(enchantment, enchantmentLevel + level));
    }
    
    /**
     * Returns the list of enchantments to display.
     * 
     * @return The list of enchantments to display.
     */
    public Map<Enchantment, Integer> getEnchantments () {
        
        return this.enchantments;
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
     * Calculates the EXP cost to repair the held ItemStack.
     * 
     * @return The amount of EXP to repair the held stack.
     */
    public int getRepairCost () {
        
        final ItemStack stack = this.tableInventory.getStackInSlot(0);
        int cost = 0;
        
        if (!ItemStackUtils.isValidStack(stack) || !stack.isItemEnchanted() || !stack.isItemDamaged())
            return cost;
        
        final Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
        
        for (final Enchantment enchantment : map.keySet()) {
            
            final Integer level = map.get(enchantment);
            cost += this.enchantmentCost(enchantment, level, 0);
        }
        
        final float repairPercent = (float) stack.getItemDamage() / (float) stack.getMaxDamage();
        
        return Math.max(1, (int) (Math.abs(cost) * repairPercent));
    }
    
    /**
     * Attempts to add all enchantments to the list of enchantments. If the enchantment is not
     * valid, this will fail.
     * 
     * @param itemStack The stack to show enchantments for.
     * @param validEnchantments The list of valid enchantments.
     */
    private void addAllEnchatments (ItemStack itemStack, HashMap<Enchantment, Integer> validEnchantments) {
        
        for (final Enchantment enchantment : Enchantment.REGISTRY)
            this.addEnchantment(itemStack, validEnchantments, enchantment);
    }
    
    /**
     * Attempts to an an enchantment to the list of enchantments. If the enchantment is not
     * valid, this will fail.
     * 
     * @param itemStack The item stack to add.
     * @param validEnchantments The list of existing enchantments.
     * @param enchantment The enchantment to add.
     */
    private void addEnchantment (ItemStack itemStack, HashMap<Enchantment, Integer> validEnchantments, Enchantment enchantment) {
        
        if (this.isEnchantmentValid(enchantment, this.player) && !ContentHandler.isEnchantmentBlacklisted(enchantment) && (itemStack.getItem() == Items.BOOK || itemStack.getItem() == Items.ENCHANTED_BOOK || enchantment.canApplyAtEnchantingTable(itemStack)))
            validEnchantments.put(enchantment, EnchantmentHelper.getEnchantmentLevel(enchantment, itemStack));
    }
    
    /**
     * Gets the item held it the table inventory.
     * 
     * @return The item in the table inventory.
     */
    public ItemStack getItem () {
        
        return this.tableInventory.getStackInSlot(0);
    }
    
    /**
     * Calculates the enchantment power in the surrounding blocks.
     * 
     * @return The total enchanting power in the nearby blocks.
     */
    public float getEnchantingPower () {
        
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
}