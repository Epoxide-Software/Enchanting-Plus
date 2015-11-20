package net.epoxide.eplus.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import net.darkhax.bookshelf.lib.util.ItemStackUtils;

import net.epoxide.eplus.EnchantingPlus;
import net.epoxide.eplus.tileentity.TileEntityEnchantmentBook;

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
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float offsetX, float offsetY, float offsetZ) {
        
        TileEntity tile = world.getTileEntity(x, y, z);
        
        if (tile instanceof TileEntityEnchantmentBook && ItemStackUtils.isValidStack(player.getHeldItem())) {
            
            TileEntityEnchantmentBook book = (TileEntityEnchantmentBook) tile;
            
            if (player.getHeldItem().getItem() == Items.feather)
                book.increaseHeight();
                
            else if (player.getHeldItem().getItem() == Items.iron_ingot)
                book.decreaseHeight();
                
            else {
                
                int dyeColor = ItemStackUtils.getDyeColor(player.getHeldItem());
                
                if (dyeColor != -1337) {
                    
                    book.color = dyeColor;
                    player.getHeldItem().stackSize--;
                }
            }
            
        }
        
        return true;
    }
    
    @Override
    public float getEnchantPowerBonus (World world, int x, int y, int z) {
        
        return 1f;
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