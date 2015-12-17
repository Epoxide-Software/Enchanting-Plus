package net.epoxide.eplus.block;

import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.epoxide.eplus.EnchantingPlus;
import net.epoxide.eplus.handler.ContentHandler;
import net.epoxide.eplus.tileentity.TileEntityEnchantTable;

public class BlockEnchantTable extends BlockEnchantmentTable {
    
    @SideOnly(Side.CLIENT)
    private IIcon blockTop;
    
    @SideOnly(Side.CLIENT)
    private IIcon blockBottom;
    
    public BlockEnchantTable() {
        
        this.setBlockName("eplus.advancedEnchantmentTable");
        this.setCreativeTab(EnchantingPlus.tabEplus);
        this.setResistance(2000.0F);
        this.setHardness(5.0F);
    }
    
    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        
        player.triggerAchievement(ContentHandler.achievementEnchanter);
        
        if (!world.isRemote && !player.isSneaking())
            player.openGui(EnchantingPlus.instance, 0, world, x, y, z);
            
        return true;
    }
    
    @Override
    public TileEntity createNewTileEntity (World world, int meta) {
        
        return new TileEntityEnchantTable();
    }
    
    @Override
    public IIcon getIcon (int side, int meta) {
        
        return side == 0 ? this.blockBottom : (side == 1 ? this.blockTop : this.blockIcon);
    }
    
    @Override
    public void registerBlockIcons (IIconRegister iconRegister) {
        
        this.blockIcon = iconRegister.registerIcon("eplus:enchanting_table_side");
        this.blockTop = iconRegister.registerIcon("eplus:enchanting_table_top");
        this.blockBottom = iconRegister.registerIcon("eplus:enchanting_table_bottom");
    }
}
