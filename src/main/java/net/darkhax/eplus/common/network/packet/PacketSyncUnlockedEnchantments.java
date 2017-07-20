package net.darkhax.eplus.common.network.packet;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.eplus.handler.PlayerHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * Packet for syncing unlocked enchantments to the client from server.
 */
public class PacketSyncUnlockedEnchantments implements IMessage {

    private List<String> enchantments;

    public PacketSyncUnlockedEnchantments () {

    }

    public PacketSyncUnlockedEnchantments (List<Enchantment> enchantments) {

        this.enchantments = new ArrayList<>();

        for (final Enchantment ench : enchantments) {
            this.enchantments.add(ench.getRegistryName().toString());
        }
    }

    @Override
    public void fromBytes (ByteBuf buf) {

        final int count = buf.readInt();
        this.enchantments = new ArrayList<>();

        for (int pos = 0; pos < count; pos++) {
            this.enchantments.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes (ByteBuf buf) {

        buf.writeInt(this.enchantments.size());

        for (final String enchant : this.enchantments) {
            ByteBufUtils.writeUTF8String(buf, enchant);
        }
    }

    public static class PacketHandler implements IMessageHandler<PacketSyncUnlockedEnchantments, IMessage> {

        @Override
        public IMessage onMessage (PacketSyncUnlockedEnchantments packet, MessageContext ctx) {

            final EntityPlayer player = PlayerUtils.getClientPlayer();
            final List<Enchantment> enchants = PlayerHandler.getUnlockedEnchantments(player);

            for (final String enchant : packet.enchantments) {

                final Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchant));

                if (ench != null && !enchants.contains(ench)) {
                    enchants.add(ench);
                }
            }

            return null;
        }
    }
}