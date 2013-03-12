package eplus.client;

import eplus.common.EnchantingPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.enchantment.Enchantment;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("ALL")
public class GuiEnchantmentItem extends Gui
{
    public static int  startingX;
    public static int  startingY;

    public final Enchantment type;

    public int         level;
    public final int         xPos;
    public int         yPos;
    public final int         width;
    public final int         height;

    public boolean     draw;
    public boolean     enabled;
    public final boolean     isSlider;
    public boolean     sliding;

    public float       sliderScroll;

    public GuiEnchantmentItem(Enchantment var1, int var2, int var3)
    {
        type = var1;
        level = 0;
        xPos = var2 + startingX;
        yPos = var3 + startingY;
        width = 144;
        height = 18;
        draw = true;
        enabled = true;
        isSlider = var1.getMaxLevel() > 1;
    }

    public void draw(Minecraft var1, int var2, int var3)
    {
        if (!draw) { return; }
        if (!sliding)
        {
            sliderScroll = (float) level / type.getMaxLevel();
        }
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        if (sliderScroll > 1)
        {
            sliderScroll = 1;
        }
        if (sliderScroll < 0)
        {
            sliderScroll = 0;
        }
        var1.renderEngine.func_98187_b("/eplus/icons" + EnchantingPlus.getTranslatedTextureIndex() + ".png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        byte var5 = 0;
        int var6 = 0x7E3517;
        if (enabled)
        {
            if (isMouseOver(var2, var3))
            {
                var5 = 3;
                var6 = 0xC38EC7;
            }
            else if (level > 0)
            {
                var5 = 2;
                var6 = 0x7E3117;
            }
        }
        else
        {
            var5 = 1;
            var6 = 0x465E41;
        }
        this.drawTexturedModalRect(xPos, yPos, 0, 18 * var5, width, height);
        if (isSlider)
        {
            this.drawTexturedModalRect(xPos + (int) ((float) 136 * sliderScroll), yPos, 145, 18 * var5, 8, height);
        }
        String var7 = type.getTranslatedName(level);
        if (level == 0)
        {
        	if(var7.lastIndexOf(" ") == -1)
        	{
        	var7 = type.getName();
        	}
        	else
        	{
        		var7 = var7.substring(0, var7.lastIndexOf(" "));
        	}
        }

        var1.fontRenderer.drawString(var7, xPos + width / 2 - var1.fontRenderer.getStringWidth(var7) / 2, yPos + height / 2 - 4, var6);
    }

    public boolean isMouseOver(int var1, int var2)
    {
        return draw && enabled && var1 >= xPos && var2 >= yPos && var1 < xPos + width && var2 < yPos + height;
    }

    public void scroll(int var1)
    {
        sliderScroll = (float) var1 / (float) (width - 12);
        level = (int) Math.floor((double) (float) type.getMaxLevel() * sliderScroll);
        if (level < 0)
        {
            level = 0;
        }
        if (level > type.getMaxLevel())
        {
            level = type.getMaxLevel();
        }
    }

    public void press()
    {
        if (isSlider) { return; }
        if (level == 0)
        {
            level++;
        }
        else
        {
            level--;
        }
        // System.out.println("pressed");
    }
}