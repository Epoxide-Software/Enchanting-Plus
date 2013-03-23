package eplus.inventory;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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

    private void readItems() {
        ItemStack itemStack = this.tableInventory.getStackInSlot(0);
        ItemStack itemStack1 = this.tableInventory.getStackInSlot(1);

        this.enchantments = new LinkedHashMap();
        this.disenchantments = new LinkedHashMap();

        if (itemStack != null) {
            if (itemStack.isItemEnchanted() || itemStack.hasTagCompound() && itemStack.stackTagCompound.hasKey("StoredEnchantments")) {
                this.disenchantments = EnchantmentHelper.getEnchantments(itemStack);
            } else if (itemStack.isItemEnchantable()) {

                for (Enchantment obj : Enchantment.field_92090_c) {
                    if (obj.func_92089_a(itemStack)) {
                        this.enchantments.put(obj.effectId, 0);
                    }
                }
            }
        }
    }
}

