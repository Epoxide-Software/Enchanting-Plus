package net.darkhax.eplus.network.messages;

import net.darkhax.bookshelf.network.SerializableMessage;
import net.darkhax.eplus.inventory.ContainerAdvancedTable;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRepair extends SerializableMessage {

    public MessageRepair () {

    }

    @Override
    public IMessage handleMessage (MessageContext context) {

        final Container container = context.getServerHandler().player.openContainer;

        if (container instanceof ContainerAdvancedTable) {

            ((ContainerAdvancedTable) container).logic.repairItem();
        }

        return null;
    }
}
