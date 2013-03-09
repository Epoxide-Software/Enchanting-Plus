package eplus.common;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import eplus.common.packet.PacketBase;
import eplus.common.packet.PacketConfig;
import eplus.common.packet.PacketDisenchant;
import eplus.common.packet.PacketEnchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet106Transaction;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class PacketHandler implements IPacketHandler
{

    public static final PacketBase[] packets;

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {

        ByteArrayInputStream bytes = new ByteArrayInputStream(packet.data);
        DataInputStream data = new DataInputStream(bytes);
        try
        {
            int id = data.readInt();
            if (id == 99)
            {
                int bookshelves = data.readByte();
                ContainerEnchanting.setBooks(bookshelves);
                return;

            }
            else if (id == 98)
            {
                PacketDispatcher.sendPacketToPlayer(new Packet106Transaction(data.readByte(), data.readByte(), true), player);
                return;
            }
            packets[id].player = (EntityPlayer) player;
            packets[id].handlePacket(data);
            data.close();
            bytes.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    static
    {
        packets = new PacketBase[]
        {new PacketConfig(), new PacketEnchant(), new PacketDisenchant(), new PacketRepair(), new PacketTransfer()};
    }
}
