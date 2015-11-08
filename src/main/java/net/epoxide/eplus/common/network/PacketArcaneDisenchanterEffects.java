package net.epoxide.eplus.common.network;

import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.network.AbstractMessage;
import net.epoxide.eplus.client.ProxyClient;
import net.epoxide.eplus.tileentity.TileEntityArcaneDisenchanter;
import net.minecraft.entity.player.EntityPlayer;

public class PacketArcaneDisenchanterEffects extends AbstractMessage<PacketArcaneDisenchanterEffects> {
    private int x;
    private int y;
    private int z;
    private boolean isSuccessful;
    private float currentPercentage;

    public PacketArcaneDisenchanterEffects () {

    }

    public PacketArcaneDisenchanterEffects (boolean isSuccessful, TileEntityArcaneDisenchanter tileEntity, float currentPercentage) {

        this.isSuccessful = isSuccessful;
        this.x = tileEntity.xCoord;
        this.y = tileEntity.yCoord;
        this.z = tileEntity.zCoord;
        this.currentPercentage = currentPercentage;
    }

    @Override
    public void handleClientMessage (PacketArcaneDisenchanterEffects packet, EntityPlayer player) {

        if (packet.isSuccessful && packet.currentPercentage >= 1) {
            ProxyClient.spawnParticleRing(player.worldObj, "enchantmenttable", packet.x + 0.5f, packet.y + 1, packet.z + 0.5f, 0.0d, 0.0d, 0.0d, 0.15);
            player.worldObj.playSound(packet.x + 0.5f, packet.y + 1, packet.z + 0.5f, "", 0.5F, 1f, false);
        }
        else {
            ProxyClient.spawnParticleRing(player.worldObj, "enchantmenttable", packet.currentPercentage, packet.x + 0.5f, packet.y + 1, packet.z + 0.5f, 0.0d, 0.0d, 0.0d, 0.15);
        }
    }

    @Override
    public void handleServerMessage (PacketArcaneDisenchanterEffects message, EntityPlayer player) {

    }

    @Override
    public void fromBytes (ByteBuf buf) {

        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.isSuccessful = buf.readBoolean();
        this.currentPercentage = buf.readFloat();
    }

    @Override
    public void toBytes (ByteBuf buf) {

        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(isSuccessful);
        buf.writeFloat(currentPercentage);
    }
}
