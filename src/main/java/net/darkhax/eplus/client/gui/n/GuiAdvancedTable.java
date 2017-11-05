package net.darkhax.eplus.client.gui.n;

import java.io.IOException;
import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import org.lwjgl.input.Mouse;

import net.darkhax.bookshelf.client.gui.GuiItemButton;
import net.darkhax.bookshelf.lib.EnchantData;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.darkhax.eplus.client.gui.GuiEnchantmentLabel;
import net.darkhax.eplus.inventory.n.ContainerAdvancedTable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiAdvancedTable extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("eplus", "textures/gui/enchant.png");

    private final TileEntityAdvancedTable table;

    private final List<GuiEnchantmentLabel> enchantmentListAll = new ArrayList<>();
    private final List<GuiEnchantmentLabel> enchantmentList = new ArrayList<>();
    
    public GuiEnchantmentLabel selected;
    public boolean wasSelecting;
    public int listOffset = 0;
    public int sliderY = 0;
    public boolean isSliding;

    public GuiAdvancedTable (InventoryPlayer invPlayer, TileEntityAdvancedTable table) {

        super(new ContainerAdvancedTable(invPlayer, table));
        this.table = table;
    }

    @Override
    public void initGui () {

        this.xSize = 235;
        this.ySize = 182;
        this.sliderY = 1;
        this.isSliding = false;
        super.initGui();
        this.buttonList.add(new GuiItemButton(0, this.guiLeft + 35, this.guiTop + 38, new ItemStack(Items.ENCHANTED_BOOK)));
        
        refreshLabels();
        updateLabels();
    }

    @Override
    public void updateScreen () {

        super.updateScreen();
        if (this.selected == null && this.wasSelecting) {
            this.wasSelecting = false;

        }
    }

    @Override
    public void drawScreen (int mouseX, int mouseY, float partialTicks) {

        super.drawScreen(mouseX, mouseY, partialTicks);
        for (final GuiEnchantmentLabel label : getVisibleLabels()) {
            label.draw(this.fontRenderer);
        }
        GlStateManager.color(1, 1, 1, 1);
        this.mc.renderEngine.bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft + 206, this.guiTop + 16 + this.sliderY, 0, 182, 12, 15);

        this.renderHoveredToolTip(mouseX, mouseY);

    }
    
    public void refreshLabels(){
        this.enchantmentListAll.clear();
        if (!this.table.validEnchantments.isEmpty()) {
            int count = 0;
            for (final Enchantment enchantment : this.table.validEnchantments) {
                GuiEnchantmentLabel label = new GuiEnchantmentLabel(this, this.table, enchantment, this.table.getCurrentLevelForEnchant(enchantment), 35 + 26 + this.guiLeft, 15 + this.guiTop + count++ * 18);
                for (final EnchantmentData data : this.table.existingEnchantments) {
                    if (data.enchantment == label.enchantment) {
                        label.currentLevel = data.enchantmentLevel;
                    }
                }
                this.enchantmentListAll.add(label);
            }
        }
    }
    

    public void updateLabels () {
        this.enchantmentList.clear();
        if(!this.enchantmentListAll.isEmpty()){
            int count = 0;
            for(int i = 0; i < enchantmentListAll.size(); i++) {
                if (i < this.listOffset) {
                    continue;
                }
                GuiEnchantmentLabel label = enchantmentListAll.get(i);
                label.yPos = 15 + this.guiTop + count++ * 18;
                label.visible = label.yPos >= this.guiTop + 15 && label.yPos < this.guiTop + 87;
                this.enchantmentList.add(label);
            }
        }
        this.lockLabels();
    }

    public void lockLabels () {
        for (final GuiEnchantmentLabel label : this.enchantmentListAll) {
        
            label.locked = false;
        
            final Enchantment enchantment = label.enchantment;
        
            for (final EnchantmentData data : this.table.existingEnchantments) {
            
                if (enchantment != data.enchantment && !data.enchantment.isCompatibleWith(enchantment)) {
                
                    label.locked = true;
                }
            }
        }
    }
    
    public List<GuiEnchantmentLabel> getVisibleLabels() {
        return new ArrayList<>(enchantmentList);
    }
    
    //    public List<GuiEnchantmentLabel> getVisibleLabels () {
