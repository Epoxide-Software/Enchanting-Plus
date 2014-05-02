package com.aesireanempire.eplus.network.packets;

import com.aesireanempire.eplus.EnchantingPlus;
import com.aesireanempire.eplus.inventory.ContainerEnchantTable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

/**
 * Created by freyja
 */
public class EnchantPacket implements IPacket
{
    protected int totalCost;
    protected HashMap<Integer, Integer> levels = new HashMap<Integer, Integer>();
    protected HashMap<Integer, Integer> enchants = new HashMap<Integer, Integer>();

    public EnchantPacket()
    {

    }

    public EnchantPacket(HashMap<Integer, Integer> enchants, HashMap<Integer, Integer> levels, int totalCost)
    {
        this.enchants = enchants;
        this.levels = levels;
        this.totalCost = totalCost;
    }

    @Override
    public void readBytes(ByteBuf bytes)
    {
        final HashMap<Integer, Integer> enchants = new HashMap<Integer, Integer>();
        final HashMap<Integer, Integer> levels = new HashMap<Integer, Integer>();

        this.totalCost = bytes.readInt();

        int size = bytes.readInt();

        for(int i = 0; i < size; i++)
        {
            enchants.put(bytes.readInt(), bytes.readInt());
        }

        size = bytes.readInt();

        for(int i = 0; i < size; i++)
        {
            levels.put(bytes.readInt(), bytes.readInt());
        }

        this.enchants = enchants;
        this.levels = levels;
    }

    @Override
    public void writeBytes(ByteBuf bytes)
    {
        bytes.writeInt(totalCost);
        bytes.writeInt(enchants.size());

        for (Integer enchantmentId : enchants.keySet())
        {
            bytes.writeInt(enchantmentId);
            bytes.writeInt(enchants.get(enchantmentId));
        }

        bytes.writeInt(levels.size());

        for (Integer enchantmentId : levels.keySet())
        {
            bytes.writeInt(enchantmentId);
            bytes.writeInt(levels.get(enchantmentId));
        }
    }

    @Override
    public void executeClient(EntityPlayer player)
    {

    }

    @Override
    public void executeServer(EntityPlayer player)
    {
        if(player.openContainer instanceof ContainerEnchantTable)
        {
            try
            {
                ((ContainerEnchantTable) player.openContainer).enchant(player, enchants, levels, totalCost);
            } catch (final Exception e)
            {
                EnchantingPlus.log.info("Enchanting failed because: " + e.getLocalizedMessage());
            }
            player.openContainer.detectAndSendChanges();
        }
    }
}
