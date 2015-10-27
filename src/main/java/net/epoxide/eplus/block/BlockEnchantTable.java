package net.epoxide.eplus.block;

import net.epoxide.eplus.EnchantingPlus;
import net.epoxide.eplus.tileentity.TileEntityEnchantingTable;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockEnchantTable extends BlockEnchantmentTable {

    public BlockEnchantTable () {

        this.setBlockName("advancedEnchantmentTable");
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float subX, float subY, float subZ) {

        if (!world.isRemote && !entityPlayer.isSneaking())
            entityPlayer.openGui(EnchantingPlus.instance, 1, world, x, y, z);

        return true;
    }

    @Override
    public TileEntity createNewTileEntity (World world, int meta) {

        return new TileEntityEnchantingTable();
    }
}
