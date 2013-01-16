package eplus.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import org.lwjgl.opengl.GL11;

public class GuiIcon extends Gui
{
    public String     id;

    public static int startingX;

    public static int startingY;

    public int        index;

    public int        xPos;

    public int        yPos;

    public int        width;

    public int        height;

    public boolean    draw;

    public boolean    enabled;

    public boolean    isButton;

    public String     info = "";

    public GuiIcon(String var1, int var2, int var3, int var4)
    {
        id = var1;
        index = var2;
        xPos = startingX + var3;
        yPos = startingY + var4;
        draw = true;
        enabled = true;
        width = 16;
        height = 16;
    }

    public GuiIcon setButton()
    {
        isButton = true;
        return this;
    }

    public void draw(Minecraft var1, int var2, int var3)
    {
        if (!draw) { return; }
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        int var4 = var1.renderEngine.getTexture("/eplus/enchant.png");
        var1.renderEngine.bindTexture(var4);
        this.drawTexturedModalRect(xPos, yPos, 24 + index * 16, 238, width, height);
    }

    public boolean isMouseOver(int var1, int var2)
    {
        return draw && var1 >= xPos && var2 >= yPos && var1 < xPos + width && var2 < yPos + height;
    }

    public boolean canClick()
    {
        return isButton && draw && enabled;
    }

    public String getTranslatedEnabled()
    {
        if (enabled)
        {
            return "Yes";
        }
        else
        {
            return "No";
        }
    }

    public GuiIcon setInfo(String var1)
    {
        info = var1;
        return this;
    }
}