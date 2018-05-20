package net.darkhax.eplus.gui;

import net.darkhax.bookshelf.client.gui.GuiItemButton;
import net.darkhax.bookshelf.lib.EnchantData;
import net.darkhax.eplus.EnchLogic;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.darkhax.eplus.inventory.ContainerAdvancedTable;
import net.darkhax.eplus.network.messages.*;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.*;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.*;

public class GuiAdvancedTable extends GuiContainer {
    
    private static final ResourceLocation TEXTURE = new ResourceLocation("eplus", "textures/gui/enchant.png");
    
    private final TileEntityAdvancedTable table;
    
    public final List<GuiEnchantmentLabel> enchantmentListAll = new ArrayList<>();
    public final List<GuiEnchantmentLabel> enchantmentList = new ArrayList<>();
    
    public GuiEnchantmentLabel selected;
    public boolean wasSelecting;
    public int listOffset = 0;
    
    public boolean isSliding;
    
    public GuiButtonScroller scrollbar;
    
    public GuiAdvancedTable(InventoryPlayer invPlayer, TileEntityAdvancedTable table) {
        
        super(new ContainerAdvancedTable(invPlayer, table));
        this.table = table;
    }
    
    @Override
    public void initGui() {
        
        this.xSize = 235;
        this.ySize = 182;
        this.isSliding = false;
        super.initGui();
        this.scrollbar = new GuiButtonScroller(this, 1, this.guiLeft + 206, this.guiTop + 16, 12, 70);
        this.buttonList.add(new GuiItemButton(0, this.guiLeft + 35, this.guiTop + 38, new ItemStack(Items.ENCHANTED_BOOK)));
        this.buttonList.add(this.scrollbar);
        
        this.refreshLabels();
        this.updateLabels();
    }
    
