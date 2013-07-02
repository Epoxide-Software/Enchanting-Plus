package eplus.network.packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.relauncher.Side;
import eplus.EnchantingPlus;
import eplus.lib.ConfigurationSettings;
import eplus.lib.References;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class GuiPacket extends BasePacket {

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
    protected void write(ByteArrayDataOutput output)
    {
        output.writeUTF(username);
        output.writeInt(guiId);
        output.writeInt(xPos);
        output.writeInt(yPos);
        output.writeInt(zPos);
    }

    @Override
    public void read(ByteArrayDataInput input)
    {
        this.username = input.readUTF();
        this.guiId = input.readInt();
        this.xPos = input.readInt();
        this.yPos = input.readInt();
        this.zPos = input.readInt();
    }

    @Override
    public void execute(EntityPlayer player, Side side)
    {
        if (side == Side.SERVER) {
            NBTTagCompound tag = player.getEntityData();

            if (tag.getBoolean(References.MODID + ":useMod")
                    && ConfigurationSettings.useMod) {
                player.openGui(EnchantingPlus.INSTANCE, guiId, player.worldObj,
                        xPos, yPos, zPos);
            }
        }
    }
}
