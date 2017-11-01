package net.darkhax.eplus.common.network.packet;

import java.util.Arrays;
import java.util.List;

import net.darkhax.bookshelf.network.SerializableMessage;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.eplus.inventory.n.ContainerAdvancedTable;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Packet for repairing an item from the enchanting plus GUI.
 */
public class PacketEnchantItem extends SerializableMessage {

    private static final long serialVersionUID = 4391316650621991566L;

    /**
     * The enchant cost that the client thinks it should have to pay.
     */
    public int enchantCost;

    public EnchantmentData[] enchantments;

    public PacketEnchantItem () {

    }

    public PacketEnchantItem (int cost, List<EnchantmentData> enchants) {

        this.enchantCost = cost;
        this.enchantments = enchants.toArray(new EnchantmentData[enchants.size()]);
    }

    @Override
    public IMessage handleMessage (MessageContext context) {

        final EntityPlayer player = context.side == Side.CLIENT ? PlayerUtils.getClientPlayer() : context.getServerHandler().player;

        if (player.openContainer instanceof ContainerAdvancedTable) {
            //TODO implement
//            ((ContainerAdvancedTable) player.openContainer).updateItemStack(player, Arrays.asList(this.enchantments), this.enchantCost);
//            player.openContainer.detectAndSendChanges();
        }

        return null;
    }
}