    @Override
    public void updateScreen() {
        
        super.updateScreen();
        if(this.selected == null && this.wasSelecting) {
            this.wasSelecting = false;
        }
        if(this.table.updateGui) {
            this.table.updateGui = false;
            this.scrollbar.sliderY = 1;
            this.isSliding = false;
            this.listOffset = 0;
            this.refreshLabels();
            this.updateLabels();
            
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    
        List<EnchantData> list = new ArrayList<>();
        
        for(EnchantData data : table.existingEnchantments) {
            for(EnchantData ench: table.existingEnchantmentsCache) {
                if(data.enchantment == ench.enchantment && data.enchantmentLevel == ench.enchantmentLevel){
                
                }else{
                    list.add(data);
                }
            }
        }
        int levels = 0;
        for(EnchantData data : list) {
            levels += EnchLogic.calculateNewEnchCost(data.enchantment, data.enchantmentLevel);
        }
        fontRenderer.drawString(Integer.toString(levels), guiLeft+30,guiTop+80,0);
    
        this.renderHoveredToolTip(mouseX, mouseY);
        
    }
    
    public void refreshLabels() {
        
        this.enchantmentListAll.clear();
        if(!this.table.validEnchantments.isEmpty()) {
            int count = 0;
            for(final Enchantment enchantment : this.table.validEnchantments) {
                final GuiEnchantmentLabel label = new GuiEnchantmentLabel(this, this.table, enchantment, this.table.getCurrentLevelForEnchant(enchantment), 35 + 26 + this.guiLeft, 15 + this.guiTop + count++ * 18);
                for(final EnchantData data : this.table.existingEnchantments) {
                    if(data.enchantment == label.getEnchantment()) {
                        label.setCurrentLevel(data.enchantmentLevel);
                    }
                }
                this.enchantmentListAll.add(label);
            }
        }
    }
    
    public void updateLabels() {
        
        this.enchantmentList.clear();
        if(!this.enchantmentListAll.isEmpty()) {
            int count = 0;
            for(int i = 0; i < this.enchantmentListAll.size(); i++) {
                if(i < this.listOffset) {
                    continue;
                }
                final GuiEnchantmentLabel label = this.enchantmentListAll.get(i);
                label.setyPos(15 + this.guiTop + count++ * 18);
                label.setVisible(label.getyPos() >= this.guiTop + 15 && label.getyPos() < this.guiTop + 87);
                this.enchantmentList.add(label);
            }
        }
        this.lockLabels();
    }
    
    public void lockLabels() {
        
        for(final GuiEnchantmentLabel label : this.enchantmentListAll) {
            
            label.setLocked(false);
            
            final Enchantment enchantment = label.getEnchantment();
            
            for(final EnchantData data : this.table.existingEnchantments) {
                
                final boolean isIncompatable = enchantment != data.enchantment && data.enchantmentLevel > 0 && !data.enchantment.isCompatibleWith(enchantment);
                final boolean isOverLeveled = enchantment == data.enchantment && data.enchantmentLevel > enchantment.getMaxLevel();
                
                if(isOverLeveled || isIncompatable) {
                    
                    label.setLocked(true);
                    break;
                }
            }
        }
    }
    
    public GuiEnchantmentLabel getLabelUnderMouse(int mx, int my) {
        
        for(final GuiEnchantmentLabel label : this.enchantmentList) {

            if (label.isMouseOver(mx, my)) {
                
                return label;
            }
        }
        
        return null;
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        
        this.mc.renderEngine.bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        GlStateManager.color(1, 1, 1, 1);
        
        for(final GuiEnchantmentLabel label : this.enchantmentList) {
            label.draw(this.fontRenderer);
        }
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        
        super.handleMouseInput();
        final int eventDWheel = Mouse.getEventDWheel();
        final int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth - this.guiLeft;
        final int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1 - this.guiTop;
        final int prevOff = this.listOffset;
        if(eventDWheel > 0) {
            this.listOffset -= 1;
        } else if(eventDWheel < 0) {
            this.listOffset += 1;
        }
        
        if(this.selected != null) {
            this.selected.updateSlider(mouseX - 62);
        }
        this.listOffset = Math.max(this.listOffset, 0);
        this.listOffset = Math.min(this.listOffset, this.enchantmentListAll.size() - 4);
        if(this.enchantmentList.size() < 4) {
            this.listOffset = 0;
        }
        if(this.listOffset != prevOff) {
            this.updateLabels();
            this.scrollbar.sliderY = 70 / (this.enchantmentListAll.size() - 4) * this.listOffset - 7;
            this.scrollbar.sliderY = Math.max(1, this.scrollbar.sliderY);
            this.scrollbar.sliderY = Math.min(56, this.scrollbar.sliderY);
        }
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.selected = this.getLabelUnderMouse(mouseX, mouseY);
        if(this.selected != null && !this.selected.isLocked()) {
            this.selected.setDragging(true);
            this.wasSelecting = true;
        } else {
            this.selected = null;
        }
        if(mouseX > this.guiLeft + 206 && mouseX < this.guiLeft + 218) {
            if(mouseY > this.guiTop + 16 + this.scrollbar.sliderY && mouseY < this.guiTop + 31 + this.scrollbar.sliderY) {
                this.isSliding = true;
            }
        }
    }
    
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        
        super.mouseReleased(mouseX, mouseY, state);
        if(this.selected != null) {
            this.selected.setDragging(false);
            this.selected = null;
            this.wasSelecting = true;
            
            this.table.existingEnchantments.clear();
            for(final GuiEnchantmentLabel label : this.enchantmentListAll) {
                if(label.getCurrentLevel() > 0) {
                    this.table.existingEnchantments.add(new EnchantData(label.getEnchantment(), label.getCurrentLevel()));
                }
            }
            this.lockLabels();
            
        }
        if(this.isSliding) {
            this.isSliding = false;
        }
    }
    
    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if(this.selected != null) {
            if(mouseX < this.selected.getxPos() || mouseX > this.selected.getxPos() + this.selected.getWidth()) {
                this.selected.setDragging(false);
                this.selected = null;
                return;
            }
            this.table.existingEnchantments.clear();
            for(final GuiEnchantmentLabel label : this.enchantmentListAll) {
                if(label.getCurrentLevel() != label.getInitialLevel()) {
                    this.table.existingEnchantments.add(new EnchantData(label.getEnchantment(), label.getCurrentLevel()));
                }
            }
            
            this.lockLabels();
        }
        
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        
        super.actionPerformed(button);
        
        if(button.id == 0) {
            
            EnchantingPlus.NETWORK.sendToServer(new MessageEnchant(((ContainerAdvancedTable) this.inventorySlots).pos));
        }
    }
    
    public TileEntityAdvancedTable getTable() {
        
        return table;
    }
}