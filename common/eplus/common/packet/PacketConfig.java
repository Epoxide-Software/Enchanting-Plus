package eplus.common.packet;

import java.io.DataInputStream;

import eplus.common.EnchantingPlus;

public class PacketConfig extends PacketBase
{

    @Override
    public void handlePacket(DataInputStream var1) throws Exception
    {
        EnchantingPlus.useMod = var1.readBoolean();
        EnchantingPlus.allowDisenchanting = var1.readBoolean();
        EnchantingPlus.allowRepair = var1.readBoolean();
        EnchantingPlus.allowTransfer = var1.readBoolean();
    }

}
