package com.aesireanempire.eplus.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by freyja
 */
public interface IPacket
{
    public void readBytes(ByteBuf bytes);
    public void writeBytes(ByteBuf bytes);
    public void executeClient(EntityPlayer player);
    public void executeServer(EntityPlayer player);

}
