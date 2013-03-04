package eplus.client;

import cpw.mods.fml.common.network.PacketDispatcher;
import eplus.common.EnchantingPlus;
import eplus.common.packet.PacketBase;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.inventory.Container;
import org.lwjgl.opengl.GL11;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class GuiContainerEnchanting extends GuiContainer
{

    public GuiContainerEnchanting(Container par1Container)
    {
        super(par1Container);
        // TODO Auto-generated constructor stub

        this.xSize = 208;
        this.ySize = 238;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {

        String name = "Enchanting Table";
        fontRenderer.drawString(name, getCenteredOffset(name), 5, 0x404040);
    }

    private int getCenteredOffset(String name)
    {
        return xSize / 2 - fontRenderer.getStringWidth(name) / 2;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        int tex = mc.renderEngine.getTexture("/enchant.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(tex);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    public void sendEnchantPacket(EnchantmentData[] var1, int var2)
    {
        sendPacket(var1, var2, EnchantingPlus.PACKET_ID_ENCHANT);
    }

    public void sendDisenchantPacket(EnchantmentData[] var1, int var2)
    {
        sendPacket(var1, var2, EnchantingPlus.PACKET_ID_DISENCHANT);
    }

    public void sendRepairPacket(int var1)
    {
        sendPacket(new EnchantmentData[]
        {}, var1, EnchantingPlus.PACKET_ID_REPAIR);
    }

    public void sendPacket(EnchantmentData[] var1, int var2, int var3)
    {
        try
        {
            ByteArrayOutputStream var4 = new ByteArrayOutputStream();
            DataOutputStream var5 = new DataOutputStream(var4);
            var5.writeInt(var2);
            if (var1.length > 0)
            {
                var5.writeInt(var1.length);
                for (EnchantmentData var6 : var1)
                {
                    var5.writeShort(var6.enchantmentobj.effectId);
                    var5.writeShort(var6.enchantmentLevel);
                }
            }
            PacketDispatcher.sendPacketToServer(PacketBase.createPacket(var3, var4.toByteArray()));
            var4.close();
            var5.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
