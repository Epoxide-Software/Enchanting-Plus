package eplus.network.packets;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import eplus.lib.References;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public abstract class BasePacket {

    public static final String CHANNEL = References.MODID;
    private static final BiMap<Integer, Class<? extends BasePacket>> idMap;

    static {
        ImmutableBiMap.Builder<Integer, Class<? extends BasePacket>> builder = ImmutableBiMap.builder();
        builder.put(0, EnchantPacket.class);
        builder.put(1, DisenchantPacket.class);

        idMap = builder.build();
    }

    public static BasePacket constructPacket(int packetId) throws ProtocolException, ReflectiveOperationException {
        Class<? extends BasePacket> clazz = idMap.get(Integer.valueOf(packetId));
        if (clazz == null) {
            throw new ProtocolException("Unknown Packet Id!");
        } else {
            return clazz.newInstance();
        }
    }

    public static class ProtocolException extends Exception {
        public ProtocolException() {
        }

        public ProtocolException(String message, Throwable cause) {
            super(message, cause);
        }

        public ProtocolException(String message) {
            super(message);
        }

        public ProtocolException(Throwable cause) {
            super(cause);
        }
    }

    public final int getPacketId() {
        if(idMap.inverse().containsKey(getClass())) {
            return idMap.inverse().get(getClass()).intValue();
        } else {
            throw new RuntimeException("Packet " + getClass().getSimpleName() + " is missing a mapping!");
        }
    }

    public final Packet makePacket() {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeByte(getPacketId());
        write(output);
        return PacketDispatcher.getPacket(CHANNEL, output.toByteArray());
    }

    public abstract void write(ByteArrayDataOutput output);

    public abstract void read(ByteArrayDataInput input);

    public abstract void execute(EntityPlayer player, Side side);
}
