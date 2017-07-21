package net.darkhax.eplus.common.network.packet;

import java.util.List;

import net.darkhax.bookshelf.lib.Constants;
import net.darkhax.bookshelf.network.SerializableMessage;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.eplus.handler.PlayerHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * This packet is used to sync all of the unlocked enchantments from the server to the client.
 */
public class PacketSyncUnlockedEnchantments extends SerializableMessage {

    private static final long serialVersionUID = 828601957159834616L;

    /**
     * An array of all the ids that have been unlocked. This array may contain null/empty
     * entries in cases where the enchantment is null or invalid.
     */
    private String[] enchIds;

    public PacketSyncUnlockedEnchantments () {

    }

    public PacketSyncUnlockedEnchantments (List<Enchantment> enchantments) {

        this.enchIds = new String[enchantments.size()];
        int index = 0;

        for (final Enchantment ench : enchantments) {

            if (ench != null) {

                // Valid enchantment
                if (ench.getRegistryName() != null) {

                    this.enchIds[index] = ench.getRegistryName().toString();
                }

                else {

                    Constants.LOG.warn("Enchantment lacks an id! " + ench.getName());
                }
            }

            else {

                Constants.LOG.warn("A null enchantment was unlocked!");
            }

            index++;
        }
    }

    @Override
    public IMessage handleMessage (MessageContext context) {

        final EntityPlayer player = PlayerUtils.getClientPlayer();
        final List<Enchantment> enchants = PlayerHandler.getUnlockedEnchantments(player);

        for (final String enchant : this.enchIds) {

            final Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchant));

            if (ench != null && !enchants.contains(ench)) {

                enchants.add(ench);
            }
        }

        return null;
    }
}