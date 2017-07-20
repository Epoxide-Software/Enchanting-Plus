package net.darkhax.eplus.block;

import net.darkhax.eplus.tileentity.TileEntityDecoration;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBookDecoration extends BlockContainer {

    private static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.3d, 0.6d, 0.3d, 0.6d, 0.9d, 0.6d);

    public BlockBookDecoration () {

        super(Material.WOOD);
        this.setHardness(1.5F);
        this.setLightLevel(0.9375F);
        this.setUnlocalizedName("eplus.enchantmentbook");
    }

    @Override
    public TileEntity createNewTileEntity (World world, int meta) {

        return new TileEntityDecoration();
    }

    @Override
    public AxisAlignedBB getBoundingBox (IBlockState state, IBlockAccess source, BlockPos pos) {

        if (source.getTileEntity(pos) instanceof TileEntityDecoration) {
            return BOUNDS.offset(0, ((TileEntityDecoration) source.getTileEntity(pos)).height, 0);
        }

        return BOUNDS;
    }

    @Override
    public float getEnchantPowerBonus (World world, BlockPos pos) {

        return 1f;
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

        if (!worldIn.isRemote && !playerIn.getHeldItemMainhand().isEmpty() && worldIn.getTileEntity(pos) instanceof TileEntityDecoration) {

            final TileEntityDecoration deco = (TileEntityDecoration) worldIn.getTileEntity(pos);

            if (playerIn.getHeldItem(hand).getItem() == Items.FEATHER) {
                deco.increaseHeight();
            }
            else if (playerIn.getHeldItem(hand).getItem() == Items.IRON_INGOT) {
                deco.decreaseHeight();
            }

            /*
             * else { final int color = StackUtils.getDyeColor(playerIn.getHeldItemMainhand());
             * if (color != -1337) { deco.color = color;
             * StackUtils.decreaseStackSize(playerIn.getHeldItemMainhand(), 1); } }
             */

            worldIn.notifyBlockUpdate(pos, state, state, 8);
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy (World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {

        final TileEntity tile = worldIn.getTileEntity(pos);

        if (tile instanceof TileEntityDecoration) {
            ((TileEntityDecoration) tile).variant = stack.getMetadata();
        }
    }

    @Override
    public ItemStack getItem (World worldIn, BlockPos pos, IBlockState state) {

        final ItemStack itemstack = this.getData((TileEntityDecoration) worldIn.getTileEntity(pos));
        return itemstack != null ? itemstack : new ItemStack(this);
    }

    @Override
    public void harvestBlock (World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {

        if (te instanceof TileEntityDecoration) {
            final TileEntityDecoration deco = (TileEntityDecoration) te;
            spawnAsEntity(worldIn, pos, this.getData(deco));
        }
        else {
            super.harvestBlock(worldIn, player, pos, state, (TileEntity) null, stack);
        }
    }

    @Override
    public java.util.List<ItemStack> getDrops (IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {

        final TileEntity te = world.getTileEntity(pos);

        final java.util.List<ItemStack> ret = new java.util.ArrayList<>();
        if (te instanceof TileEntityDecoration) {
            final TileEntityDecoration deco = (TileEntityDecoration) te;
            ret.add(this.getData(deco));
        }
        else {
            ret.add(new ItemStack(this, 1, 0));
        }
        return ret;
    }

    public ItemStack getData (TileEntityDecoration tile) {

        final ItemStack stack = new ItemStack(this, 1, tile.variant);
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setFloat("Height", tile.height);
        tag.setInteger("Color", tile.color);
        stack.setTagCompound(tag);
        return stack;
    }
}