package eplus.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.Packet1Login;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import eplus.common.packet.PacketBase;

public class ConnectionHandler implements IConnectionHandler
{

    @Override
    public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager)
    {
        try
        {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream data = new DataOutputStream(bytes);
            data.writeBoolean(EnchantingPlus.useMod);
            data.writeBoolean(EnchantingPlus.allowDisenchanting);
            data.writeBoolean(EnchantingPlus.allowRepair);
            data.writeBoolean(EnchantingPlus.allowTransfer);
            PacketDispatcher.sendPacketToPlayer(PacketBase.createPacket(EnchantingPlus.PACKET_ID_CONFIG, bytes.toByteArray()), player);
            bytes.close();
            data.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager)
    {
        return null;
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager)
    {
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager)
    {
    }

    @Override
    public void connectionClosed(INetworkManager manager)
    {
    }

    @Override
    public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login)
    {

    }

}
