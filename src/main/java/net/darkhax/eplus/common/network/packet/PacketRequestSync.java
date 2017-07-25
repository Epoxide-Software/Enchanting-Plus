package net.darkhax.eplus.common.network.packet;

import net.darkhax.bookshelf.network.SerializableMessage;
import net.darkhax.eplus.handler.PlayerHandler;
import net.darkhax.eplus.libs.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * A packet the client can send any time to request their unlocked enchantment list be
 * completely updated.
 */
public class PacketRequestSync extends SerializableMessage {

    public PacketRequestSync () {

    }

    @Override
    public IMessage handleMessage (MessageContext context) {

        Constants.LOG.info("Recieved sync request from {}", context.getServerHandler().player.getName());
        PlayerHandler.getPlayerData(context.getServerHandler().player).syncData();
        return null;
    }
}
