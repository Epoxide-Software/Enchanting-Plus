package eplus.blocks;

import eplus.EnchantingPlus;
import eplus.inventory.TileEnchantTable;
import eplus.lib.ConfigurationSettings;
import eplus.lib.GuiIds;
import eplus.lib.References;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class BlockEnchantTable extends BlockEnchantmentTable {

    protected BlockEnchantTable(int par1)
    {
        super(par1);

        if (ConfigurationSettings.hasLight) {
            this.setLightValue(1.0F);
        }
        
    }

    @Override
    public TileEntity createNewTileEntity(World par1World)
    {
        return new TileEnchantTable();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z,
                                    EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        if (world.isRemote) {
            return true;
        }

        NBTTagCompound tag = player.getEntityData();

        if (tag.getBoolean(References.MODID + ":useMod")
                && ConfigurationSettings.useMod) {
            player.openGui(EnchantingPlus.INSTANCE, GuiIds.ModTable, world, x,
                    y, z);
        } else {
            player.openGui(EnchantingPlus.INSTANCE, GuiIds.VanillaTable, world,
                    x, y, z);
        }

        return true;
    }

    @Override
    public void randomDisplayTick(World par1World, int x, int y, int z,
                                  Random par5Random)
    {
        super.randomDisplayTick(par1World, x, y, z, par5Random);

        if (!ConfigurationSettings.hasParticles) {
            return;
        }

        for (int var6 = 0; var6 < 4; ++var6) {
            double var7 = (double) ((float) x + par5Random.nextFloat());
            double var9 = (double) ((float) y + par5Random.nextFloat());
            double var11 = (double) ((float) z + par5Random.nextFloat());
            double var13;
            double var15;
            double var17;
            int var19 = par5Random.nextInt(2) * 2 - 1;
            var13 = ((double) par5Random.nextFloat() - 0.5D) * 0.5D;
            var15 = ((double) par5Random.nextFloat() - 0.5D) * 0.5D;
            var17 = ((double) par5Random.nextFloat() - 0.5D) * 0.5D;

            if (par1World.getBlockId(x - 1, y, z) != this.blockID
                    && par1World.getBlockId(x + 1, y, z) != this.blockID) {
                var7 = (double) x + 0.5D + 0.25D * (double) var19;
                var13 = (double) (par5Random.nextFloat() * 2.0F * (float) var19);
            } else {
                var11 = (double) z + 0.5D + 0.25D * (double) var19;
                var17 = (double) (par5Random.nextFloat() * 2.0F * (float) var19);
            }

            par1World.spawnParticle("portal", var7, var9, var11, var13, var15,
                    var17);
        }
    }
}
