package com.aesireanempire.eplus.network.packets;

import com.aesireanempire.eplus.gui.GuiModTable;
import cpw.mods.fml.client.FMLClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by freyja
 */
public class ErrorPacket implements IPacket
{
    protected String error;

    public ErrorPacket()
    {

    }

    public ErrorPacket(String localizedMessage)
    {
        this.error = localizedMessage;
    }

    @Override public void readBytes(ByteBuf bytes)
    {
        int length = bytes.readInt();
        error = bytes.readBytes(length).toString();
    }

    @Override public void writeBytes(ByteBuf bytes)
    {
        bytes.writeInt(error.length());
        bytes.writeBytes(error.getBytes());
    }

    @Override public void executeClient(EntityPlayer player)
    {
        Minecraft client = FMLClientHandler.instance().getClient();

        if (client.currentScreen instanceof GuiModTable)
        {
            ((GuiModTable) client.currentScreen).error = error;
        }
    }

    @Override public void executeServer(EntityPlayer player)
    {

    }
}
