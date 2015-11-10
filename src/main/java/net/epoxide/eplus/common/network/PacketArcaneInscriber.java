package net.epoxide.eplus.common.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.network.AbstractMessage;
import net.darkhax.bookshelf.lib.util.Utilities;
import net.epoxide.eplus.tileentity.TileEntityArcaneInscriber;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PacketArcaneInscriber extends AbstractMessage<PacketArcaneInscriber> {

    private ItemStack output;
    private int x;
    private int y;
    private int z;

    public PacketArcaneInscriber () {

    }

    public PacketArcaneInscriber (ItemStack output, TileEntityArcaneInscriber tile) {

        this.output = output;
        this.x = tile.xCoord;
        this.y = tile.yCoord;
        this.z = tile.zCoord;
    }

    @Override
    public void handleClientMessage (PacketArcaneInscriber packet, EntityPlayer player) {

        if (packet.output != null) {
            Utilities.spawnParticleRing(player.worldObj, "enchantmenttable", packet.x + 0.5f, packet.y + 1, packet.z + 0.5f, 0.0d, 0.0d, 0.0d, 0.15);
            player.worldObj.playSound(packet.x + 0.5f, packet.y + 1, packet.z + 0.5f, "", 0.5F, 1f, false);
        }

        TileEntityArcaneInscriber tile = (TileEntityArcaneInscriber) player.getEntityWorld().getTileEntity(packet.x, packet.y, packet.z);
        tile.updateTileInfo();
        tile.setOutput(packet.output);
    }

    @Override
    public void handleServerMessage (PacketArcaneInscriber message, EntityPlayer player) {

    }

    @Override
    public void fromBytes (ByteBuf buf) {

        output = ByteBufUtils.readItemStack(buf);
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes (ByteBuf buf) {

        ByteBufUtils.writeItemStack(buf, output);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }
}
