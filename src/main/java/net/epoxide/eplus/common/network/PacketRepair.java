package net.epoxide.eplus.common.network;

import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.network.AbstractMessage;
import net.epoxide.eplus.inventory.ContainerEnchantTable;
import net.minecraft.entity.player.EntityPlayer;

public class PacketRepair extends AbstractMessage {
    protected int totalCost;
    
    public PacketRepair() {
    
    }
    
    public PacketRepair(int totalCost) {
        
        this.totalCost = totalCost;
    }
    
    @Override
    public void handleClientMessage (AbstractMessage message, EntityPlayer player) {
    
    }
    
    @Override
    public void handleServerMessage (AbstractMessage message, EntityPlayer player) {
        
        if (message instanceof PacketRepair) {
            if (player.openContainer instanceof ContainerEnchantTable) {
                PacketRepair packet = (PacketRepair) message;
                try {
                    ((ContainerEnchantTable) player.openContainer).repair(player, packet.totalCost);
                }
                catch (Exception e) {
                    // EnchantingPlus.log.info("Repair failed because: " +
                    // e.getLocalizedMessage());
                }
                player.openContainer.detectAndSendChanges();
            }
        }
    }
    
    @Override
    public void fromBytes (ByteBuf buf) {
        
        totalCost = buf.readInt();
    }
    
    @Override
    public void toBytes (ByteBuf buf) {
        
        buf.writeInt(totalCost);
    }
    
}
