package net.epoxide.eplus.block;

import net.epoxide.eplus.EnchantingPlus;
import net.epoxide.eplus.tileentity.TileEntityEnchantmentBook;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockEnchantmentBook extends BlockContainer {
    
    public BlockEnchantmentBook() {
        
        super(Material.wood);
        this.setCreativeTab(EnchantingPlus.tabEplus);
        this.setBlockBounds(0.3F, 0.3F, 0.3F, 0.6F, 0.6F, 0.6F);
        this.setHardness(1.5F);
        this.setLightLevel(0.9375F);
        this.setBlockName("eplus.enchantmentbook");
        this.setBlockTextureName("minecraft:bookshelf");
    }
    
    @Override
    public boolean renderAsNormalBlock () {
        
        return false;
    }
    
    @Override
    public boolean isOpaqueCube () {
        
        return false;
    }
    
    @Override
    public int getRenderType () {
        
        return -1;
    }
    
    @Override
    public TileEntity createNewTileEntity (World world, int meta) {
        
        return new TileEntityEnchantmentBook();
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool (World world, int i, int j, int k) {
        
        return null;
    }
}