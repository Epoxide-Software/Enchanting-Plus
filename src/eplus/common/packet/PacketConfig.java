package eplus.common.packet;

import eplus.common.EnchantingPlus;

import java.io.DataInputStream;

public class PacketConfig extends PacketBase
{

    @Override
    public void handlePacket(DataInputStream var1) throws Exception
    {
        EnchantingPlus.useMod = var1.readBoolean();
        EnchantingPlus.allowDisenchanting = var1.readBoolean();
        EnchantingPlus.allowRepair = var1.readBoolean();
        EnchantingPlus.allowTransfer = var1.readBoolean();

        EnchantingPlus.allowDestroyItemOnDisenchanting = var1.readBoolean(); // created by Slash
        EnchantingPlus.allowPocketDisenchanting = var1.readBoolean(); // created by Slash
        EnchantingPlus.allowPocketRepair = var1.readBoolean(); // created by Slash
        EnchantingPlus.allowPocketTransfer = var1.readBoolean(); // created by Slash
        EnchantingPlus.needBookShelves = var1.readBoolean(); // created by Slash
        EnchantingPlus.strictEnchant = var1.readBoolean(); // created by Slash
        EnchantingPlus.enchantFactor = var1.readDouble(); // created by Slash
        EnchantingPlus.disenchantFactor = var1.readDouble(); // created by Slash
        EnchantingPlus.repairFactor = var1.readDouble(); // created by Slash
        EnchantingPlus.transferFactor = var1.readDouble(); // created by Slash

        EnchantingPlus.unbreakingAll = var1.readBoolean();
    }

}
