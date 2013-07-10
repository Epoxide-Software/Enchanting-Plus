package eplus.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;
import eplus.lib.ConfigurationSettings;
import eplus.lib.GuiIds;
import eplus.lib.Strings;
import eplus.network.packets.GuiPacket;

/**
 * Enchanting Plus
 * 
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class GuiVanillaTable extends GuiEnchantment
{
    private final EntityPlayer player;
    private final int xPos;
    private final int yPos;
    private final int zPos;

    public GuiVanillaTable(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5, String par6Str)
    {
        super(par1InventoryPlayer, par2World, par3, par4, par5, par6Str);
        player = par1InventoryPlayer.player;
        xPos = par3;
        yPos = par4;
        zPos = par5;
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        super.actionPerformed(par1GuiButton);

        switch (par1GuiButton.id) {
            case 0:
                PacketDispatcher.sendPacketToServer(new GuiPacket(player.username, GuiIds.ModTable, xPos, yPos, zPos).makePacket());
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);
        final String displayText = String.format("%s: %s", Strings.playerLevel, player.experienceLevel);
        drawCreativeTabHoveringText(displayText, guiLeft - 20 - fontRenderer.getStringWidth(displayText), guiTop + fontRenderer.FONT_HEIGHT + 8);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();
        final String s = "Mod";
        if (ConfigurationSettings.useMod)
        {
            buttonList.add(new GuiButton(0, guiLeft + xSize + 10, guiTop + 5, fontRenderer.getStringWidth(s) + 10, 20, s));
        }
    }
}
