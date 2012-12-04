package eplus.common;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityEnchantmentTable;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IGuiHandler;
import eplus.client.GuiEnchantmentPlus;

public class CommonProxy implements IGuiHandler
{

    public void init(){
        
    }
    
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityEnchantmentTable) { return new ContainerEnchanting(player, world, x, y, z); }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityEnchantmentTable) { return new GuiEnchantmentPlus(player, world, x, y, z); }
        return null;
    }
}
