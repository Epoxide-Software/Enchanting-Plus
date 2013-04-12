package eplus.inventory;

import eplus.helper.EnchantHelper;
import eplus.lib.ConfigurationSettings;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ContainerEnchantTable extends Container {

    public final World worldObj;
    private final int xPos;
    private final int yPos;
    private final int zPos;
    public IInventory tableInventory = new SlotEnchantTable(this, "Enchant", true, 1);
    TileEnchantTable tileEnchantTable;
    private Map<Integer, Integer> enchantments;

    public ContainerEnchantTable(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5, TileEnchantTable tileEntity)
    {
        this.worldObj = par2World;
        this.xPos = par3;
        this.yPos = par4;
        this.zPos = par5;

        this.tileEnchantTable = tileEntity;

        this.addSlotToContainer(new SlotEnchant(this, this.tableInventory, 0, 11, 31));
        int l;

        for (l = 0; l < 3; ++l) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, i1 + l * 9 + 9, 17 + i1 * 18, 91 + l * 18));
            }
        }

        for (l = 0; l < 9; ++l) {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, l, 17 + l * 18, 149));
        }

        this.putStackInSlot(0, tileEntity.itemInTable);
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return true;
    }

    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        super.onCraftMatrixChanged(par1IInventory);
        tileEnchantTable.getWorldObj().markBlockForRenderUpdate(tileEnchantTable.xCoord, tileEnchantTable.yCoord, tileEnchantTable.zCoord);

        readItems();
    }

    public Map<Integer, Integer> getEnchantments()
    {
        return enchantments;
    }

    @Override
    public void onCraftGuiClosed(EntityPlayer par1EntityPlayer)
    {
        super.onCraftGuiClosed(par1EntityPlayer);

        tileEnchantTable.itemInTable = tableInventory.getStackInSlot(0);
        tileEnchantTable.getWorldObj().markBlockForRenderUpdate(tileEnchantTable.xCoord, tileEnchantTable.yCoord, tileEnchantTable.zCoord);
    }

    /**
     * Will read the enchantments on the items and ones the can be added to the items
     */
    @SuppressWarnings("unchecked")
    private void readItems()
    {
        ItemStack itemStack = this.tableInventory.getStackInSlot(0);

        HashMap<Integer, Integer> temp = new LinkedHashMap<Integer, Integer>();

        if (itemStack != null) {
            tileEnchantTable.itemInTable = itemStack.copy();
            if (EnchantHelper.isItemEnchanted(itemStack)) {
                temp.putAll(EnchantmentHelper.getEnchantments(itemStack));
            }
            if (EnchantHelper.isItemEnchantable(itemStack)) {
                for (Enchantment obj : Enchantment.enchantmentsList) {
                    if (obj != null && EnchantHelper.canEnchantItem(itemStack, obj)) {
                        temp.put(obj.effectId, 0);
                    }
                }
            } else {
                for (Enchantment obj : Enchantment.enchantmentsList) {
                    boolean add = true;
                    for (Object enc : temp.keySet()) {
                        Enchantment enchantment = Enchantment.enchantmentsList[(Integer) enc];
                        if (obj != null && (!obj.canApplyTogether(enchantment) || !enchantment.canApplyTogether(obj))) {
                            add = false;
                        }
                    }
                    if (obj != null && EnchantHelper.canEnchantItem(itemStack, obj) && add) {
                        temp.put(obj.effectId, 0);
                    }
                }
            }

            if (this.enchantments != temp) {
                this.enchantments = temp;
            }
        } else {
            this.enchantments = temp;
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemStack = null;
        Slot slot = (Slot) this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();

            ItemStack tempstack = stack.copy();
            itemStack = stack.copy();
            tempstack.stackSize = 1;

            if (par2 != 0) {
                Slot slot1 = (Slot) this.inventorySlots.get(0);

                if (!slot1.getHasStack() && slot1.isItemValid(tempstack) && mergeItemStack(tempstack, 0, 1, false)) {
                    stack.stackSize--;
                    itemStack = stack.copy();
                }

            } else if (!mergeItemStack(stack, 1, 37, false)) {
                return null;
            }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemStack.stackSize == stack.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(par1EntityPlayer, stack);
        }
        return itemStack;
    }

    /**
     * Enchants an item
     *
     * @param player player requesting the enchantment
     * @param map    the list of enchantments to add
     * @param cost   the cost of the operation
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    public void enchant(EntityPlayer player, HashMap<Integer, Integer> map, int cost)
    {
        ItemStack itemstack = this.getSlot(0).getStack();
        HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
        int serverCost = 0;

        if (itemstack == null) return;
        for (Integer enchantId : map.keySet()) {
            Integer level = map.get(enchantId);
            Integer startingLevel = enchantments.get(enchantId);

            if (level > startingLevel) {
                serverCost += enchantmentCost(enchantId, level, startingLevel);
            } else if (level < startingLevel) {
                serverCost += disenchantmentCost(enchantId, level, startingLevel);
            }
        }

        if (cost != serverCost) return;

        for (Integer enchantId : enchantments.keySet()) {
            Integer level = enchantments.get(enchantId);

            if (level != 0)
                if (!map.containsKey(enchantId)) {
                    map.put(enchantId, level);
                }
        }

        for (Integer enchantId : map.keySet()) {
            Integer level = map.get(enchantId);

            if (level == 0) {
                temp.put(enchantId, level);
            }
        }
        for (Object object : temp.keySet()) map.remove(object);

        if (canPurchase(player, serverCost)) {
            EnchantHelper.setEnchantments(map, itemstack);
            if (!player.capabilities.isCreativeMode) player.addExperienceLevel(-cost);
        }

        this.onCraftMatrixChanged(this.tableInventory);

    }

    public boolean canPurchase(EntityPlayer player, int cost)
    {
        return player.capabilities.isCreativeMode || (player.experienceLevel >= cost && ConfigurationSettings.bookShelves ? cost <= bookCases() : cost <= player.experienceLevel);
    }

    public int enchantmentCost(int enchantmentId, int enchantmentLevel, Integer level)
    {
        ItemStack itemStack = this.tableInventory.getStackInSlot(0);
        if (itemStack == null) return 0;
        Enchantment enchantment = Enchantment.enchantmentsList[enchantmentId];
        int maxLevel = enchantment.getMaxLevel();

        int averageCost = (enchantment.getMinEnchantability(enchantmentLevel) + enchantment.getMaxEnchantability(enchantmentLevel)) / 2;
        int adjustedCost = (int) ((averageCost * (enchantmentLevel - level)) / ((double) maxLevel * 4));
        if (!ConfigurationSettings.bookShelves) {
            int temp = adjustedCost * (60 / (bookCases() + 1));
            temp /= 20;
            if (temp > adjustedCost) {
                adjustedCost = temp;
            }
        }
        return Math.max(1, adjustedCost);
    }

    public int disenchantmentCost(int enchantmentId, int enchantmentLevel, Integer level)
    {
        ItemStack itemStack = this.tableInventory.getStackInSlot(0);
        if (itemStack == null) return 0;
        Enchantment enchantment = Enchantment.enchantmentsList[enchantmentId];
        int maxLevel = enchantment.getMaxLevel();

        int averageCost = (enchantment.getMinEnchantability(level) + enchantment.getMaxEnchantability(level)) / 2;
        int adjustedCost = (int) ((averageCost * (enchantmentLevel - level)) / ((double) maxLevel * 2));
        if (!ConfigurationSettings.bookShelves) {
            int temp = adjustedCost * (60 / (bookCases() + 1));
            temp /= 20;
            if (temp > adjustedCost) {
                adjustedCost = temp;
            }
        }
        return Math.min(-1, adjustedCost);
    }

    public int bookCases()
    {
        int temp = 0;
        for (int j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                if ((j != 0 || k != 0) && this.worldObj.isAirBlock(this.xPos + k, this.yPos, this.zPos + j) && this.worldObj.isAirBlock(this.xPos + k, this.yPos + 1, this.zPos + j)) {
                    if (this.worldObj.getBlockId(this.xPos + k * 2, this.yPos, this.zPos + j * 2) == Block.bookShelf.blockID) {
                        ++temp;
                    }

                    if (this.worldObj.getBlockId(this.xPos + k * 2, this.yPos + 1, this.zPos + j * 2) == Block.bookShelf.blockID) {
                        ++temp;
                    }

                    if (k != 0 && j != 0) {
                        if (this.worldObj.getBlockId(this.xPos + k * 2, this.yPos, this.zPos + j) == Block.bookShelf.blockID) {
                            ++temp;
                        }

                        if (this.worldObj.getBlockId(this.xPos + k * 2, this.yPos + 1, this.zPos + j) == Block.bookShelf.blockID) {
                            ++temp;
                        }

                        if (this.worldObj.getBlockId(this.xPos + k, this.yPos, this.zPos + j * 2) == Block.bookShelf.blockID) {
                            ++temp;
                        }

                        if (this.worldObj.getBlockId(this.xPos + k, this.yPos + 1, this.zPos + j * 2) == Block.bookShelf.blockID) {
                            ++temp;
                        }
                    }
                }
            }
        }

        return temp * 2;
    }
}

