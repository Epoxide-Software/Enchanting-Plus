package eplus.common.packet;

import java.io.DataInputStream;
import java.util.ArrayList;

import net.minecraft.src.Container;
import net.minecraft.src.Enchantment;
import net.minecraft.src.EnchantmentData;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Slot;
import eplus.client.GuiEnchantmentPlus;
import eplus.common.ContainerEnchanting;

public class PacketDisenchant extends PacketBase
{

    @Override
    public void handlePacket(DataInputStream dataInputStream) throws Exception
    {
        int enchantmentCost = dataInputStream.readByte();
        int numberofEnchantmentsToadd = dataInputStream.readByte();
        EnchantmentData[] arrayorEnchantmentData = new EnchantmentData[numberofEnchantmentsToadd];
        for (int var5 = 0; var5 < numberofEnchantmentsToadd; var5++)
        {
            short enchantmentID = dataInputStream.readByte();
            short enchantmentLevel = dataInputStream.readByte();
            arrayorEnchantmentData[var5] = new EnchantmentData(Enchantment.enchantmentsList[enchantmentID], enchantmentLevel);
        }
        if (player.openContainer instanceof ContainerEnchanting)
        {
            EntityPlayerMP playerEntity = (EntityPlayerMP) player;
            Container container = player.openContainer;
            ((ContainerEnchanting) container).doDisenchant(container.getSlot(0).getStack(), player, arrayorEnchantmentData, enchantmentCost);

            
        }
    }

}
