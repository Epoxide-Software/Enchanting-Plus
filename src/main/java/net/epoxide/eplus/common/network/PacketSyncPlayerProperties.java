package net.epoxide.eplus.common.network;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.network.AbstractMessage;
import net.epoxide.eplus.common.PlayerProperties;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSyncPlayerProperties extends AbstractMessage {
    
    private List<Integer> enchantmentIDs = new ArrayList<Integer>();
    
    public PacketSyncPlayerProperties(PlayerProperties props) {
        
        this.enchantmentIDs = props.unlockedEcnahntments;
    }
    
    @Override
    public void fromBytes (ByteBuf buf) {
        
        int size = buf.readInt();
        this.enchantmentIDs = new ArrayList<Integer>();
        
        for (int count = 0; count < size; count++)
            enchantmentIDs.add(buf.readInt());
    }
    
    @Override
    public void toBytes (ByteBuf buf) {
        
        buf.writeInt(this.enchantmentIDs.size());
        
        for (Integer integer : this.enchantmentIDs)
            if (integer != null)
                buf.writeInt(integer);
    }
    
    @Override
    public void handleClientMessage (AbstractMessage message, EntityPlayer player) {
        
        if (message instanceof PacketSyncPlayerProperties) {
            
            PacketSyncPlayerProperties packet = (PacketSyncPlayerProperties) message;
            PlayerProperties props = PlayerProperties.getProperties(player);
            props.unlockedEcnahntments = packet.enchantmentIDs;
        }
    }
    
    @Override
    public void handleServerMessage (AbstractMessage message, EntityPlayer player) {
    
    }
}