package eplus.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Enchanting Plus
 * 
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class SlotEnchant extends Slot
{
    final ContainerEnchantTable container;

    public SlotEnchant(ContainerEnchantTable containerEnchantTable, IInventory tableInventory, int i, int i1, int i2)
    {
        super(tableInventory, i, i1, i2);
        container = containerEnchantTable;
    }

    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }

    @Override
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return par1ItemStack.isItemEnchantable() || par1ItemStack.isItemEnchanted() || par1ItemStack.itemID == Item.book.itemID
                || par1ItemStack.itemID == Item.enchantedBook.itemID;
    }
}
