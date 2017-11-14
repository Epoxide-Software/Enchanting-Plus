package net.darkhax.eplus.client.gui.n;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonScroller extends GuiButton {
    
    public int sliderY = 1;
    protected static final ResourceLocation TEXTURE = new ResourceLocation("eplus", "textures/gui/enchant.png");
    public GuiAdvancedTable parent;
    
    public GuiButtonScroller(GuiAdvancedTable parent, int buttonId, int x, int y) {
        super(buttonId, x, y, "");
        this.parent = parent;
        
    }
    
    public GuiButtonScroller(GuiAdvancedTable parent,int buttonId, int x, int y, int widthIn, int heightIn) {
        super(buttonId, x, y, widthIn, heightIn, "");
        this.parent = parent;
    }
    
    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if(this.visible) {
            
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            mc.getTextureManager().bindTexture(TEXTURE);
            this.drawTexturedModalRect(x, y + this.sliderY, parent.isSliding ? getButtonWidth() : 0, 182, getButtonWidth(), height);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }
    
    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        super.mouseDragged(mc, mouseX, mouseY);
        if(parent.isSliding) {
            sliderY = mouseY - y-7;
            sliderY = Math.max(1, sliderY);
            sliderY = Math.min(56, sliderY);
            parent.updateLabels();
            int div = height/parent.enchantmentListAll.size()+4;
            parent.listOffset = sliderY / div;
            parent.listOffset = Math.max(parent.listOffset, 0);
            parent.listOffset = Math.min(parent.listOffset, parent.enchantmentListAll.size()-4);
        }
    }
}
