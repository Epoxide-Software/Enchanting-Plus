package net.darkhax.eplus.common.network;

import net.darkhax.eplus.client.gui.GuiAdvancedTable;
import net.darkhax.eplus.inventory.ContainerAdvancedTable;
import net.darkhax.eplus.tileentity.TileEntityAdvancedTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public final class GuiHandler implements IGuiHandler {
    
    public static final int ADVANCED_TABLE = 0;
    
    @Override
    public Object getServerGuiElement (int id, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        
        final TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        
        switch (id) {
            case ADVANCED_TABLE:
                if (tileEntity instanceof TileEntityAdvancedTable)
                    return new ContainerAdvancedTable(entityPlayer.inventory, world, new BlockPos(x, y, z), (TileEntityAdvancedTable) tileEntity);
                    
            default:
                return null;
        }
    }
    
    @Override
    public Object getClientGuiElement (int id, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        
        final TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        
        switch (id) {
            case ADVANCED_TABLE:
                if (tileEntity instanceof TileEntityAdvancedTable)
                    return new GuiAdvancedTable(entityPlayer.inventory, world, new BlockPos(x, y, z), (TileEntityAdvancedTable) tileEntity);
                    
            default:
                return null;
        }
    }
}