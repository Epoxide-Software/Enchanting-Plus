package net.darkhax.eplus.block;

import java.util.Map;
import java.util.UUID;

import net.darkhax.bookshelf.block.BlockTileEntity;
import net.darkhax.bookshelf.block.ITileEntityBlock;
import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.darkhax.eplus.block.tileentity.renderer.TileEntityAdvancedTableRenderer;
import net.darkhax.eplus.inventory.ItemStackHandlerEnchant;
import net.darkhax.eplus.network.GuiHandler;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
    public void breakBlock (World worldIn, BlockPos pos, IBlockState state) {

        final TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityAdvancedTable) {

            final Map<UUID, ItemStackHandlerEnchant> inventories = ((TileEntityAdvancedTable) tileentity).getInveotries();

            for (final ItemStackHandlerEnchant inventory : inventories.values()) {

                StackUtils.dropStackInWorld(worldIn, pos, inventory.getEnchantingStack());
                inventory.setStackInSlot(0, ItemStack.EMPTY);
            }

            inventories.clear();
        }

        super.breakBlock(worldIn, pos, state);
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