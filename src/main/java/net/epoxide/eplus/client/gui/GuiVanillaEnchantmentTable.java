package net.epoxide.eplus.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import net.epoxide.eplus.EnchantingPlus;
import net.epoxide.eplus.common.network.PacketGui;

public class GuiVanillaEnchantmentTable extends GuiEnchantment {
    
    private final EntityPlayer player;
    private final int xPos;
    private final int yPos;
    private final int zPos;
    
    public GuiVanillaEnchantmentTable(InventoryPlayer inventoryPlayer, World world, int x, int y, int z, String guiName) {
        
        super(inventoryPlayer, world, x, y, z, guiName);
        this.player = inventoryPlayer.player;
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
    }
    
    @Override
    protected void actionPerformed (GuiButton guiButton) {
        
        super.actionPerformed(guiButton);
        
        switch (guiButton.id) {
            case 0:
                EnchantingPlus.network.sendToServer(new PacketGui(player.getDisplayName(), 0, xPos, yPos, zPos));
        }
    }
    
    @Override
    public void drawScreen (int mouseX, int mouseY, float partialTicks) {
        
        super.drawScreen(mouseX, mouseY, partialTicks);
        final String displayText = String.format("%s: %s", StatCollector.translateToLocal("tooltip.eplus.playerlevel"), player.experienceLevel);
        drawCreativeTabHoveringText(displayText, guiLeft - 20 - fontRendererObj.getStringWidth(displayText), guiTop + fontRendererObj.FONT_HEIGHT + 8);
    }
    
    @Override
    public void initGui () {
        
        super.initGui();
        final String displayText = StatCollector.translateToLocal("gui.button.eplus.mod");
        buttonList.add(new GuiButton(0, guiLeft - 40, guiTop + 102, fontRendererObj.getStringWidth(displayText) + 10, 20, displayText));
    }
}
