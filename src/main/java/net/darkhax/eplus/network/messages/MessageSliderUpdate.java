package net.darkhax.eplus.network.messages;

import net.darkhax.bookshelf.lib.EnchantData;
import net.darkhax.bookshelf.network.SerializableMessage;
import net.darkhax.eplus.inventory.ContainerAdvancedTable;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSliderUpdate extends SerializableMessage {

    public EnchantData updatedEnchant;

    public MessageSliderUpdate () {

    }

    public MessageSliderUpdate (EnchantData updatedEnchant) {

        this.updatedEnchant = updatedEnchant;
    }

    @Override
    public IMessage handleMessage (MessageContext context) {

        final Container container = context.getServerHandler().player.openContainer;

        if (container instanceof ContainerAdvancedTable) {

            ((ContainerAdvancedTable) container).logic.updateEnchantment(this.updatedEnchant.enchantment, this.updatedEnchant.enchantmentLevel);
        }

        return null;
    }
}
