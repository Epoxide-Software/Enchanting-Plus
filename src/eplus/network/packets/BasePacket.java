package eplus.network.packets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import eplus.lib.References;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public abstract class BasePacket
{

    public static class ProtocolException extends Exception
    {
        /**
         * 
         */
        private static final long serialVersionUID = 4024261898937341903L;

        public ProtocolException()
        {
        }

        public ProtocolException(String message)
        {
            super(message);
        }

        public ProtocolException(String message, Throwable cause)
        {
            super(message, cause);
        }

        public ProtocolException(Throwable cause)
        {
            super(cause);
        }
    }

    public static final String CHANNEL = References.MODID;

    private static final BiMap<Integer, Class<? extends BasePacket>> idMap;

    static
    {
        final ImmutableBiMap.Builder<Integer, Class<? extends BasePacket>> builder = ImmutableBiMap.builder();
        builder.put(0, EnchantPacket.class);
        builder.put(1, ConfigPacket.class);
        builder.put(2, ReConfigPacket.class);
        builder.put(3, GuiPacket.class);
        builder.put(4, RepairPacket.class);

        idMap = builder.build();
    }

    /**
     * Constructs the packet
     * 
     * @param packetId
     *            id of the packet
     * @return constructed packet
     * @throws ProtocolException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static BasePacket constructPacket(int packetId) throws ProtocolException, IllegalAccessException, InstantiationException
    {
        final Class<? extends BasePacket> clazz = idMap.get(packetId);
        if (clazz == null)
        {
            throw new ProtocolException("Unknown Packet Id!");
        } else
        {
            return clazz.newInstance();
        }
    }

    /**
     * Executes any actions required when packet is received
     * 
     * @param player
     *            the player requesting the action
     * @param side
     *            which side the packet is received (Client | Serve)
     */
    public abstract void execute(EntityPlayer player, Side side);

    final int getPacketId()
    {
        if (idMap.inverse().containsKey(getClass()))
        {
            return idMap.inverse().get(getClass());
        } else
        {
            throw new RuntimeException("Packet " + getClass().getSimpleName() + " is missing a mapping!");
        }
    }

    /**
     * Writes all data to and finalizes the packet
     * 
     * @return Finalized packet
     */
    public final Packet makePacket()
    {
        final ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeByte(getPacketId());
        write(output);
        return PacketDispatcher.getPacket(CHANNEL, output.toByteArray());
    }

    /**
     * Reads data from the packet
     * 
     * @param input
     *            array being read from
     */
    public abstract void read(ByteArrayDataInput input);

    /**
     * Writes data to the packet
     * 
     * @param output
     *            array being wrote to
     */
    protected abstract void write(ByteArrayDataOutput output);
}
