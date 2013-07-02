package eplus.gui;

import cpw.mods.fml.common.network.PacketDispatcher;
import eplus.lib.ConfigurationSettings;
import eplus.lib.GuiIds;
import eplus.network.packets.GuiPacket;
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
public class GuiVanillaTable extends GuiEnchantment {
    private final EntityPlayer player;
    private final World world;
    private final int xPos;
    private final int yPos;
    private final int zPos;

    public GuiVanillaTable(InventoryPlayer par1InventoryPlayer,
                           World par2World, int par3, int par4, int par5, String par6Str)
    {
        super(par1InventoryPlayer, par2World, par3, par4, par5, par6Str);
        this.player = par1InventoryPlayer.player;
        this.world = par2World;
        this.xPos = par3;
        this.yPos = par4;
        this.zPos = par5;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        String s = "Mod";
        if (ConfigurationSettings.useMod) {
            this.buttonList.add(new GuiButton(0, guiLeft + xSize + 10,
                    guiTop + 5, fontRenderer.getStringWidth(s) + 10, 20, s));
        }
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        super.actionPerformed(par1GuiButton);

        switch (par1GuiButton.id) {
            case 0:
                PacketDispatcher.sendPacketToServer(new GuiPacket(player.username,
                        GuiIds.ModTable, xPos, yPos, zPos).makePacket());
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);
        String displayText = String.format("Player XP Level: %s",
                player.experienceLevel);
        drawCreativeTabHoveringText(displayText,
                guiLeft - 20 - fontRenderer.getStringWidth(displayText), guiTop
                + fontRenderer.FONT_HEIGHT + 8);
    }
}
