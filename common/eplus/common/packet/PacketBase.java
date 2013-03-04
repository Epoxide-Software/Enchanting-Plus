package eplus.common.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class PacketBase {
    public EntityPlayer player;

    public abstract void handlePacket(DataInputStream var1) throws Exception;

    public static Packet250CustomPayload createPacket(int var1, byte[] var2)
    {
        Packet250CustomPayload var3 = null;
        try {
            ByteArrayOutputStream var4 = new ByteArrayOutputStream();
            DataOutputStream var5 = new DataOutputStream(var4);
            var5.writeInt(var1);
            for (byte var6 : var2) {
                var5.writeByte(var6);
            }
            var3 = new Packet250CustomPayload("eplus", var4.toByteArray());
            var4.close();
            var5.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return var3;
    }
}
