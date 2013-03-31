package eplus.inventory;

import eplus.helper.EnchantHelper;
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

    public IInventory tableInventory = new SlotEnchantTable(this, "Enchant", true, 2);
    public World worldObj;
    private Map disenchantments;
    private Map enchantments;

    public ContainerEnchantTable(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5) {
        this.worldObj = par2World;

        this.addSlotToContainer(new SlotEnchant(this, this.tableInventory, 0, 11, 31));
        this.addSlotToContainer(new SlotEnchant(this, this.tableInventory, 1, 11, 57));
        int l;

        for (l = 0; l < 3; ++l) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, i1 + l * 9 + 9, 17 + i1 * 18, 147 + l * 18));
            }
        }

        for (l = 0; l < 9; ++l) {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, l, 17 + l * 18, 205));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory) {
        super.onCraftMatrixChanged(par1IInventory);

        readItems();
    }

    public Map getEnchantments() {
        return enchantments;
    }

    public Map getDisenchantments() {
        return disenchantments;
    }

    @Override
    public void onCraftGuiClosed(EntityPlayer par1EntityPlayer) {
        super.onCraftGuiClosed(par1EntityPlayer);

        if (this.worldObj.isRemote) return;

        for (int i = 0; i < this.tableInventory.getSizeInventory(); i++) {
            ItemStack stack = this.tableInventory.getStackInSlotOnClosing(i);

            if (stack != null) par1EntityPlayer.dropPlayerItem(stack);

        }
    }

    /**
     * Will read the enchantments on the items and ones the can be added to the items
     */
    private void readItems() {
        ItemStack itemStack = this.tableInventory.getStackInSlot(0);
        ItemStack itemStack1 = this.tableInventory.getStackInSlot(1);

        this.enchantments = new LinkedHashMap();
        this.disenchantments = new LinkedHashMap();

        if (itemStack != null) {
            if (itemStack1 != null) {

            } else {
                if (EnchantHelper.isItemEnchanted(itemStack)) {
                    this.disenchantments = EnchantmentHelper.getEnchantments(itemStack);
                }
                if (EnchantHelper.isItemEnchantable(itemStack)) {
                    for (Enchantment obj : Enchantment.field_92090_c) {
                        if (EnchantHelper.canEnchantItem(itemStack, obj)) {
                            this.enchantments.put(obj.effectId, 0);
                        }
                    }
                } else {
                    for (Enchantment obj : Enchantment.field_92090_c) {
                        boolean add = true;
                        for (Object enc : disenchantments.keySet()) {
                            Enchantment enchantment = Enchantment.enchantmentsList[(Integer) enc];
                            if (!obj.canApplyTogether(enchantment) || !enchantment.canApplyTogether(obj)) {
                                add = false;
                            }
                        }
                        if (EnchantHelper.canEnchantItem(itemStack, obj) && add) {
                            this.enchantments.put(obj.effectId, 0);
                        }
                    }
                }
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemStack = null;
        Slot slot = (Slot) this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();

            ItemStack tempstack = stack.copy();
            itemStack = stack.copy();
            tempstack.stackSize = 1;

            if (par2 != 0 && par2 != 1) {
                Slot slot1 = (Slot) this.inventorySlots.get(0);
                Slot slot2 = (Slot) this.inventorySlots.get(1);

                if (!slot1.getHasStack() && slot1.isItemValid(tempstack) && mergeItemStack(tempstack, 0, 1, false)) {
                    stack.stackSize--;
                    itemStack = stack.copy();
                } else if (!slot2.getHasStack() && slot2.isItemValid(tempstack) && mergeItemStack(tempstack, 1, 2, false)) {
                    stack.stackSize--;
                    itemStack = stack.copy();
                }

            } else if (!mergeItemStack(stack, 2, 38, false)) {
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
     * @param player player requesting the enchantment
     * @param map the list of enchantments to add
     * @param cost the cost of the operation
     */
    public void enchant(EntityPlayer player, HashMap<Integer, Integer> map, int cost) {
        ItemStack itemstack = (ItemStack) this.getSlot(0).getStack();

        if (itemstack == null) return;

        map.putAll(disenchantments);

        EnchantHelper.setEnchantments(map, itemstack);

        if (!player.capabilities.isCreativeMode) player.addExperienceLevel(-cost);

        this.onCraftMatrixChanged(this.tableInventory);

    }

    /**
     * Disenchants an item
     * @param player player requesting the disenchantment
     * @param map the list of enchantments to remove / change
     * @param cost the cost of the operations
     */
    public void disenchant(EntityPlayer player, HashMap<Integer, Integer> map, int cost) {
        ItemStack itemstack = (ItemStack) this.getSlot(0).getStack();

        if (itemstack == null) return;

        for(Integer enchantmentId : map.keySet()) {
            if (map.get(enchantmentId) != 0) {
                this.disenchantments.put(enchantmentId, map.get(enchantmentId));
            } else {
                this.disenchantments.remove(enchantmentId);
            }
        }

        EnchantHelper.setEnchantments(disenchantments, itemstack);

        if (!player.capabilities.isCreativeMode) player.addExperienceLevel(cost);

        this.onCraftMatrixChanged(this.tableInventory);
    }
}

