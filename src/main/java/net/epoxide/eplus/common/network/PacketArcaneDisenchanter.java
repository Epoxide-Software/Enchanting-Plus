package net.epoxide.eplus.common.network;

import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.network.AbstractMessage;
import net.epoxide.eplus.client.ProxyClient;
import net.epoxide.eplus.tileentity.TileEntityArcaneDisenchanter;
import net.minecraft.entity.player.EntityPlayer;

public class PacketArcaneDisenchanter extends AbstractMessage<PacketArcaneDisenchanter> {
    private int x;
    private int y;
    private int z;
    private boolean isSuccessful;

    public PacketArcaneDisenchanter () {

    }

    public PacketArcaneDisenchanter (boolean isSuccessful, TileEntityArcaneDisenchanter tileEntity) {

        this.isSuccessful = isSuccessful;
        this.x = tileEntity.xCoord;
        this.y = tileEntity.yCoord;
        this.z = tileEntity.zCoord;
    }

    @Override
    public void handleClientMessage (PacketArcaneDisenchanter packet, EntityPlayer player) {

        ProxyClient.spawnParticleRing(player.worldObj, "enchantmenttable", packet.x + 0.5f, packet.y + 1, packet.z + 0.5f, 0.0d, 0.0d, 0.0d, 0.15);
        player.worldObj.playSound(packet.x + 0.5f, packet.y + 1, packet.z + 0.5f, "", 0.5F, 1f, false);
    }

    @Override
    public void handleServerMessage (PacketArcaneDisenchanter message, EntityPlayer player) {

    }

    @Override
    public void fromBytes (ByteBuf buf) {

        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.isSuccessful = buf.readBoolean();
    }

    @Override
    public void toBytes (ByteBuf buf) {

        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(isSuccessful);
    }
}
