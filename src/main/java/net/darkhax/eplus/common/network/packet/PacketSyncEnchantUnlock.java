package net.darkhax.eplus.common.network.packet;

import net.darkhax.bookshelf.network.SerializableMessage;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.eplus.handler.PlayerHandler;
import net.darkhax.eplus.handler.PlayerHandler.ICustomData;
import net.darkhax.eplus.libs.EPlusUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncEnchantUnlock extends SerializableMessage {

    public String enchId;
    public boolean isUsable;

    public PacketSyncEnchantUnlock () {

    }

    public PacketSyncEnchantUnlock (Enchantment enchant, boolean isUsable) {

        this.enchId = EPlusUtils.getEnchId(enchant);
        this.isUsable = isUsable;
    }

    @Override
    public IMessage handleMessage (MessageContext context) {

        final Enchantment enchant = EPlusUtils.getEnchantmentFromId(this.enchId);

        if (enchant != null) {

            final ICustomData data = PlayerHandler.getPlayerData(PlayerUtils.getClientPlayer());

            if (this.isUsable) {

                data.unlockEnchantment(enchant);
            }

            else {

                data.lockEnchantment(enchant);
            }
        }

        return null;
    }
}