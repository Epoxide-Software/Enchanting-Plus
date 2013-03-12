package eplus.common;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

/**
 * User: Stengel
 * Date: 3/12/13
 * Time: 7:56 AM
 */
@SuppressWarnings("ALL")
public class EplusTab extends CreativeTabs{
    public EplusTab(String label) {
        super(label);
    }

    @Override
    public int getTabIconItemIndex() {
        return Block.enchantmentTable.blockID;
    }
}
