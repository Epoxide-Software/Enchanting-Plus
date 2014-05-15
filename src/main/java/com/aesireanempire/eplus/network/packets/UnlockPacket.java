package com.aesireanempire.eplus.network.packets;

import com.aesireanempire.eplus.inventory.ContainerEnchantTable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

/**
 * Created by freyja
 */
public class UnlockPacket implements IPacket
{
    private HashMap<Integer, Integer> enchants;

    public UnlockPacket()
    {

    }

    public UnlockPacket(HashMap<Integer, Integer> enchants)
    {
        this.enchants = enchants;
    }

    @Override
    public void readBytes(ByteBuf bytes)
    {
        final HashMap<Integer, Integer> enchants = new HashMap<Integer, Integer>();

        int size = bytes.readInt();

        for (int i = 0; i < size; i++)
        {
            enchants.put(bytes.readInt(), bytes.readInt());
        }

        this.enchants = enchants;
    }

    @Override
    public void writeBytes(ByteBuf bytes)
    {
        bytes.writeInt(enchants.size());

        for (Integer enchantmentId : enchants.keySet())
        {
            bytes.writeInt(enchantmentId);
            bytes.writeInt(enchants.get(enchantmentId));
        }
    }

    @Override public void executeClient(EntityPlayer player)
    {

    }

    @Override public void executeServer(EntityPlayer player)
    {
        if (player.openContainer instanceof ContainerEnchantTable)
        {
            ((ContainerEnchantTable) player.openContainer).unlock(player, enchants);

            player.openContainer.detectAndSendChanges();
        }
    }
}
