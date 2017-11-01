package net.darkhax.eplus.common.network;

import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.darkhax.eplus.client.gui.n.GuiAdvancedTable;
import net.darkhax.eplus.inventory.n.ContainerAdvancedTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public final class GuiHandler implements IGuiHandler {

    public static final int ADVANCED_TABLE = 0;

    @Override
    public Object getClientGuiElement (int id, EntityPlayer entityPlayer, World world, int x, int y, int z) {

        switch (id) {
            case ADVANCED_TABLE:
                return new GuiAdvancedTable(entityPlayer.inventory, (TileEntityAdvancedTable) world.getTileEntity(new BlockPos(x, y, z)));

            default:
                return null;
        }
    }

    @Override
    public Object getServerGuiElement (int id, EntityPlayer entityPlayer, World world, int x, int y, int z) {

        switch (id) {
            case ADVANCED_TABLE:
                return new ContainerAdvancedTable(entityPlayer.inventory, (TileEntityAdvancedTable) world.getTileEntity(new BlockPos(x, y, z)));

            default:
                return null;
        }
    }
}