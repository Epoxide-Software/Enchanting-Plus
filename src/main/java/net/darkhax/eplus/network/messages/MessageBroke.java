/* FOR TEST.
package net.darkhax.eplus.network.messages;

import net.darkhax.bookshelf.network.SerializableMessage;
import net.darkhax.eplus.inventory.ContainerAdvancedTable;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageBroke extends SerializableMessage {

    public MessageBroke () {

    }

    @Override
    public IMessage handleMessage (MessageContext context) {

        final Container container = context.getServerHandler().player.openContainer;

        if (container instanceof ContainerAdvancedTable) {

            ((ContainerAdvancedTable) container).logic.brokeItem();
        }

        return null;
    }
}
*/