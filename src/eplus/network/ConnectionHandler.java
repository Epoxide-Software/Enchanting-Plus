package eplus.network;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import eplus.lib.ConfigurationSettings;
import eplus.network.packets.EnchantmentAllowedPacket;
import eplus.network.packets.ReConfigPacket;

/**
 * Enchanting Plus
 * 
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ConnectionHandler implements IConnectionHandler
{

    @Override
    public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login)
    {
        PacketDispatcher.sendPacketToServer(new ReConfigPacket().makePacket());
    }

    @Override
    public void connectionClosed(INetworkManager manager)
    {

    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager)
    {

    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager)
    {

    }

    @Override
    public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager)
    {
        return null;
    }

    @Override
    public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager)
    {
        PacketDispatcher.sendPacketToPlayer(new ReConfigPacket().makePacket(), player);
        PacketDispatcher.sendPacketToPlayer(new EnchantmentAllowedPacket(ConfigurationSettings.enchantments).makePacket(), player);
        
    }
}
