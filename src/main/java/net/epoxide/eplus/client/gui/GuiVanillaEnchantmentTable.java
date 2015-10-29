package net.epoxide.eplus.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class GuiVanillaEnchantmentTable extends GuiEnchantment {
    private final EntityPlayer player;
    private final int xPos;
    private final int yPos;
    private final int zPos;
    
    public GuiVanillaEnchantmentTable(InventoryPlayer par1InventoryPlayer, World par2World, int x, int y, int z, String par6Str) {
        
        super(par1InventoryPlayer, par2World, x, y, z, par6Str);
        this.player = par1InventoryPlayer.player;
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
    }
    
    @Override
    public void drawScreen (int mouseX, int mouseY, float partialTicks) {
        
        super.drawScreen(mouseX, mouseY, partialTicks);
        final String displayText = String.format("%s: %s", "Player Level", player.experienceLevel);
        drawCreativeTabHoveringText(displayText, guiLeft - 20 - fontRendererObj.getStringWidth(displayText), guiTop + fontRendererObj.FONT_HEIGHT + 8);
    }
    
    @Override
    public void initGui () {
        
        super.initGui();
        final String s = "Mod";
        // if (ConfigurationSettings.useMod)
        {
            buttonList.add(new GuiButton(0, guiLeft + xSize + 10, guiTop + 5, fontRendererObj.getStringWidth(s) + 10, 20, s));
        }
    }
}
