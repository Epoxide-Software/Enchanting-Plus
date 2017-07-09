package net.darkhax.eplus.item;

import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.handler.ContentHandler;
import net.darkhax.eplus.tileentity.TileEntityAdvancedTable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTableUpgrade extends Item {
    
    public ItemTableUpgrade() {
        
        this.setMaxStackSize(16);
        this.setUnlocalizedName("eplus.tableUpgrade");
        this.setCreativeTab(EnchantingPlus.tabEplus);
    }
    
    @Override
    public EnumActionResult onItemUse (EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        
            
            final Block block = worldIn.getBlockState(pos).getBlock();
            
            if (block == Blocks.ENCHANTING_TABLE) {
                
                worldIn.setBlockState(pos, ContentHandler.blockAdvancedTable.getDefaultState());
                worldIn.setTileEntity(pos, new TileEntityAdvancedTable());
            
            playerIn.getHeldItem(hand).shrink(1);
            return EnumActionResult.SUCCESS;
            }
            
        return EnumActionResult.PASS;
    }
}