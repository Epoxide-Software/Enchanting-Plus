package net.darkhax.eplus.client.gui.n;

import net.darkhax.bookshelf.util.EnchantmentUtils;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.darkhax.eplus.client.gui.GuiEnchantmentLabel;
import net.darkhax.eplus.inventory.n.ContainerAdvancedTable;
import net.darkhax.eplus.libs.EnchantData;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
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
    
    private List<GuiEnchantmentLabel> enchantmentList = new ArrayList<>();
    private GuiEnchantmentLabel selected;
    private boolean wasSelecting;
    private int listOffset = 0;
    private int sliderY = 0;
    private boolean isSliding;
    
    public GuiAdvancedTable(InventoryPlayer invPlayer, TileEntityAdvancedTable table) {
        super(new ContainerAdvancedTable(invPlayer, table));
        this.table = table;
    }
    
    @Override
    public void initGui() {
        this.xSize = 235;
        this.ySize = 182;
        sliderY = 1;
        isSliding = false;
        super.initGui();
        this.buttonList.add(new GuiItemButton(0, this.guiLeft + 35, this.guiTop + 38, new ItemStack(Items.ENCHANTED_BOOK)));
        enchantmentList.clear();
        if(!table.enchantmentsValid.isEmpty()) {
            int count = 0;
            for(Enchantment enchantment : table.enchantmentsValid) {
                enchantmentList.add(new GuiEnchantmentLabel(table, enchantment, table.getCurrentLevelForEnchant(enchantment), 35 + 26 + this.guiLeft, (15 + this.guiTop + count++ * 18)));
            }
        }
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        if(selected == null && wasSelecting) {
            wasSelecting = false;
            
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        for(GuiEnchantmentLabel label : getVisibleLabels()) {
            label.draw(this.fontRenderer);
        }
        GlStateManager.color(1, 1, 1, 1);
        this.mc.renderEngine.bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft + 206, this.guiTop + 16 + (int) sliderY, 0, 182, 12, 15);
        
        this.renderHoveredToolTip(mouseX, mouseY);
        
    }
    
    public void updateLabels() {
        enchantmentList.clear();
        if(!table.enchantmentsValid.isEmpty()) {
            int count = 0;
            for(int i = 0; i < table.enchantmentsValid.size(); i++) {
                if(i < listOffset) {
                    continue;
                }
                Enchantment enchantment = table.enchantmentsValid.get(i);
                GuiEnchantmentLabel label = new GuiEnchantmentLabel(table, enchantment, table.getCurrentLevelForEnchant(enchantment), 35 + 26 + this.guiLeft, (15 + this.guiTop + count++ * 18));
                for(EnchantmentData data : table.enchantmentsNew) {
                    if(data.enchantment == label.enchantment) {
                        label.currentLevel = data.enchantmentLevel;
                    }
                }
                enchantmentList.add(label);
            }
            lockLabels();
        }
    }
    
    public void lockLabels() {
        //messes with existing enchants
        for(GuiEnchantmentLabel label : enchantmentList) {
            label.locked = false;
            Enchantment enchantment = label.enchantment;
            for(EnchantmentData data : table.enchantmentsCurrent) {
                if(data.enchantment != enchantment && !data.enchantment.isCompatibleWith(enchantment)) {
                    label.locked = true;
                }
            }
            for(EnchantmentData data : table.enchantmentsNew) {
                if(data.enchantment != enchantment && !data.enchantment.isCompatibleWith(enchantment)) {
                    label.locked = true;
                }
            }
        }
    }
    
    public List<GuiEnchantmentLabel> getVisibleLabels() {
        List<GuiEnchantmentLabel> visible = new ArrayList<>();
        
        for(int i = 0; i < enchantmentList.size(); i++) {
            GuiEnchantmentLabel label = enchantmentList.get(i);
            label.visible = label.yPos >= this.guiTop + 15 && label.yPos < this.guiTop + 87;
            if(label.visible) {
                visible.add(label);
            }
        }
        return visible;
    }
    
    public GuiEnchantmentLabel getLabelUnderMouse(int mx, int my) {
        for(GuiEnchantmentLabel label : getVisibleLabels()) {
            if(label.xPos <= mx && label.xPos + label.width >= mx) {
                if(label.yPos <= my && label.yPos + label.height >= my) {
                    return label;
                }
            }
        }
        return null;
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.renderEngine.bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        final int eventDWheel = Mouse.getEventDWheel();
        final int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth - this.guiLeft;
        final int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1 - this.guiTop;
        int prevOff = listOffset;
        if(eventDWheel > 0) {
            listOffset -= 1;
        } else if(eventDWheel < 0) {
            listOffset += 1;
        }
        
        if(selected != null) {
            selected.updateSlider(mouseX - 62, this.guiLeft + 62);
            lockLabels();
        }
        if(isSliding) {
            sliderY = mouseY - guiTop + 7;
            sliderY = Math.max(1, sliderY);
            sliderY = Math.min(56, sliderY);
            //TODO fix this code
            listOffset = sliderY / 4;
        }
        listOffset = Math.max(listOffset, 0);
        listOffset = Math.min(listOffset, enchantmentList.size() + 1);
        
        if(listOffset != prevOff) {
            updateLabels();
        }
    }
    
    public void moveSlider(int mx, int my) {
        //        System.out.println(mx + ":" + (guiLeft + 206) +":" + (guiLeft + 218));
        //        System.out.println(my + ":" + (guiTop + 16 + sliderY) + ":" + (guiTop + 31 + sliderY));
        //
        //        System.out.println();
        //        if(mx > guiLeft + 206 && mx < guiLeft + 218) {
        //            if(my > guiTop + 16 + sliderY && my < guiTop + 31 + sliderY) {
        //                if(Mouse.getEventButton() == 0)
        //                    sliderY = my-guiTop;
        //            }
        //        }
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        selected = getLabelUnderMouse(mouseX, mouseY);
        if(selected != null) {
            selected.dragging = true;
            wasSelecting = true;
        }
        
        if(mouseX > guiLeft + 206 && mouseX < guiLeft + 218) {
            if(mouseY > guiTop + 16 + sliderY && mouseY < guiTop + 31 + sliderY) {
                isSliding = true;
            }
        }
    }
    
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if(selected != null) {
            selected.dragging = false;
            selected = null;
            wasSelecting = true;
            table.enchantmentsNew.clear();
            for(GuiEnchantmentLabel label : enchantmentList) {
                if(label.currentLevel != label.initialLevel) {
                    table.enchantmentsNew.add(new EnchantData(label.enchantment, label.currentLevel));
                }
            }
            
            lockLabels();
            
        }
        if(isSliding) {
            isSliding = false;
        }
    }
    
    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if(selected != null) {
            table.enchantmentsNew.clear();
            //Something with this code forced hidden enchants to reset to their initial value
            for(GuiEnchantmentLabel label : enchantmentList) {
                if(label.currentLevel != label.initialLevel) {
                    table.enchantmentsNew.add(new EnchantData(label.enchantment, label.currentLevel));
                }
            }
            
            lockLabels();
        }
        moveSlider(mouseX, mouseY);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if(button.id == 0) {
            for(GuiEnchantmentLabel label : enchantmentList) {
                if(label.currentLevel != label.initialLevel) {
                    table.inventory.getStackInSlot(0).addEnchantment(label.enchantment, label.currentLevel);
                    //TODO send a packet to the server
                    //TODO calculate costs
                }
            }
        }
    }
}
