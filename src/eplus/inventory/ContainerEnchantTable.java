package eplus.inventory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import cpw.mods.fml.common.network.PacketDispatcher;
import eplus.helper.EnchantHelper;
import eplus.lib.ConfigurationSettings;
import eplus.lib.EnchantmentHelp;

/**
 * Enchanting Plus
 * 
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ContainerEnchantTable extends Container
{

    public final World worldObj;
    public final IInventory tableInventory = new SlotEnchantTable(this, "Enchant", true, 1);
    final TileEnchantTable tileEnchantTable;
    private final int xPos;
    private final int yPos;
    private final int zPos;
    private Map<Integer, Integer> enchantments;

    public ContainerEnchantTable(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5, TileEnchantTable tileEntity)
    {
        worldObj = par2World;
        xPos = par3;
        yPos = par4;
        zPos = par5;

        tileEnchantTable = tileEntity;

        addSlotToContainer(new SlotEnchant(this, tableInventory, 0, 11, 17));
        int l;

        for (l = 0; l < 3; ++l)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                addSlotToContainer(new Slot(par1InventoryPlayer, i1 + l * 9 + 9, 17 + i1 * 18, 91 + l * 18));
            }
        }

        for (l = 0; l < 9; ++l)
        {
            addSlotToContainer(new Slot(par1InventoryPlayer, l, 17 + l * 18, 149));
        }

        // Will drop items saved in the table.
        if (tileEnchantTable.itemInTable != null)
        {
            par1InventoryPlayer.player.dropPlayerItem(tileEnchantTable.itemInTable);
            tileEnchantTable.itemInTable = null;
        }

        // this.putStackInSlot(0, tileEnchantTable.itemInTable);
    }

    public float bookCases()
    {
        float temp = 0;
        for (int j = -1; j <= 1; ++j)
        {
            for (int k = -1; k <= 1; ++k)
            {
                if ((j != 0 || k != 0) && worldObj.isAirBlock(xPos + k, yPos, zPos + j) && worldObj.isAirBlock(xPos + k, yPos + 1, zPos + j))
                {
                    temp += ForgeHooks.getEnchantPower(worldObj, xPos + k * 2, yPos, zPos + j * 2);
                    temp += ForgeHooks.getEnchantPower(worldObj, xPos + k * 2, yPos + 1, zPos + j * 2);

                    if (k != 0 && j != 0)
                    {
                        temp += ForgeHooks.getEnchantPower(worldObj, xPos + k * 2, yPos, zPos + j);
                        temp += ForgeHooks.getEnchantPower(worldObj, xPos + k * 2, yPos + 1, zPos + j);
                        temp += ForgeHooks.getEnchantPower(worldObj, xPos + k, yPos, zPos + j * 2);
                        temp += ForgeHooks.getEnchantPower(worldObj, xPos + k, yPos + 1, zPos + j * 2);
                    }
                }
            }
        }

        return temp * 2;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return true;
    }

    public boolean canPurchase(EntityPlayer player, int cost) throws Exception
    {
        if (player.capabilities.isCreativeMode)
        {
            return true;
        }

        if (ConfigurationSettings.needsBookShelves)
        {
            if (cost > bookCases())
            {
                throw new Exception("Not enough bookcases. Required " + cost);
            }
        }

        if (player.experienceLevel < cost)
        {
            throw new Exception("Not enough levels. Required " + cost);
        }

        return true;
    }

    public void checkItems()
    {
        if (!ItemStack.areItemStacksEqual(tileEnchantTable.itemInTable, tableInventory.getStackInSlot(0)))
        {
            putStackInSlot(0, tileEnchantTable.itemInTable);
            PacketDispatcher.sendPacketToAllAround(tileEnchantTable.xCoord, tileEnchantTable.yCoord, tileEnchantTable.zCoord, 64, tileEnchantTable.worldObj.getWorldInfo()
                    .getDimension(), tileEnchantTable.getDescriptionPacket());
            onCraftMatrixChanged(tableInventory);
        }
    }

    public int disenchantmentCost(int enchantmentId, int enchantmentLevel, Integer level)
    {
        final ItemStack itemStack = tableInventory.getStackInSlot(0);
        if (itemStack == null)
        {
            return 0;
        }
        final Enchantment enchantment = Enchantment.enchantmentsList[enchantmentId];
        final int maxLevel = enchantment.getMaxLevel();

        if (enchantmentLevel > maxLevel)
        {
            return 0;
        }

        final int averageCost = (enchantment.getMinEnchantability(level) + enchantment.getMaxEnchantability(level)) / 2;
        int adjustedCost = (int) (averageCost * (enchantmentLevel - level - maxLevel) / ((double) maxLevel * 6));
        if (!ConfigurationSettings.needsBookShelves)
        {
            int temp = (int) (adjustedCost * (60 / (bookCases() + 1)));
            temp /= 20;
            if (temp > adjustedCost)
            {
                adjustedCost = temp;
            }
        }
        final int enchantmentCost = enchantmentCost(enchantmentId, level - 1, enchantmentLevel);

        return Math.max(adjustedCost, -enchantmentCost);
    }

    /**
     * Enchants an item
     * 
     * @param player
     *            player requesting the enchantment
     * @param map
     *            the list of enchantments to add
     * @param cost
     *            the cost of the operation
     * @throws Exception
     */
    public void enchant(EntityPlayer player, HashMap<Integer, Integer> map, int cost) throws Exception
    {
        final ItemStack itemstack = tableInventory.getStackInSlot(0);
        final HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
        int serverCost = 0;

        if (itemstack == null)
        {
            return;
        }
        for (final Integer enchantId : map.keySet())
        {
            final Integer level = map.get(enchantId);
            final Integer startingLevel = enchantments.get(enchantId);

            if (level > startingLevel)
            {
                serverCost += enchantmentCost(enchantId, level, startingLevel);
            } else if (level < startingLevel)
            {
                serverCost += disenchantmentCost(enchantId, level, startingLevel);
            }
        }

        if (cost != serverCost)
        {
            throw new Exception("Cost is different on client and server");
        }

        for (final Integer enchantId : enchantments.keySet())
        {
            final Integer level = enchantments.get(enchantId);

            if (level != 0)
            {
                if (!map.containsKey(enchantId))
                {
                    map.put(enchantId, level);
                }
            }
        }

        for (final Integer enchantId : map.keySet())
        {
            final Integer level = map.get(enchantId);

            if (level == 0)
            {
                temp.put(enchantId, level);
            }
        }
        for (final Object object : temp.keySet())
        {
            map.remove(object);
        }

        if (canPurchase(player, serverCost))
        {
            EnchantHelper.setEnchantments(map, itemstack);
            if (!player.capabilities.isCreativeMode)
            {
                player.addExperienceLevel(-cost);
            }
        }

        onCraftMatrixChanged(tableInventory);

    }

    public int enchantmentCost(int enchantmentId, int enchantmentLevel, Integer level)
    {
        final ItemStack itemStack = tableInventory.getStackInSlot(0);
        if (itemStack == null)
        {
            return 0;
        }
        final Enchantment enchantment = Enchantment.enchantmentsList[enchantmentId];
        final int maxLevel = enchantment.getMaxLevel();

        if (enchantmentLevel > maxLevel)
        {
            return 0;
        }

        final int averageCost = (enchantment.getMinEnchantability(enchantmentLevel) + enchantment.getMaxEnchantability(enchantmentLevel)) / 2;
        int adjustedCost = (int) (averageCost * (enchantmentLevel - level + maxLevel) / ((double) maxLevel * 5));
        if (!ConfigurationSettings.needsBookShelves)
        {
            int temp = (int) (adjustedCost * (60 / (bookCases() + 1)));
            temp /= 20;
            if (temp > adjustedCost)
            {
                adjustedCost = temp;
            }
        }
        return Math.max(1, adjustedCost);
    }

    public Map<Integer, Integer> getEnchantments()
    {
        return enchantments;
    }

    @Override
    public void onCraftGuiClosed(EntityPlayer par1EntityPlayer)
    {
        super.onCraftGuiClosed(par1EntityPlayer);

        for (int i = 0; i < tableInventory.getSizeInventory(); i++)
        {
            final ItemStack stack = tableInventory.getStackInSlot(i);
            if (stack != null)
            {
                par1EntityPlayer.dropPlayerItem(stack);
            }
        }

        // TODO fix http://mantis.aesireanempire.com/view.php?id=1
        // tileEnchantTable.itemInTable = tableInventory.getStackInSlot(0);
        // tileEnchantTable.getWorldObj().markBlockForRenderUpdate(tileEnchantTable.xCoord,
        // tileEnchantTable.yCoord, tileEnchantTable.zCoord);
    }

    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        super.onCraftMatrixChanged(par1IInventory);

        // TODO fix http://mantis.aesireanempire.com/view.php?id=1
        // tileEnchantTable.itemInTable = this.tableInventory.getStackInSlot(0);
        // PacketDispatcher.sendPacketToAllAround(tileEnchantTable.xCoord,
        // tileEnchantTable.yCoord, tileEnchantTable.zCoord, 64,
        // tileEnchantTable.worldObj.getWorldInfo().getDimension(),
        // tileEnchantTable.getDescriptionPacket());
        tileEnchantTable.getWorldObj().markBlockForRenderUpdate(tileEnchantTable.xCoord, tileEnchantTable.yCoord, tileEnchantTable.zCoord);

        readItems();
    }

    /**
     * Will read the enchantments on the items and ones the can be added to the
     * items
     */
    @SuppressWarnings("unchecked")
    private void readItems()
    {
        final ItemStack itemStack = tableInventory.getStackInSlot(0);

        final HashMap<Integer, Integer> temp = new LinkedHashMap<Integer, Integer>();

        if (itemStack != null && !EnchantmentHelp.isBlackListed(itemStack.getItem()))
        {
            if (EnchantHelper.isItemEnchanted(itemStack))
            {
                temp.putAll(EnchantmentHelper.getEnchantments(itemStack));
            }
            if (EnchantHelper.isItemEnchantable(itemStack))
            {
                for (final Enchantment obj : Enchantment.enchantmentsList)
                {
                    if (obj != null && EnchantHelper.canEnchantItem(itemStack, obj) && !EnchantmentHelp.isBlackListed(obj))
                    {
                        temp.put(obj.effectId, 0);
                    }
                }
            } else
            {
                for (final Enchantment obj : Enchantment.enchantmentsList)
                {
                    boolean add = true;
                    for (final Object enc : temp.keySet())
                    {
                        final Enchantment enchantment = Enchantment.enchantmentsList[(Integer) enc];
                        if (obj != null && (!obj.canApplyTogether(enchantment) || !enchantment.canApplyTogether(obj)))
                        {
                            add = false;
                        }
                    }
                    if (obj != null && EnchantHelper.canEnchantItem(itemStack, obj) && add && !EnchantmentHelp.isBlackListed(obj))
                    {
                        temp.put(obj.effectId, 0);
                    }
                }
            }

            if (enchantments != temp)
            {
                enchantments = temp;
            }
        } else
        {
            enchantments = temp;
        }
    }

    public void repair(EntityPlayer player, int cost) throws Exception
    {
        final ItemStack itemStack = tableInventory.getStackInSlot(0);
        if (itemStack == null)
        {
            return;
        }

        final int serverCost = repairCost();
        if (cost != serverCost || !itemStack.isItemEnchanted())
        {
            return;
        }

        if (canPurchase(player, serverCost))
        {
            itemStack.setItemDamage(0);
            if (!player.capabilities.isCreativeMode)
            {
                player.addExperienceLevel(-cost);
            }
        }

        onCraftMatrixChanged(tableInventory);
    }

    public int repairCost()
    {
        final ItemStack itemStack = tableInventory.getStackInSlot(0);
        if (itemStack == null)
        {
            return 0;
        }
        if (!itemStack.isItemEnchanted() || !itemStack.isItemDamaged())
        {
            return 0;
        }

        int cost = 0;

        @SuppressWarnings("unchecked")
        final Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);

        for (final Integer enchantment : enchantments.keySet())
        {
            final Integer enchantmentId = enchantment;
            final Integer enchantmentLevel = enchantments.get(enchantment);

            cost += enchantmentCost(enchantmentId, enchantmentLevel, 0);
        }

        final int maxDamage = itemStack.getMaxDamage();
        final int currentDamage = itemStack.getItemDamage();

        final double percentDamage = currentDamage / ((double) maxDamage * 2);

        return (int) Math.max(1, percentDamage * cost);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemStack = null;
        final Slot slot = (Slot) inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            final ItemStack stack = slot.getStack();

            final ItemStack tempStack = stack.copy();
            itemStack = stack.copy();
            tempStack.stackSize = 1;

            if (par2 != 0)
            {
                final Slot slot1 = (Slot) inventorySlots.get(0);

                if (!slot1.getHasStack() && slot1.isItemValid(tempStack) && mergeItemStack(tempStack, 0, 1, false))
                {
                    stack.stackSize--;
                    itemStack = stack.copy();
                }

            } else if (!mergeItemStack(stack, 1, 37, false))
            {
                return null;
            }

            if (stack.stackSize == 0)
            {
                slot.putStack(null);
            } else
            {
                slot.onSlotChanged();
            }

            if (itemStack.stackSize == stack.stackSize)
            {
                return null;
            }
            slot.onPickupFromSlot(par1EntityPlayer, stack);
        }
        return itemStack;
    }
}
