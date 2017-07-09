package net.darkhax.eplus.common.network.packet;

import java.util.HashMap;
import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.eplus.inventory.ContainerAdvancedTable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Packet for repairing an item from the enchanting plus GUI.
 */
public class PacketEnchantItem implements IMessage {
    
    /**
     * The enchant cost that the client thinks it should have to pay.
     */
    protected int enchantCost;
    
    protected HashMap<Enchantment, Integer> enchantments = new HashMap<>();
    
    public PacketEnchantItem() {
        
    }
    
    public PacketEnchantItem(int cost, HashMap<Enchantment, Integer> enchants) {
        
        this.enchantCost = cost;
        this.enchantments = enchants;
    }
    
    @Override
    public void fromBytes (ByteBuf buf) {
        
        this.enchantCost = buf.readInt();
        
        this.enchantments = new HashMap<>();
        final int size = buf.readInt();
        
        for (int index = 0; index < size; index++)
            this.enchantments.put(Enchantment.getEnchantmentByLocation(ByteBufUtils.readUTF8String(buf)), buf.readInt());
    }
    
    @Override
    public void toBytes (ByteBuf buf) {
        
        buf.writeInt(this.enchantCost);
        buf.writeInt(this.enchantments.size());
        
        for (final Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
            
            ByteBufUtils.writeUTF8String(buf, entry.getKey().getRegistryName().toString());
            buf.writeInt(entry.getValue());
        }
    }
    
    public static class PacketHandler implements IMessageHandler<PacketEnchantItem, IMessage> {
        
        @Override
        public IMessage onMessage (PacketEnchantItem packet, MessageContext ctx) {
            
            final EntityPlayer player = ctx.side == Side.CLIENT ? PlayerUtils.getClientPlayer() : ctx.getServerHandler().player;
            
            if (player.openContainer instanceof ContainerAdvancedTable) {
                
                ((ContainerAdvancedTable) player.openContainer).updateItemStack(player, packet.enchantments, packet.enchantCost);
                player.openContainer.detectAndSendChanges();
            }
            
            return null;
        }
    }
}