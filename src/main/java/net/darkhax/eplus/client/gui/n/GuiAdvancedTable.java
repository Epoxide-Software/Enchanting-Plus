package net.darkhax.eplus.client.gui.n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private final List<GuiEnchantmentLabel> enchantmentList = new ArrayList<>();
    private GuiEnchantmentLabel selected;
    private boolean wasSelecting;
    private int listOffset = 0;
    private int sliderY = 0;
    private boolean isSliding;

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
        this.enchantmentList.clear();
        if (!this.table.enchantmentsValid.isEmpty()) {
            int count = 0;
            for (final Enchantment enchantment : this.table.enchantmentsValid) {
                this.enchantmentList.add(new GuiEnchantmentLabel(this.table, enchantment, this.table.getCurrentLevelForEnchant(enchantment), 35 + 26 + this.guiLeft, 15 + this.guiTop + count++ * 18));
            }
        }
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
        for (final GuiEnchantmentLabel label : this.getVisibleLabels()) {
            label.draw(this.fontRenderer);
        }
        GlStateManager.color(1, 1, 1, 1);
        this.mc.renderEngine.bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft + 206, this.guiTop + 16 + this.sliderY, 0, 182, 12, 15);

        this.renderHoveredToolTip(mouseX, mouseY);

    }

    public void updateLabels () {

        this.enchantmentList.clear();
        if (!this.table.enchantmentsValid.isEmpty()) {
            int count = 0;
            for (int i = 0; i < this.table.enchantmentsValid.size(); i++) {
                if (i < this.listOffset) {
                    continue;
                }
                final Enchantment enchantment = this.table.enchantmentsValid.get(i);
                final GuiEnchantmentLabel label = new GuiEnchantmentLabel(this.table, enchantment, this.table.getCurrentLevelForEnchant(enchantment), 35 + 26 + this.guiLeft, 15 + this.guiTop + count++ * 18);
                for (final EnchantmentData data : this.table.enchantmentsNew) {
                    if (data.enchantment == label.enchantment) {
                        label.currentLevel = data.enchantmentLevel;
                    }
                }
                this.enchantmentList.add(label);
            }
            this.lockLabels();
        }
    }

    public void lockLabels () {

        // messes with existing enchants
        for (final GuiEnchantmentLabel label : this.enchantmentList) {
            label.locked = false;
            final Enchantment enchantment = label.enchantment;
            for (final EnchantmentData data : this.table.enchantmentsCurrent) {
                if (data.enchantment != enchantment && !data.enchantment.isCompatibleWith(enchantment)) {
                    label.locked = true;
                }
            }
            for (final EnchantmentData data : this.table.enchantmentsNew) {
                if (data.enchantment != enchantment && !data.enchantment.isCompatibleWith(enchantment)) {
                    label.locked = true;
                }
            }
        }
    }

    public List<GuiEnchantmentLabel> getVisibleLabels () {

        final List<GuiEnchantmentLabel> visible = new ArrayList<>();

        for (int i = 0; i < this.enchantmentList.size(); i++) {
            final GuiEnchantmentLabel label = this.enchantmentList.get(i);
            label.visible = label.yPos >= this.guiTop + 15 && label.yPos < this.guiTop + 87;
            if (label.visible) {
                visible.add(label);
            }
        }
        return visible;
    }

    public GuiEnchantmentLabel getLabelUnderMouse (int mx, int my) {

        for (final GuiEnchantmentLabel label : this.getVisibleLabels()) {
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
        this.listOffset = Math.min(this.listOffset, this.enchantmentList.size() + 1);

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
        if (this.selected != null) {
            this.selected.dragging = true;
            this.wasSelecting = true;
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
            this.table.enchantmentsNew.clear();
            for (final GuiEnchantmentLabel label : this.enchantmentList) {
                if (label.currentLevel != label.initialLevel) {
                    this.table.enchantmentsNew.add(new EnchantData(label.enchantment, label.currentLevel));
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
            this.table.enchantmentsNew.clear();
            // Something with this code forced hidden enchants to reset to their initial value
            for (final GuiEnchantmentLabel label : this.enchantmentList) {
                if (label.currentLevel != label.initialLevel) {
                    this.table.enchantmentsNew.add(new EnchantData(label.enchantment, label.currentLevel));
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
            for (final GuiEnchantmentLabel label : this.enchantmentList) {
                if (label.currentLevel != label.initialLevel) {
                    this.table.inventory.getStackInSlot(0).addEnchantment(label.enchantment, label.currentLevel);
                    // TODO send a packet to the server
                    // TODO calculate costs
                }
            }
        }
    }
}
