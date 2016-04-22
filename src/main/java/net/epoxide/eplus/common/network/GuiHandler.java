package net.epoxide.eplus.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public final class GuiHandler implements IGuiHandler {
    
    @Override
    public Object getServerGuiElement (int id, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        
        return null;
    }
    
    @Override
    public Object getClientGuiElement (int id, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        
        return null;
    }
}