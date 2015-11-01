package net.epoxide.eplus.client.gui;

import net.darkhax.bookshelf.lib.util.Utilities;
import net.epoxide.eplus.handler.EPlusConfigurationHandler;
import net.epoxide.eplus.inventory.ContainerEnchantTable;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.enchantment.Enchantment;

public class GuiItem extends Gui {
    private final ContainerEnchantTable container;
    public final Enchantment enchantment;
    public final int height = 18;
    private final int width = 143;
    public final int privateLevel;

    public int startingXPos;
    public int startingYPos;
    public int yPos;
    public boolean locked = false;
    public int xPos;
    public int enchantmentLevel;
    public boolean show = true;
    public boolean dragging = false;
    private int sliderX;
    public boolean disabled;

    public GuiItem (ContainerEnchantTable container, int id, int level, int x, int y) {

        this.container = container;
        this.enchantment = Utilities.getEnchantment(id);
        this.enchantmentLevel = level;
        this.privateLevel = level;
        this.xPos = startingXPos = x;
        this.yPos = startingYPos = y;

        this.sliderX = xPos + 1;
    }

    /**
     * Draws the gui item
     */
    public void draw (FontRenderer fontRendererObj) {

        if (!show) {
            return;
        }

        final int indexX = dragging ? sliderX : enchantmentLevel <= enchantment.getMaxLevel() ? (int) (xPos + 1 + (width - 6) * (enchantmentLevel / (double) enchantment.getMaxLevel())) : xPos + 1 + width - 6;

        drawRect(indexX, yPos + 1, indexX + 5, yPos - 1 + height, 0xff000000);
        fontRendererObj.drawString(getTranslatedName(), xPos + 5, yPos + height / 4, 0x55aaff00);
        if (disabled) {
            drawRect(xPos, yPos + 1, xPos + width, yPos - 1 + height, 0x44aaffff);
        }
        else if (locked) {
            drawRect(xPos, yPos + 1, xPos + width, yPos - 1 + height, 0x44ff0000);
        }
        else {
            drawRect(xPos, yPos + 1, xPos + width, yPos - 1 + height, 0x44aa55ff);
        }
    }

    public String getTranslatedName () {

        String name = enchantment.getTranslatedName(enchantmentLevel);

        if (enchantmentLevel == 0) {
            if (name.lastIndexOf(" ") == -1) {
                name = enchantment.getName();
            }
            else {
                name = name.substring(0, name.lastIndexOf(" "));
            }
        }

        return name;
    }


    /**
     * Scrolls the item
     *
     * @param xPos the xPost of the mouse to scroll to
     */
    public void scroll (int xPos, int start) {

        if (disabled) {
            return;
        }
        sliderX = start + xPos;

        if (sliderX <= start) {
            sliderX = start;
        }

        if (sliderX >= start + width + 20) {
            sliderX = start + width + 20;
        }

        float index = xPos / (float) (width + 10);
        final int tempLevel = (int) Math.floor(privateLevel > enchantment.getMaxLevel() ? privateLevel * index : enchantment.getMaxLevel() * index);
        if (locked) {
            if (tempLevel < enchantmentLevel && (!container.tableInventory.getStackInSlot(0).isItemDamaged() || EPlusConfigurationHandler.allowEnchantDamaged)) {
                enchantmentLevel = tempLevel;
                locked = false;
            }
        }
        else {
            if (tempLevel >= privateLevel || EPlusConfigurationHandler.allowDisenchanting && (!container.tableInventory.getStackInSlot(0).isItemDamaged()) || EPlusConfigurationHandler.allowEnchantDamaged) {
                enchantmentLevel = tempLevel;
            }
        }

        if (enchantmentLevel <= 0) {
            enchantmentLevel = 0;
        }
    }

    /**
     * Changes the show property
     *
     * @param b true to show | false to hide
     */
    public void show (boolean b) {

        show = b;
    }
}