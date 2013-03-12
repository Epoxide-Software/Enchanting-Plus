package eplus.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.world.World;

import java.util.Random;

public class BlockEnchantingTable extends BlockEnchantmentTable
{

    protected BlockEnchantingTable(int par1)
    {
        super(par1);
        if (EnchantingPlus.hasLight) // created by Slash
        	this.setLightValue(1.0F);
    }

    @Override
    public TileEntity createNewTileEntity(World par1World)
    {
        return new TileEntityEnchantmentTable();
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int var6, float var7, float var8, float var9)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
            if (EnchantingPlus.useMod)
            {
           		entityPlayer.openGui(EnchantingPlus.instance, 0, world, x, y, z);
            }
            else
            {
                TileEntityEnchantmentTable tileentityenchantmenttable = (TileEntityEnchantmentTable)world.getBlockTileEntity(x, y, z);
                entityPlayer.displayGUIEnchantment(x, y, z, tileentityenchantmenttable.func_94135_b() ? tileentityenchantmenttable.func_94133_a() : null);
            }
            return true;
        }
    }

    @SideOnly(Side.CLIENT)
    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @Override
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) // created by Slash
    {
    	super.randomDisplayTick(par1World, par2, par3, par4, par5Random);
    	
    	if (!EnchantingPlus.hasParticles) return;
    	
        for (int var6 = 0; var6 < 4; ++var6)
        {
            double var7 = (double)((float)par2 + par5Random.nextFloat());
            double var9 = (double)((float)par3 + par5Random.nextFloat());
            double var11 = (double)((float)par4 + par5Random.nextFloat());
            double var13;
            double var15;
            double var17;
            int var19 = par5Random.nextInt(2) * 2 - 1;
            var13 = ((double)par5Random.nextFloat() - 0.5D) * 0.5D;
            var15 = ((double)par5Random.nextFloat() - 0.5D) * 0.5D;
            var17 = ((double)par5Random.nextFloat() - 0.5D) * 0.5D;

            if (par1World.getBlockId(par2 - 1, par3, par4) != this.blockID && par1World.getBlockId(par2 + 1, par3, par4) != this.blockID)
            {
                var7 = (double)par2 + 0.5D + 0.25D * (double)var19;
                var13 = (double)(par5Random.nextFloat() * 2.0F * (float)var19);
            }
            else
            {
                var11 = (double)par4 + 0.5D + 0.25D * (double)var19;
                var17 = (double)(par5Random.nextFloat() * 2.0F * (float)var19);
            }

            par1World.spawnParticle("portal", var7, var9, var11, var13, var15, var17);
        }
    }
    
    
    
}
