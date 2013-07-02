package eplus.network;

import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import eplus.network.packets.ReConfigPacket;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ConnectionHandler implements IConnectionHandler {

    @Override
    public void playerLoggedIn(Player player, NetHandler netHandler,
                               INetworkManager manager)
    {
        PacketDispatcher.sendPacketToPlayer(new ReConfigPacket().makePacket(),
                player);
    }

    @Override
    public String connectionReceived(NetLoginHandler netHandler,
                                     INetworkManager manager)
    {
        return null;
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, String server,
                                 int port, INetworkManager manager)
    {

    }

    @Override
    public void connectionOpened(NetHandler netClientHandler,
                                 MinecraftServer server, INetworkManager manager)
    {

    }

    @Override
    public void connectionClosed(INetworkManager manager)
    {

    }

    @Override
    public void clientLoggedIn(NetHandler clientHandler,
                               INetworkManager manager, Packet1Login login)
    {
        PacketDispatcher.sendPacketToServer(new ReConfigPacket().makePacket());
    }
}
