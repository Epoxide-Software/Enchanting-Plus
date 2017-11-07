package net.darkhax.eplus.client.gui;

import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.darkhax.eplus.client.gui.n.GuiAdvancedTable;
import net.darkhax.eplus.handler.ConfigurationHandler;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class GuiEnchantmentLabel extends Gui {

    private final TileEntityAdvancedTable tile;
    public final Enchantment enchantment;
    public final int initialLevel;
    public int currentLevel;

    public int startingXPos;
    public int startingYPos;
    public int xPos;
    public int yPos;
    public final int height = 18;
    public final int width = 143;

    private int sliderX;
    public boolean dragging = false;

    public boolean visible = true;
    public boolean locked = false;

    public final GuiAdvancedTable parent;

    /**
     * Creates a new enchantment label. This is used to represent an enchantment in the GUI.
     *
     * @param container The tile used.
     * @param enchant The enchantment to represent.
     * @param level The current level of the enchantment to depict.
     * @param x The X position of the label.
     * @param y The Y position of the label.
     */
    public GuiEnchantmentLabel (GuiAdvancedTable parent, TileEntityAdvancedTable tile, Enchantment enchant, int level, int x, int y) {

        this.parent = parent;
        this.tile = tile;
        this.enchantment = enchant;
        this.currentLevel = level;
        this.initialLevel = level;
        this.xPos = this.startingXPos = x;
        this.yPos = this.startingYPos = y;
        this.sliderX = this.xPos + 1;
    }

    /**
     * Handles the rendering of an enchantment label.
     *
     * @param font The font renderer. Allows rendering font.
     */
    public void draw (FontRenderer font) {
        
        if (!this.visible) {
            return;
        }

        final int indexX = this.dragging ? this.sliderX : this.currentLevel <= this.enchantment.getMaxLevel() ? (int) (this.xPos + 1 + (this.width - 6) * (this.currentLevel / (double) this.enchantment.getMaxLevel())) : this.xPos + 1 + this.width - 6;
        drawRect(this.xPos, this.yPos + 1, this.xPos + this.width, this.yPos - 1 + this.height, this.locked ? 0x44d10841 : 0x445aaeae);
        drawRect(indexX, this.yPos + 1, indexX + 5, this.yPos - 1 + this.height, this.parent.selected != null && this.parent.selected == this ? 0xFFFF00FF : 0xFF000000);
    
        font.drawString(this.getDisplayName(), this.xPos + 5, this.yPos + 6, 0x55aaff00);
    }
    
    /**
     * Used to get the translated name of the enchantment. If the enchantment is of level 0,
     * the level bit is cut off.
     *
     * @return The name to display for the label.
     */
    public String getDisplayName () {
    
        String s = I18n.translateToLocal(enchantment.getName());
    
        if (enchantment.isCurse())
        {
            s = TextFormatting.RED + s;
        }
    
        return currentLevel <= 0 ? s : s + " " + I18n.translateToLocal("enchantment.level." + currentLevel);
    }

    /**
     * Updates the state of the slider. This is used to handle changing the current level of
     * the enchantment being represented.
     *
     * @param xPos The xPos of the slider.
     * @param prevX The previous slider position.
     */
    public void updateSlider (int xPos, int prevX) {

        if (this.locked) {
            return;
        }
        if (xPos > prevX + this.width) {
            return;
        }
        this.sliderX = prevX + xPos+2;

        if (this.sliderX <= prevX) {
            this.sliderX = prevX;
        }

        if (this.sliderX >= prevX + this.width - 6) {
            this.sliderX = prevX + this.width - 6;
        }

        final float index = (xPos) / (float) (this.width - 10);
        final int tempLevel = (int) Math.round(this.initialLevel > this.enchantment.getMaxLevel() ? this.initialLevel * index : this.enchantment.getMaxLevel() * index);

        if (tempLevel >= this.initialLevel || ConfigurationHandler.allowDisenchanting && !this.tile.inventory.getStackInSlot(0).isItemDamaged()) {
            this.currentLevel = tempLevel;
        }

        if (this.currentLevel <= 0) {
            this.currentLevel = 0;
        }
    }

    /**
     * Sets whether or not the label should be shown.
     *
     * @param isVisible Whether or not the label is visible.
     */
    public void setVisible (boolean isVisible) {

        this.visible = isVisible;
    }
}