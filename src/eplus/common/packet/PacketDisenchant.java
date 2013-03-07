package eplus.common.packet;

import eplus.common.ContainerEnchanting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

import java.io.DataInputStream;

public class PacketDisenchant extends PacketBase {

    @Override
    public void handlePacket(DataInputStream dataInputStream) throws Exception
    {
        int overflow = dataInputStream.read();
        int enchantmentCost = dataInputStream.readByte();

        if(enchantmentCost < 0) {
            enchantmentCost += 256 * overflow;
        }
        else {
            enchantmentCost += 128 * overflow;
        }

        int numberofEnchantmentsToadd = dataInputStream.readByte();
        EnchantmentData[] arrayorEnchantmentData = new EnchantmentData[numberofEnchantmentsToadd];
        for (int var5 = 0; var5 < numberofEnchantmentsToadd; var5++) {
            short enchantmentID = dataInputStream.readByte();
            if (enchantmentID < 0)
                enchantmentID += 256;
            short enchantmentLevel = dataInputStream.readByte();
            arrayorEnchantmentData[var5] = new EnchantmentData(Enchantment.enchantmentsList[enchantmentID], enchantmentLevel);
        }
        if (player.openContainer instanceof ContainerEnchanting) {
            Container container = player.openContainer;
            ((ContainerEnchanting) container).doDisenchant(container.getSlot(0).getStack(), player, arrayorEnchantmentData, enchantmentCost);

        }
    }

}
