package eplus.common;

import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import eplus.common.packet.PacketBase;

public class PacketRepair extends PacketBase
{

    @Override
    public void handlePacket(DataInputStream var1) throws Exception {
        
        int repairCost = var1.readInt();
        
        // TODO Auto-generated method stub
        if (player.openContainer instanceof ContainerEnchanting) {
            EntityPlayerMP playerEntity = (EntityPlayerMP) player;
            Container container = player.openContainer;
            
            ((ContainerEnchanting)container).repair(container.getSlot(0).getStack(), playerEntity, repairCost);
        }
    }

}
