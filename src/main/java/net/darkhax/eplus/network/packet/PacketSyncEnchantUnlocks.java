package net.darkhax.eplus.network.packet;

import java.util.HashSet;
import java.util.Set;

import net.darkhax.bookshelf.lib.Constants;
import net.darkhax.bookshelf.network.SerializableMessage;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.eplus.handler.PlayerHandler;
import net.darkhax.eplus.libs.EPlusUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This packet is used to sync all of the unlocked enchantments from the server to the client.
 */
public class PacketSyncEnchantUnlocks extends SerializableMessage {

    /**
     * An array of all the ids that have been unlocked. This array may contain null/empty
     * entries in cases where the enchantment is null or invalid.
     */
    public String[] enchIds;

    public PacketSyncEnchantUnlocks () {

    }

    public PacketSyncEnchantUnlocks (Set<Enchantment> enchantments) {

        this.enchIds = new String[enchantments.size()];
        int index = 0;

        for (final Enchantment ench : enchantments) {

            this.enchIds[index] = EPlusUtils.getEnchId(ench);
            index++;
        }
    }

    @Override
    public IMessage handleMessage (MessageContext context) {

        final EntityPlayer player = PlayerUtils.getClientPlayer();
        if (this.enchIds != null) {

            final Set<Enchantment> unlocks = new HashSet<>();

            for (final String id : this.enchIds) {

                final Enchantment enchant = EPlusUtils.getEnchantmentFromId(id);

                if (enchant != null) {

                    unlocks.add(enchant);
                }
            }

            Constants.LOG.info("Syncing {} unlocked enchantments for {}", this.enchIds.length, player.getName());
            PlayerHandler.getPlayerData(player).overrideUnlocks(unlocks);
        }

        return null;
    }
}