package com.aesireanempire.eplus.network.packets;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.NetworkRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

/**
 * Created by freyja
 */
public class ChannelHandler extends FMLIndexedMessageToMessageCodec<IPacket>
{
    public ChannelHandler()
    {
        addDiscriminator(0, EnchantPacket.class);
        addDiscriminator(1, RepairPacket.class);
        addDiscriminator(2, ErrorPacket.class);
        addDiscriminator(3, GuiPacket.class);
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, IPacket packet, ByteBuf data) throws Exception
    {
        packet.writeBytes(data);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf data, IPacket packet)
    {
        packet.readBytes(data);
        switch (FMLCommonHandler.instance().getEffectiveSide())
        {
            case CLIENT:
                INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
                packet.executeClient(((NetHandlerPlayServer) netHandler).playerEntity);
            case SERVER:
                netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
                packet.executeServer(((NetHandlerPlayServer) netHandler).playerEntity);
                break;
        }
    }
}
