package net.darkhax.eplus.client.gui.n;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonScroller extends GuiButton {
    
    public int sliderY = 1;
    protected static final ResourceLocation TEXTURE = new ResourceLocation("eplus", "textures/gui/enchant.png");
    public GuiAdvancedTable parent;
    
    public GuiButtonScroller(GuiAdvancedTable parent, int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
        this.parent = parent;
        
    }
    
    public GuiButtonScroller(GuiAdvancedTable parent,int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.parent = parent;
    }
    
    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if(this.visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(x, y + this.sliderY, parent.isSliding ? getButtonWidth():0, 182, getButtonWidth(), height);
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;
            
            if(packedFGColour != 0) {
                j = packedFGColour;
            } else if(!this.enabled) {
                j = 10526880;
            } else if(this.hovered) {
                j = 16777120;
            }
            
            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
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
