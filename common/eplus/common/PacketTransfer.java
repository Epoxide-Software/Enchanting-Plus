package eplus.common;

import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import eplus.common.packet.PacketBase;

public class PacketTransfer extends PacketBase
{

    @Override
    public void handlePacket(DataInputStream var1) throws Exception {
        
        int transferCost = var1.readInt();
        
        if (player.openContainer instanceof ContainerEnchanting) {
            EntityPlayerMP playerEntity = (EntityPlayerMP) player;
            Container container = player.openContainer;
            
            ((ContainerEnchanting)container).transfer(container.getSlot(0).getStack(),container.getSlot(1).getStack(), playerEntity, transferCost);
        }
    }

}
