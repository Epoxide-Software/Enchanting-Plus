package net.darkhax.eplus.client.gui.n;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.client.config.GuiSlider;

public class GuiSliderEnchant extends GuiSlider {

    private final Enchantment enchantment;

    public GuiSliderEnchant (int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, Enchantment enchantment) {

        super(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr);
        this.enchantment = enchantment;
    }

    public GuiSliderEnchant (int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par, Enchantment enchantment) {

        super(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, par);
        this.enchantment = enchantment;
    }

    public GuiSliderEnchant (int id, int xPos, int yPos, String displayStr, double minVal, double maxVal, double currentVal, ISlider par, Enchantment enchantment) {

        super(id, xPos, yPos, displayStr, minVal, maxVal, currentVal, par);
        this.enchantment = enchantment;
    }

    @Override
    public void drawButton (Minecraft mc, int mouseX, int mouseY, float partial) {

        super.drawButton(mc, mouseX, mouseY, partial);
        if (!this.visible) {
            return;
        }

        // final int indexX = this.dragging ? (int)this.sliderValue : ((int)this.sliderValue)
        // <= this.enchantment.getMaxLevel() ? (int) (this.x + 1 + (this.width - 6) * (
        // ((int)this.sliderValue) / (double) this.enchantment.getMaxLevel())) : this.x + 1 +
        // this.width - 6;

        drawRect(this.x + (int) this.sliderValue, this.x + 1, this.x + (int) this.sliderValue + 5, this.y - 1 + this.height, 0xff000000);
        Minecraft.getMinecraft().fontRenderer.drawString(this.displayString, this.x + 5, this.y + this.height / 4, 0x55aaff00);
        drawRect(this.x, this.y + 1, this.x + this.width, this.y - 1 + this.height, !this.enabled ? 0x44aaffff : 0x44aa55ff);
    }

    @Override
    public void updateSlider () {

        super.updateSlider();

    }
}
