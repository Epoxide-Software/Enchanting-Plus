package eplus.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import eplus.EnchantingPlus;
import eplus.network.packets.BasePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class PacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager manager,
                             Packet250CustomPayload packet, Player player)
    {
        try {
            EntityPlayer entityPlayer = (EntityPlayer) player;
            ByteArrayDataInput input = ByteStreams.newDataInput(packet.data);
            int packetId = input.readUnsignedByte();

            BasePacket basePacket = BasePacket.constructPacket(packetId);
            basePacket.read(input);
            basePacket.execute(entityPlayer,
                    entityPlayer.worldObj.isRemote ? Side.CLIENT : Side.SERVER);
        } catch (BasePacket.ProtocolException ex) {
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).playerNetServerHandler
                        .kickPlayerFromServer("Protocol Exception!");
                EnchantingPlus.log.warning(((EntityPlayer) player).username
                        + " cause a Protocol Exception!");
            }
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Unexpected Reflection exception during Packet construction!",
                    ex);
        }
    }
}
