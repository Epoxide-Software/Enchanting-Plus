package net.darkhax.eplus.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiIcon extends GuiButton {
    
    private final ResourceLocation texture = new ResourceLocation("eplus:textures/gui/enchant.png");
    private boolean customTexture;
    private int textureIndex;
    
    public GuiIcon(int id, int x, int y, int width, int height, String caption) {
        
        super(id, x, y, width, height, caption);
        
    }
    
    public GuiIcon(int id, int x, int y, String caption) {
        
        this(id, x, y, 16, 16, caption);
    }
    
    /**
     * Determines if GuiIcon has a customTexture
     *
     * @param texture index of the Texture
     * @return the Icon with according changes
     */
    public GuiIcon customTexture (int texture) {
        
        this.textureIndex = texture;
        this.customTexture = texture != 0;
        
        if (!this.customTexture) {
            
            this.width = 20;
            this.height = 20;
        }
        
        return this;
    }
    
    @Override
    public void drawButton (Minecraft mc, int x, int y, float partialTicks) {
        
        if (!this.customTexture)
            super.drawButton(mc, x, y, partialTicks);
        else {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            mc.renderEngine.bindTexture(this.texture);
            this.drawTexturedModalRect(this.x, this.y, 8 + this.textureIndex * 16, 182, this.width, this.height);
        }
    }
    
    public void setDisplayString (String displayString) {
        
        this.displayString = displayString;
    }
}