package com.aesireanempire.eplus.blocks;

import com.aesireanempire.eplus.EnchantingPlus;
import com.aesireanempire.eplus.inventory.TileEnchantTable;
import com.aesireanempire.eplus.lib.ConfigurationSettings;
import com.aesireanempire.eplus.lib.GuiIds;
import com.aesireanempire.eplus.lib.References;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Enchanting Plus
 * 
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class BlockEnchantTable extends BlockEnchantmentTable
{

    private IIcon field_94461_a;
    private IIcon field_94460_b;

    protected BlockEnchantTable(int par1)
    {
        this();
    }

    public BlockEnchantTable()
    {
        if (ConfigurationSettings.hasLight)
        {
            setLightLevel(1.0F);
        }

    }

    @Override
    public TileEntity createNewTileEntity(World par1World, int meta)
    {
        return new TileEnchantTable();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        if (world.isRemote)
        {
            return true;
        }

        if (ConfigurationSettings.useMod)
        {
            player.openGui(EnchantingPlus.INSTANCE, GuiIds.ModTable, world, x, y, z);
        }

        return true;
    }

    @Override
    public void randomDisplayTick(World par1World, int x, int y, int z, Random par5Random)
    {
        super.randomDisplayTick(par1World, x, y, z, par5Random);

        if (!ConfigurationSettings.hasParticles)
        {
            return;
        }

        for (int var6 = 0; var6 < 4; ++var6)
        {
            double var7 = x + par5Random.nextFloat();
            final double var9 = y + par5Random.nextFloat();
            double var11 = z + par5Random.nextFloat();
            double var13;
            double var15;
            double var17;
            final int var19 = par5Random.nextInt(2) * 2 - 1;
            var13 = (par5Random.nextFloat() - 0.5D) * 0.5D;
            var15 = (par5Random.nextFloat() - 0.5D) * 0.5D;
            var17 = (par5Random.nextFloat() - 0.5D) * 0.5D;

            if (par1World.getBlock(x - 1, y, z) != Blocks.table && par1World.getBlock(x + 1, y, z) != Blocks.table)
            {
                var7 = x + 0.5D + 0.25D * var19;
                var13 = par5Random.nextFloat() * 2.0F * var19;
            }
            else
            {
                var11 = z + 0.5D + 0.25D * var19;
                var17 = par5Random.nextFloat() * 2.0F * var19;
            }

            par1World.spawnParticle("portal", var7, var9, var11, var13, var15, var17);
        }
    }

    @Override
    public IIcon getIcon(int par1, int par2)
    {
        return par1 == 0 ? this.field_94460_b : (par1 == 1 ? this.field_94461_a : this.blockIcon);
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("eplus:enchanting_table_side");
        this.field_94461_a = par1IconRegister.registerIcon("eplus:enchanting_table_top");
        this.field_94460_b = par1IconRegister.registerIcon("eplus:enchanting_table_bottom");
    }
}
