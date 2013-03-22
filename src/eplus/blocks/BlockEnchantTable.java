package eplus.blocks;

import eplus.EnchantingPlus;
import eplus.inventory.TileEnchantTable;
import eplus.lib.ConfigurationSettings;
import eplus.lib.GuiIds;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */


public class BlockEnchantTable extends BlockEnchantmentTable
{

    protected BlockEnchantTable(int par1)
    {
        super(par1);
    }

    @Override
    public TileEntity createNewTileEntity(World par1World)
    {
        return new TileEnchantTable();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        if(world.isRemote) return true;

        if(ConfigurationSettings.useMod)
        {
            player.openGui(EnchantingPlus.INSTANCE, GuiIds.ModTable, world, x, y, z);
        }
        else {
            player.openGui(EnchantingPlus.INSTANCE, GuiIds.VanillaTable, world, x, y, z);
        }

        return true;
    }
}
