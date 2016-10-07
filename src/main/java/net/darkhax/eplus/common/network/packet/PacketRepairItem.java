package net.darkhax.eplus.common.network.packet;

import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.lib.util.PlayerUtils;
import net.darkhax.eplus.inventory.ContainerAdvancedTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Packet for repairing an item from the enchanting plus GUI.
 */
public class PacketRepairItem implements IMessage {
    
    /**
     * The repair cost that the client thinks it should have to pay.
     */
    protected int repairCost;
    
    public PacketRepairItem() {
        
    }
    
    public PacketRepairItem(int repairCost) {
        
        this.repairCost = repairCost;
    }
    
    @Override
    public void fromBytes (ByteBuf buf) {
        
        this.repairCost = buf.readInt();
    }
    
    @Override
    public void toBytes (ByteBuf buf) {
        
        buf.writeInt(this.repairCost);
    }
    
    public static class PacketHandler implements IMessageHandler<PacketRepairItem, IMessage> {
        
        @Override
        public IMessage onMessage (PacketRepairItem packet, MessageContext ctx) {
            
            final EntityPlayer player = ctx.side == Side.CLIENT ? PlayerUtils.getClientPlayer() : ctx.getServerHandler().playerEntity;
            
            if (player.openContainer instanceof ContainerAdvancedTable) {
                
                ((ContainerAdvancedTable) player.openContainer).repair(player, packet.repairCost);
                player.openContainer.detectAndSendChanges();
            }
            
            return null;
        }
    }
}