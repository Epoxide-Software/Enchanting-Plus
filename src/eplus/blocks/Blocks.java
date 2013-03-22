package eplus.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class Blocks
{

    public static void init()
    {
        int blockID = Block.enchantmentTable.blockID;
        Block.blocksList[blockID] = null;
        Item.itemsList[blockID] = null;

        Block table = (new BlockEnchantTable(blockID)).setHardness(5.0F).setResistance(2000.0F).setUnlocalizedName("enchantmentTable");
        GameRegistry.registerBlock(table, table.getUnlocalizedName2());
    }
}
