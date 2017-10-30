package net.darkhax.eplus.item;

import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.darkhax.eplus.libs.Content;
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

    public ItemTableUpgrade () {

        this.setMaxStackSize(16);
    }

    @Override
    public EnumActionResult onItemUse (EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        final Block block = worldIn.getBlockState(pos).getBlock();

        if (block == Blocks.ENCHANTING_TABLE) {

            worldIn.setBlockState(pos, Content.blockAdvancedTable.getDefaultState());
            worldIn.setTileEntity(pos, new TileEntityAdvancedTable());

            if (!playerIn.capabilities.isCreativeMode) {
                playerIn.getHeldItem(hand).shrink(1);
            }
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }
}