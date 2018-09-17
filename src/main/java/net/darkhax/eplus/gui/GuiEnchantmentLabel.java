package net.darkhax.eplus.gui;

import net.darkhax.bookshelf.lib.EnchantData;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.darkhax.eplus.handler.ConfigurationHandler;
import net.darkhax.eplus.inventory.ContainerAdvancedTable;
import net.darkhax.eplus.network.messages.MessageSliderUpdate;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.text.TextFormatting;

public class GuiEnchantmentLabel extends Gui {

    private static final int HEIGHT = 18;
    private static final int WIDTH = 143;
    private final TileEntityAdvancedTable tile;
    private final Enchantment enchantment;
    private final int initialLevel;

    private int currentLevel;
    private int startingXPos;
    private int startingYPos;
    private int xPos;
    private int yPos;

    private int sliderX;
    private boolean dragging = false;

    private boolean visible = true;
    private boolean locked = false;

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

        if (this.currentLevel > this.enchantment.getMaxLevel()) {

            this.locked = true;
        }
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

        final int indexX = this.dragging ? this.sliderX : this.currentLevel <= this.enchantment.getMaxLevel() ? (int) (this.xPos + 1 + (GuiEnchantmentLabel.WIDTH - 6) * (this.currentLevel / (double) this.enchantment.getMaxLevel())) : this.xPos + 1 + GuiEnchantmentLabel.WIDTH - 6;

        drawRect(this.xPos, this.yPos + 1, this.xPos + GuiEnchantmentLabel.WIDTH, this.yPos - 1 + GuiEnchantmentLabel.HEIGHT, this.locked ? 0x44d10841 : 0x445aaeae);
        drawRect(indexX, this.yPos + 1, indexX + 5, this.yPos - 1 + GuiEnchantmentLabel.HEIGHT, this.isSelected() ? 0xFFFF00FF : 0xFF000000);

        font.drawString(this.getDisplayName(), this.xPos + 5, this.yPos + 6, 0x55aaff00);
    }

    public boolean isSelected () {

        return this.parent.selected != null && this.parent.selected.enchantment == this.enchantment;
    }

    /**
     * Used to get the translated name of the enchantment. If the enchantment is of level 0,
     * the level bit is cut off.
     *
     * @return The name to display for the label.
     */
    public String getDisplayName () {

        String s = I18n.format(this.enchantment.getName());

        if (this.enchantment.isCurse()) {
            s = TextFormatting.RED + s;
        }

        return this.currentLevel <= 0 ? s : s + " " + I18n.format("enchantment.level." + this.currentLevel);
    }

    /**
     * Updates the state of the slider. This is used to handle changing the current level of
     * the enchantment being represented.
     *
     * @param xPos The xPos of the slider.
     * @param baseX The previous slider position.
     */
    public void updateSlider (int xPos) {

        // If the slider is locked, prevent it from updating.
        if (this.locked) {

            return;
        }

        final int min = this.xPos + 1;
        final int max = min + GuiEnchantmentLabel.WIDTH - 6;

        // Updates the position of the slider. -2 to center the cursor on the mouse position.
        this.sliderX = min + xPos - 2;

        // Prevents the slider from being dragged less than the lowest position.
        if (this.sliderX < min) {

            this.sliderX = min;
        }

        // Prevents the slider from being dragged
        else if (this.sliderX > max) {

            this.sliderX = max;
        }

        final float index = xPos / (float) (GuiEnchantmentLabel.WIDTH - 10);
        final int updatedLevel = Math.round(this.initialLevel > this.enchantment.getMaxLevel() ? this.initialLevel * index : this.enchantment.getMaxLevel() * index);

        // Checks if the updated level can be applied.
        if (updatedLevel > this.initialLevel || ConfigurationHandler.allowDisenchanting && !this.tile.getItem().isItemDamaged()) {
            this.currentLevel = updatedLevel;
        }

        // Adjust to range.
        if (this.currentLevel < 0) {

            this.currentLevel = 0;
        }

        // Adjust to range.
        else if (this.currentLevel > this.enchantment.getMaxLevel()) {

            this.currentLevel = this.enchantment.getMaxLevel();
        }

        // Send the update to the server.
        EnchantingPlus.NETWORK.sendToServer(new MessageSliderUpdate(((ContainerAdvancedTable) this.parent.inventorySlots).pos, new EnchantData(this.enchantment, this.currentLevel)));
        this.parent.getTable().getLogic().updateEnchantment(this.enchantment, this.currentLevel);
    }

    public int getCurrentLevel () {

        return this.currentLevel;
    }

    public void setCurrentLevel (int currentLevel) {

        this.currentLevel = currentLevel;
    }

    public int getStartingXPos () {

        return this.startingXPos;
    }

    public void setStartingXPos (int startingXPos) {

        this.startingXPos = startingXPos;
    }

    public int getStartingYPos () {

        return this.startingYPos;
    }

    public void setStartingYPos (int startingYPos) {

        this.startingYPos = startingYPos;
    }

    public int getxPos () {

        return this.xPos;
    }

    public void setxPos (int xPos) {

        this.xPos = xPos;
    }

    public int getyPos () {

        return this.yPos;
    }

    public void setyPos (int yPos) {

        this.yPos = yPos;
    }

    public int getSliderX () {

        return this.sliderX;
    }

    public void setSliderX (int sliderX) {

        this.sliderX = sliderX;
    }

    public boolean isDragging () {

        return this.dragging;
    }

    public void setDragging (boolean dragging) {

        this.dragging = dragging;
    }

    public boolean isLocked () {

        return this.locked;
    }

    public void setLocked (boolean locked) {

        this.locked = locked;
    }

    public int getWidth () {

        return WIDTH;
    }

    public TileEntityAdvancedTable getTile () {

        return this.tile;
    }

    public boolean isVisible () {

        return this.visible;
    }

    public GuiAdvancedTable getParent () {

        return this.parent;
    }

    /**
     * Sets whether or not the label should be shown.
     *
     * @param isVisible Whether or not the label is visible.
     */
    public void setVisible (boolean isVisible) {

        this.visible = isVisible;
    }

    public int getHeight () {

        return HEIGHT;
    }

    public Enchantment getEnchantment () {

        return this.enchantment;
    }

    public int getInitialLevel () {

        return this.initialLevel;
    }

    public boolean isMouseOver (int mouseX, int mouseY) {

        return this.getxPos() <= mouseX && this.getxPos() + this.getWidth() >= mouseX && this.getyPos() <= mouseY && this.getyPos() + this.getHeight() >= mouseY;
    }
}