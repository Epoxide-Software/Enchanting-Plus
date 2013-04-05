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

    public IInventory tableInventory = new SlotEnchantTable(this, "Enchant", true, 1);
    public World worldObj;
    private Map<Integer, Integer> enchantments;

    public ContainerEnchantTable(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5) {
        this.worldObj = par2World;

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

    public Map<Integer, Integer> getEnchantments() {
        return enchantments;
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
    @SuppressWarnings("unchecked")
    private void readItems() {
        ItemStack itemStack = this.tableInventory.getStackInSlot(0);

        this.enchantments = new LinkedHashMap<Integer, Integer>();

        if (itemStack != null) {
            if (EnchantHelper.isItemEnchanted(itemStack)) {
                this.enchantments.putAll(EnchantmentHelper.getEnchantments(itemStack));
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
                    for (Object enc : enchantments.keySet()) {
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

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemStack = null;
        Slot slot = (Slot) this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();

            ItemStack tempstack = stack.copy();
            itemStack = stack.copy();
            tempstack.stackSize = 1;

            if (par2 != 0) {
                Slot slot1 = (Slot) this.inventorySlots.get(0);
                Slot slot2 = (Slot) this.inventorySlots.get(1);

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
     * @param player player requesting the enchantment
     * @param map the list of enchantments to add
     * @param cost the cost of the operation
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    public void enchant(EntityPlayer player, HashMap<Integer, Integer> map, int cost) {
        ItemStack itemstack = this.getSlot(0).getStack();
        if (itemstack == null) return;

        for (Integer enchantId : enchantments.keySet()) {
            Integer level = enchantments.get(enchantId);

            if (level != 0)
                if (!map.containsKey(enchantId))
                    map.put(enchantId, level);
        }

        HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();

        for (Integer enchantId : map.keySet()) {
            Integer level = map.get(enchantId);

            if (level == 0)
                temp.put(enchantId, level);
        }
        for (Object object : temp.keySet()) map.remove(object);

        EnchantHelper.setEnchantments(map, itemstack);

        if (!player.capabilities.isCreativeMode) player.addExperienceLevel(-cost);

        this.onCraftMatrixChanged(this.tableInventory);

    }
}

