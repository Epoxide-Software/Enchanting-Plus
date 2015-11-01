package net.epoxide.eplus.common.network;

import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.network.AbstractMessage;
import net.epoxide.eplus.inventory.ContainerEnchantTable;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

/**
 * This class was created on 10/31/2015 by lclc98.
 */
public class PacketEnchant extends AbstractMessage {
    protected int totalCost;
    protected HashMap<Integer, Integer> levels = new HashMap<Integer, Integer>();
    protected HashMap<Integer, Integer> enchants = new HashMap<Integer, Integer>();

    public PacketEnchant () {

    }

    public PacketEnchant (HashMap<Integer, Integer> enchants, HashMap<Integer, Integer> levels, int totalCost) {

        this.enchants = enchants;
        this.levels = levels;
        this.totalCost = totalCost;
    }

    @Override
    public void handleClientMessage (AbstractMessage message, EntityPlayer player) {

    }

    @Override
    public void handleServerMessage (AbstractMessage message, EntityPlayer player) {

        if (message instanceof PacketEnchant) {
            PacketEnchant packet = (PacketEnchant) message;
            if (player.openContainer instanceof ContainerEnchantTable) {
                try {
                    ((ContainerEnchantTable) player.openContainer).enchant(player, packet.enchants, packet.levels, packet.totalCost);
                } catch (final Exception e) {
                    //                    EnchantingPlus.log.info("Enchanting failed because: " + e.getLocalizedMessage());
                }
                player.openContainer.detectAndSendChanges();
            }
        }
    }

    @Override
    public void fromBytes (ByteBuf buf) {

        final HashMap<Integer, Integer> enchants = new HashMap<Integer, Integer>();
        final HashMap<Integer, Integer> levels = new HashMap<Integer, Integer>();

        this.totalCost = buf.readInt();

        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            enchants.put(buf.readInt(), buf.readInt());
        }

        size = buf.readInt();

        for (int i = 0; i < size; i++) {
            levels.put(buf.readInt(), buf.readInt());
        }

        this.enchants = enchants;
        this.levels = levels;
    }

    @Override
    public void toBytes (ByteBuf buf) {

        buf.writeInt(totalCost);
        buf.writeInt(enchants.size());

        for (Integer enchantmentId : enchants.keySet()) {
            buf.writeInt(enchantmentId);
            buf.writeInt(enchants.get(enchantmentId));
        }
        buf.writeInt(levels.size());

        for (Integer enchantmentId : levels.keySet()) {
            buf.writeInt(enchantmentId);
            buf.writeInt(levels.get(enchantmentId));
        }
    }
}
