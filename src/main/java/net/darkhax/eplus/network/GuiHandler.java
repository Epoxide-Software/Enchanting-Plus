package net.darkhax.eplus.network;

import net.darkhax.eplus.block.tileentity.EnchantmentLogicController;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.darkhax.eplus.gui.GuiAdvancedTable;
import net.darkhax.eplus.inventory.ContainerAdvancedTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public final class GuiHandler implements IGuiHandler {

    public static final int ADVANCED_TABLE = 0;

    @Override
    public Object getClientGuiElement (int id, EntityPlayer player, World world, int x, int y, int z) {

        final BlockPos pos = new BlockPos(x, y, z);

        if (id == ADVANCED_TABLE) {

            final ContainerAdvancedTable container = this.getAdvancedContainer(player, world, pos);

            if (container != null) {

                return new GuiAdvancedTable(container);
            }
        }

        return null;
    }

    @Override
    public Object getServerGuiElement (int id, EntityPlayer player, World world, int x, int y, int z) {

        final BlockPos pos = new BlockPos(x, y, z);

        if (id == ADVANCED_TABLE) {

            return this.getAdvancedContainer(player, world, pos);
        }

        return null;
    }

    private ContainerAdvancedTable getAdvancedContainer (EntityPlayer player, World world, BlockPos pos) {

        final TileEntity te = world.getTileEntity(pos);

        if (te instanceof TileEntityAdvancedTable) {

            final EnchantmentLogicController logic = new EnchantmentLogicController(player, world, pos, ((TileEntityAdvancedTable) te).getInventory(player));
            return new ContainerAdvancedTable(player.inventory, logic);
        }

        return null;
    }
}