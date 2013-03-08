package eplus.common;

import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import eplus.common.packet.PacketBase;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

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
            data.writeBoolean(EnchantingPlus.allowDestroyItemOnDisenchanting);
            data.writeBoolean(EnchantingPlus.allowPocketDisenchanting);
            data.writeBoolean(EnchantingPlus.allowPocketRepair);
            data.writeBoolean(EnchantingPlus.allowPocketTransfer);
            data.writeBoolean(EnchantingPlus.needBookShelves);
            data.writeBoolean(EnchantingPlus.strictEnchant);
            data.writeDouble(EnchantingPlus.enchantFactor);
            data.writeDouble(EnchantingPlus.disenchantFactor);
            data.writeDouble(EnchantingPlus.repairFactor);
            data.writeDouble(EnchantingPlus.transferFactor);
            
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
