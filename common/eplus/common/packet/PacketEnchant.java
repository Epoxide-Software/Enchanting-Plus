package eplus.common.packet;

import java.io.DataInputStream;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import eplus.common.ContainerEnchanting;

public class PacketEnchant extends PacketBase {

    @Override
    public void handlePacket(DataInputStream dataInputStream) throws Exception
    {
        int enchantmentCost = dataInputStream.readByte();
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
            EntityPlayerMP playerEntity = (EntityPlayerMP) player;
            Container container = player.openContainer;
            ((ContainerEnchanting) container).doEnchant(container.getSlot(0).getStack(), player, arrayorEnchantmentData, enchantmentCost, player.openContainer.windowId);

        }
    }

}
