package net.darkhax.eplus.block;

import net.darkhax.bookshelf.block.BlockTileEntity;
import net.darkhax.bookshelf.block.ITileEntityBlock;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.darkhax.eplus.block.tileentity.renderer.TileEntityAdvancedTableRenderer;
import net.darkhax.eplus.network.GuiHandler;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAdvancedTable extends BlockTileEntity implements ITileEntityBlock {

    private static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);

    public BlockAdvancedTable () {

        super(Material.ROCK, MapColor.PURPLE);
        this.setLightOpacity(0);
        this.setHardness(5.0F);
        this.setResistance(2000.0F);
    }

    @Override
    public TileEntity createNewTileEntity (World worldIn, int meta) {

        return new TileEntityAdvancedTable();
    }

    @Override
    public AxisAlignedBB getBoundingBox (IBlockState state, IBlockAccess source, BlockPos pos) {

        return BOUNDS;
    }

    @Override
    public boolean isFullCube (IBlockState state) {

        return false;
    }

    @Override
    public boolean isOpaqueCube (IBlockState state) {

        return false;
    }

    @Override
    public boolean onBlockActivated (World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

        if (!worldIn.isRemote) {

            final TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityAdvancedTable) {

                playerIn.openGui(EnchantingPlus.instance, GuiHandler.ADVANCED_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
                ((TileEntityAdvancedTable) tileentity).updateItem();
            }

            return true;
        }

        return true;
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass () {

        return TileEntityAdvancedTable.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer<?> getTileRenderer () {

        return new TileEntityAdvancedTableRenderer();
    }
}