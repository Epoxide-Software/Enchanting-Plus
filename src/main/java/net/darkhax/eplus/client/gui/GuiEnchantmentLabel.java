package net.darkhax.eplus.client.gui;

import net.darkhax.eplus.handler.ConfigurationHandler;
import net.darkhax.eplus.inventory.ContainerAdvancedTable;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.enchantment.Enchantment;

public class GuiEnchantmentLabel extends Gui {
    
    private final ContainerAdvancedTable container;
    public final Enchantment enchantment;
    public int enchantmentLevel;
    public final int currentLevel;
    
    public int startingXPos;
    public int startingYPos;
    public int xPos;
    public int yPos;
    public final int height = 18;
    private final int width = 143;
    
    private int sliderX;
    public boolean dragging = false;
    
    public boolean show = true;
    public boolean locked = false;
    
    public GuiEnchantmentLabel(ContainerAdvancedTable container, Enchantment enchant, int level, int x, int y) {
        
        this.container = container;
        this.enchantment = enchant;
        this.enchantmentLevel = level;
        this.currentLevel = level;
        this.xPos = this.startingXPos = x;
        this.yPos = this.startingYPos = y;
        this.sliderX = this.xPos + 1;
    }
    
    public void draw (FontRenderer fontRendererObj) {
        
        if (!this.show)
            return;
            
        final int indexX = this.dragging ? this.sliderX : this.enchantmentLevel <= this.enchantment.getMaxLevel() ? (int) (this.xPos + 1 + (this.width - 6) * (this.enchantmentLevel / (double) this.enchantment.getMaxLevel())) : this.xPos + 1 + this.width - 6;
        
        drawRect(indexX, this.yPos + 1, indexX + 5, this.yPos - 1 + this.height, 0xff000000);
        fontRendererObj.drawString(this.getTranslatedName(), this.xPos + 5, this.yPos + this.height / 4, 0x55aaff00);
        
        if (this.locked)
            drawRect(this.xPos, this.yPos + 1, this.xPos + this.width, this.yPos - 1 + this.height, 0x44aaffff);
            
        else
            drawRect(this.xPos, this.yPos + 1, this.xPos + this.width, this.yPos - 1 + this.height, 0x44aa55ff);
    }
    
    public String getTranslatedName () {
        
        String name = this.enchantment.getTranslatedName(this.enchantmentLevel);
        
        if (this.enchantmentLevel == 0)
            if (name.lastIndexOf(" ") == -1)
                name = this.enchantment.getName();
            else
                name = name.substring(0, name.lastIndexOf(" "));
                
        return name;
    }
    
    /**
     * Scrolls the item
     *
     * @param xPos the xPost of the mouse to scroll to
     */
    public void scroll (int xPos, int start) {
        
        if (this.locked)
            return;
        this.sliderX = start + xPos;
        
        if (this.sliderX <= start)
            this.sliderX = start;
            
        if (this.sliderX >= start + this.width + 20)
            this.sliderX = start + this.width + 20;
            
        final float index = xPos / (float) (this.width + 10);
        final int tempLevel = (int) Math.floor(this.currentLevel > this.enchantment.getMaxLevel() ? this.currentLevel * index : this.enchantment.getMaxLevel() * index);
        
        if (tempLevel >= this.currentLevel || ConfigurationHandler.allowDisenchanting && !this.container.tableInventory.getStackInSlot(0).isItemDamaged() || ConfigurationHandler.allowDamagedEnchanting)
            this.enchantmentLevel = tempLevel;
            
        if (this.enchantmentLevel <= 0)
            this.enchantmentLevel = 0;
    }
    
    /**
     * Changes the show property
     *
     * @param b true to show | false to hide
     */
    public void show (boolean b) {
        
        this.show = b;
    }
}