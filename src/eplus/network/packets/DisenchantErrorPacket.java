package eplus.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import eplus.gui.GuiModTable;

public class DisenchantErrorPacket extends BasePacket
{
    private String error;

    public DisenchantErrorPacket()
    {
    }

    public DisenchantErrorPacket(String error)
    {
        this.error = error;
    }

    @Override
    public void execute(EntityPlayer player, Side side)
    {
        if (side == Side.CLIENT)
        {
            Minecraft minecraft = FMLClientHandler.instance().getClient();

            if (minecraft.currentScreen instanceof GuiModTable)
            {
                ((GuiModTable)minecraft.currentScreen).error = this.error; 
            }
        }

    }

    @Override
    public void read(ByteArrayDataInput input)
    {
        this.error = input.readUTF();

    }

    @Override
    protected void write(ByteArrayDataOutput output)
    {
        output.writeUTF(error);

    }

}
