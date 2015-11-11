package net.epoxide.eplus.common.network;

import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.network.AbstractMessage;
import net.darkhax.bookshelf.lib.util.Utilities;
import net.epoxide.eplus.tileentity.TileEntityArcaneInscriber;
import net.minecraft.entity.player.EntityPlayer;

public class PacketArcaneInscriberEffects extends AbstractMessage<PacketArcaneInscriberEffects> {
    private int x;
    private int y;
    private int z;
    private float progression;
    
    public PacketArcaneInscriberEffects() {
    
    }
    
    public PacketArcaneInscriberEffects(TileEntityArcaneInscriber tileEntity, float progression) {
        
        this.x = tileEntity.xCoord;
        this.y = tileEntity.yCoord;
        this.z = tileEntity.zCoord;
        this.progression = progression;
    }
    
    @Override
    public void handleClientMessage (PacketArcaneInscriberEffects packet, EntityPlayer player) {
        
        if (packet.progression < 1) {
            Utilities.spawnParticleRing(player.worldObj, "enchantmenttable", packet.progression, packet.x + 0.5f, packet.y + 1, packet.z + 0.5f, 0.0d, 0.0d, 0.0d, 0.15);
        }
    }
    
    @Override
    public void handleServerMessage (PacketArcaneInscriberEffects message, EntityPlayer player) {
    
    }
    
    @Override
    public void fromBytes (ByteBuf buf) {
        
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.progression = buf.readFloat();
    }
    
    @Override
    public void toBytes (ByteBuf buf) {
        
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeFloat(progression);
    }
}
