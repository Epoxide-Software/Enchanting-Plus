package net.epoxide.eplus.common.network;

import net.minecraft.entity.player.EntityPlayer;

import net.darkhax.bookshelf.common.network.AbstractMessage;

import io.netty.buffer.ByteBuf;
import net.epoxide.eplus.EnchantingPlus;

public class PacketGui extends AbstractMessage {
    
    private int guiId;
    private int xPos;
    private int yPos;
    private int zPos;
    private String username;
    
    public PacketGui() {
    
    }
    
    public PacketGui(String username, int guidId, int xPos, int yPos, int zPos) {
        
        this.username = username;
        this.guiId = guidId;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }
    
    @Override
    public void handleClientMessage (AbstractMessage abstractMessage, EntityPlayer entityPlayer) {
    
    }
    
    @Override
    public void handleServerMessage (AbstractMessage message, EntityPlayer entityPlayer) {
        
        if (message instanceof PacketGui) {
            PacketGui packet = (PacketGui) message;
            entityPlayer.openGui(EnchantingPlus.instance, packet.guiId, entityPlayer.worldObj, packet.xPos, packet.yPos, packet.zPos);
        }
    }
    
    @Override
    public void fromBytes (ByteBuf buf) {
        
        int length = buf.readInt();
        username = buf.readBytes(length).toString();
        guiId = buf.readInt();
        xPos = buf.readInt();
        yPos = buf.readInt();
        zPos = buf.readInt();
    }
    
    @Override
    public void toBytes (ByteBuf buf) {
        
        buf.writeInt(username.length());
        buf.writeBytes(username.getBytes());
        buf.writeInt(guiId);
        buf.writeInt(xPos);
        buf.writeInt(yPos);
        buf.writeInt(zPos);
    }
    
}
