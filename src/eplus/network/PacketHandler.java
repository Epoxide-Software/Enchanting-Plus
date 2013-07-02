package eplus.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import eplus.EnchantingPlus;
import eplus.network.packets.BasePacket;

/**
 * Enchanting Plus
 * 
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class PacketHandler implements IPacketHandler
{

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        try
        {
            final EntityPlayer entityPlayer = (EntityPlayer) player;
            final ByteArrayDataInput input = ByteStreams.newDataInput(packet.data);
            final int packetId = input.readUnsignedByte();

            final BasePacket basePacket = BasePacket.constructPacket(packetId);
            basePacket.read(input);
            basePacket.execute(entityPlayer, entityPlayer.worldObj.isRemote ? Side.CLIENT : Side.SERVER);
        } catch (final BasePacket.ProtocolException ex)
        {
            if (player instanceof EntityPlayerMP)
            {
                ((EntityPlayerMP) player).playerNetServerHandler.kickPlayerFromServer("Protocol Exception!");
                EnchantingPlus.log.warning(((EntityPlayer) player).username + " cause a Protocol Exception!");
            }
        } catch (final Exception ex)
        {
            throw new RuntimeException("Unexpected Reflection exception during Packet construction!", ex);
        }
    }
}
