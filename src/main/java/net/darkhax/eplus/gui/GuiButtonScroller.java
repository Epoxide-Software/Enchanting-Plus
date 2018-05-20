package net.darkhax.eplus.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonScroller extends GuiButton {

    public int sliderY = 1;
    protected static final ResourceLocation TEXTURE = new ResourceLocation("eplus", "textures/gui/enchant.png");
    public GuiAdvancedTable parent;

    public GuiButtonScroller (GuiAdvancedTable parent, int buttonId, int x, int y) {

        super(buttonId, x, y, "");
        this.parent = parent;

    }

    public GuiButtonScroller (GuiAdvancedTable parent, int buttonId, int x, int y, int widthIn, int heightIn) {

        super(buttonId, x, y, widthIn, heightIn, "");
        this.parent = parent;
    }

    @Override
    public void drawButton (Minecraft mc, int mouseX, int mouseY, float partialTicks) {

        if (this.visible) {

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            mc.getTextureManager().bindTexture(TEXTURE);
            this.drawTexturedModalRect(this.x, this.y + this.sliderY, this.parent.isSliding ? this.getButtonWidth() : 0, 182, this.getButtonWidth(), this.height);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    @Override
    protected void mouseDragged (Minecraft mc, int mouseX, int mouseY) {

        super.mouseDragged(mc, mouseX, mouseY);
        if (this.parent.isSliding) {
            this.sliderY = mouseY - this.y - 7;
            this.sliderY = Math.max(1, this.sliderY);
            this.sliderY = Math.min(56, this.sliderY);
            this.parent.updateLabels();
            final int div = this.height / Math.max(this.parent.enchantmentListAll.size(), 1) + 4;
            this.parent.listOffset = this.sliderY / div;
            this.parent.listOffset = Math.max(this.parent.listOffset, 0);
            this.parent.listOffset = Math.min(this.parent.listOffset, this.parent.enchantmentListAll.size() - 4);
        }
    }
}
