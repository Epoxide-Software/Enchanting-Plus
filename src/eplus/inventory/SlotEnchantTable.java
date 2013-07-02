package eplus.inventory;

import net.minecraft.inventory.InventoryBasic;

/**
 * Enchanting Plus
 * 
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class SlotEnchantTable extends InventoryBasic
{
    final ContainerEnchantTable container;

    public SlotEnchantTable(ContainerEnchantTable containerEnchantTable, String par1Str, boolean par2, int par3)
    {
        super(par1Str, par2, par3);

        container = containerEnchantTable;
    }

    @Override
    public void onInventoryChanged()
    {
        super.onInventoryChanged();
        container.onCraftMatrixChanged(this);
    }
}