//
//        final List<GuiEnchantmentLabel> visible = new ArrayList<>();
//
//        for (int i = 0; i < this.enchantmentListAll.size(); i++) {
//            final GuiEnchantmentLabel label = this.enchantmentListAll.get(i);
//            label.visible = label.yPos >= this.guiTop + 15 && label.yPos < this.guiTop + 87;
//            if (label.visible) {
//                visible.add(label);
//            }
//        }
//        return visible;
//    }

    public GuiEnchantmentLabel getLabelUnderMouse (int mx, int my) {

        for (final GuiEnchantmentLabel label : this.enchantmentList) {
            if (label.xPos <= mx && label.xPos + label.width >= mx) {
                if (label.yPos <= my && label.yPos + label.height >= my) {
                    return label;
                }
            }
        }
        return null;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer (float partialTicks, int mouseX, int mouseY) {

        this.mc.renderEngine.bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void handleMouseInput () throws IOException {

        super.handleMouseInput();
        final int eventDWheel = Mouse.getEventDWheel();
        final int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth - this.guiLeft;
        final int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1 - this.guiTop;
        final int prevOff = this.listOffset;
        if (eventDWheel > 0) {
            this.listOffset -= 1;
        }
        else if (eventDWheel < 0) {
            this.listOffset += 1;
        }

        if (this.selected != null) {
            this.selected.updateSlider(mouseX - 62, this.guiLeft + 62);
            this.lockLabels();
        }
        if (this.isSliding) {
            this.sliderY = mouseY - this.guiTop + 7;
            this.sliderY = Math.max(1, this.sliderY);
            this.sliderY = Math.min(56, this.sliderY);
            // TODO fix this code
            this.listOffset = this.sliderY / 4;
        }
        this.listOffset = Math.max(this.listOffset, 0);
        this.listOffset = Math.min(this.listOffset, this.enchantmentListAll.size() );

        if (this.listOffset != prevOff) {
            this.updateLabels();
        }
    }

    public void moveSlider (int mx, int my) {

        // System.out.println(mx + ":" + (guiLeft + 206) +":" + (guiLeft + 218));
        // System.out.println(my + ":" + (guiTop + 16 + sliderY) + ":" + (guiTop + 31 +
        // sliderY));
        //
        // System.out.println();
        // if(mx > guiLeft + 206 && mx < guiLeft + 218) {
        // if(my > guiTop + 16 + sliderY && my < guiTop + 31 + sliderY) {
        // if(Mouse.getEventButton() == 0)
        // sliderY = my-guiTop;
        // }
        // }
    }

    @Override
    protected void mouseClicked (int mouseX, int mouseY, int mouseButton) throws IOException {

        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.selected = this.getLabelUnderMouse(mouseX, mouseY);
        if (this.selected != null && !this.selected.locked) {
            this.selected.dragging = true;
            this.wasSelecting = true;
        }
        else {
            this.selected = null;
        }

        if (mouseX > this.guiLeft + 206 && mouseX < this.guiLeft + 218) {
            if (mouseY > this.guiTop + 16 + this.sliderY && mouseY < this.guiTop + 31 + this.sliderY) {
                this.isSliding = true;
            }
        }
    }

    @Override
    protected void mouseReleased (int mouseX, int mouseY, int state) {

        super.mouseReleased(mouseX, mouseY, state);
        if (this.selected != null) {
            this.selected.dragging = false;
            this.selected = null;
            this.wasSelecting = true;
            this.table.existingEnchantments.clear();
            for (final GuiEnchantmentLabel label : this.enchantmentListAll) {
                if (label.currentLevel != label.initialLevel) {
                    this.table.existingEnchantments.add(new EnchantData(label.enchantment, label.currentLevel));
                }
            }

            this.lockLabels();

        }
        if (this.isSliding) {
            this.isSliding = false;
        }
    }

    @Override
    protected void mouseClickMove (int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {

        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (this.selected != null) {
            if (mouseX < this.selected.xPos || mouseX > this.selected.xPos + this.selected.width) {
                this.selected.dragging = false;
                this.selected = null;
                return;
            }
            this.table.existingEnchantments.clear();
            // Something with this code forced hidden enchants to reset to their initial value
            for (final GuiEnchantmentLabel label : this.enchantmentListAll) {
                if (label.currentLevel != label.initialLevel) {
                    this.table.existingEnchantments.add(new EnchantData(label.enchantment, label.currentLevel));
                }
            }

            this.lockLabels();
        }
        this.moveSlider(mouseX, mouseY);
    }

    @Override
    protected void actionPerformed (GuiButton button) throws IOException {

        super.actionPerformed(button);
        if (button.id == 0) {
            for (final GuiEnchantmentLabel label : this.enchantmentListAll) {
                if (label.currentLevel != label.initialLevel) {
                    this.table.inventory.getStackInSlot(0).addEnchantment(label.enchantment, label.currentLevel);
                    // TODO send a packet to the server
                    // TODO calculate costs
                }
            }
        }
    }
}
