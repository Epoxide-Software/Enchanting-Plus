package eplus.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class SlotEnchant extends Slot {
    final ContainerEnchantTable container;

    public SlotEnchant(ContainerEnchantTable containerEnchantTable, IInventory tableInventory, int i, int i1, int i2) {
        super(tableInventory, i, i1, i2);
        this.container = containerEnchantTable;
    }
}
