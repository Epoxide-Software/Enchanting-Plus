package eplus.common;

import eplus.common.packet.PacketBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

import java.io.DataInputStream;

public class PacketRepair extends PacketBase
{

    @Override
    public void handlePacket(DataInputStream var1) throws Exception {

        int overflow = var1.read();
        int repairCost = var1.readInt();

        if(repairCost < 0) {
            repairCost += 256 * overflow;
        }
        else {
            repairCost += 128 * overflow;
        }
        
        // TODO Auto-generated method stub
        if (player.openContainer instanceof ContainerEnchanting) {
            EntityPlayerMP playerEntity = (EntityPlayerMP) player;
            Container container = player.openContainer;
            
            ((ContainerEnchanting)container).repair(container.getSlot(0).getStack(), playerEntity, repairCost);
        }
    }

}
