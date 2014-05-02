package com.aesireanempire.eplus.gui;

import com.aesireanempire.eplus.EnchantingPlus;
import com.aesireanempire.eplus.lib.ConfigurationSettings;
import com.aesireanempire.eplus.lib.GuiIds;
import com.aesireanempire.eplus.lib.Strings;
import com.aesireanempire.eplus.network.packets.GuiPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

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

        switch (par1GuiButton.id)
        {
            case 0:
                EnchantingPlus.sendPacketToServer(new GuiPacket(player.getDisplayName(), GuiIds.ModTable, xPos, yPos, zPos));
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);
        final String displayText = String.format("%s: %s", Strings.playerLevel, player.experienceLevel);
        drawCreativeTabHoveringText(displayText, guiLeft - 20 - fontRendererObj.getStringWidth(displayText), guiTop + fontRendererObj.FONT_HEIGHT + 8);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();
        final String s = "Mod";
        if (ConfigurationSettings.useMod)
        {
            buttonList.add(new GuiButton(0, guiLeft + xSize + 10, guiTop + 5, fontRendererObj.getStringWidth(s) + 10, 20, s));
        }
    }
}
