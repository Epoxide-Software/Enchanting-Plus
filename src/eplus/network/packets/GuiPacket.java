package eplus.network.packets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.Side;
import eplus.EnchantingPlus;
import eplus.lib.ConfigurationSettings;
import eplus.lib.References;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class GuiPacket extends BasePacket
{

    private String username;
    private int guiId;
    private int xPos;
    private int yPos;
    private int zPos;

    public GuiPacket()
    {
    }

    public GuiPacket(String username, int guiId, int xPos, int yPos, int zPos)
    {
        this.username = username;
        this.guiId = guiId;
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    @Override
    public void execute(EntityPlayer player, Side side)
    {
        if (side == Side.SERVER)
        {
            final NBTTagCompound tag = player.getEntityData();

            if (tag.getBoolean(References.MODID + ":useMod") && ConfigurationSettings.useMod)
            {
                player.openGui(EnchantingPlus.INSTANCE, guiId, player.worldObj, xPos, yPos, zPos);
            }
        }
    }

    @Override
    public void read(ByteArrayDataInput input)
    {
        username = input.readUTF();
        guiId = input.readInt();
        xPos = input.readInt();
        yPos = input.readInt();
        zPos = input.readInt();
    }

    @Override
    protected void write(ByteArrayDataOutput output)
    {
        output.writeUTF(username);
        output.writeInt(guiId);
        output.writeInt(xPos);
        output.writeInt(yPos);
        output.writeInt(zPos);
    }
}
