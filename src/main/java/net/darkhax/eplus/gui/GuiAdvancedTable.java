package net.darkhax.eplus.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.input.Mouse;

import net.darkhax.bookshelf.client.gui.GuiItemButton;
import net.darkhax.bookshelf.lib.Constants;
import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.api.event.InfoBoxEvent;
import net.darkhax.eplus.block.tileentity.TileEntityAdvancedTable;
import net.darkhax.eplus.inventory.ContainerAdvancedTable;
import net.darkhax.eplus.network.messages.MessageEnchant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiAdvancedTable extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("eplus", "textures/gui/enchant.png");
    private static final KeyBinding keyBindSneak = Minecraft.getMinecraft().gameSettings.keyBindSneak;

    private final TileEntityAdvancedTable table;

    private GuiButton enchantButton;

    public final List<GuiEnchantmentLabel> enchantmentListAll = new ArrayList<>();
    public final List<GuiEnchantmentLabel> enchantmentList = new ArrayList<>();

    public GuiEnchantmentLabel selected;
    public int listOffset = 0;

    public boolean isSliding;

    public GuiButtonScroller scrollbar;

    String[] tips = { "notip", "description", "books", "treasure", "curse" };
    private int currentTip = 0;

    public GuiAdvancedTable (InventoryPlayer invPlayer, TileEntityAdvancedTable table) {

        super(new ContainerAdvancedTable(invPlayer, table));
        this.table = table;
        this.xSize = 235;
        this.ySize = 182;

        this.currentTip = Constants.RANDOM.nextInt(this.tips.length);
    }

    @Override
    public void initGui () {

        super.initGui();

        this.isSliding = false;
        this.scrollbar = new GuiButtonScroller(this, 1, this.guiLeft + 206, this.guiTop + 16, 12, 15);
        this.enchantButton = new GuiItemButton(0, this.guiLeft + 35, this.guiTop + 38, new ItemStack(Items.ENCHANTED_BOOK));
        this.buttonList.add(this.enchantButton);
        this.buttonList.add(this.scrollbar);
    }

    @Override
    public void updateScreen () {

        super.updateScreen();
        this.updateLabels();
        this.populateEnchantmentSliders();
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int mouseX, int mouseY) {

        this.fontRenderer.drawString(I18n.format("tile.eplus.advanced.table.name"), 32, 5, 4210752);
    }

    @Override
    public void drawScreen (int mouseX, int mouseY, float partialTicks) {

        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    public void populateEnchantmentSliders () {

        this.enchantmentListAll.clear();

        int labelCount = 0;

        for (final Enchantment enchant : this.table.getLogic().getValidEnchantments()) {

            final GuiEnchantmentLabel label = new GuiEnchantmentLabel(this, this.table, enchant, this.table.getLogic().getCurrentLevel(enchant), 35 + 26 + this.guiLeft, 15 + this.guiTop + labelCount++ * 18);
            label.setCurrentLevel(this.table.getLogic().getCurrentLevel(enchant));

            this.enchantmentListAll.add(label);
        }
    }

    public void updateLabels () {

        this.enchantmentList.clear();

        int count = 0;

        for (int i = 0; i < this.enchantmentListAll.size(); i++) {
            if (i < this.listOffset) {
                continue;
            }
            final GuiEnchantmentLabel label = this.enchantmentListAll.get(i);
            label.setyPos(15 + this.guiTop + count++ * 18);
            label.setVisible(label.getyPos() >= this.guiTop + 15 && label.getyPos() < this.guiTop + 87);
            this.enchantmentList.add(label);
        }

        this.lockLabels();
    }

    public void lockLabels () {

        for (final GuiEnchantmentLabel label : this.enchantmentListAll) {

            label.setLocked(false);

            final Enchantment enchantment = label.getEnchantment();

            for (final Entry<Enchantment, Integer> data : this.table.getLogic().getCurrentEnchantments().entrySet()) {

                final boolean isIncompatable = enchantment != data.getKey() && data.getValue() > 0 && !data.getKey().isCompatibleWith(enchantment);
                final boolean isOverLeveled = enchantment == data.getKey() && data.getValue() > enchantment.getMaxLevel();
                if (isOverLeveled || isIncompatable) {

                    label.setLocked(true);
                    break;
                }
            }
        }
    }

    public GuiEnchantmentLabel getLabelUnderMouse (int mx, int my) {

        for (final GuiEnchantmentLabel label : this.enchantmentList) {

            if (label.isMouseOver(mx, my)) {

                return label;
            }
        }

        return null;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer (float partialTicks, int mouseX, int mouseY) {

        GlStateManager.color(1, 1, 1, 1);
        this.mc.renderEngine.bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        for (final GuiEnchantmentLabel label : this.enchantmentList) {
            label.draw(this.fontRenderer);
        }
    }

    @Override
    public void handleMouseInput () throws IOException {

        super.handleMouseInput();
        final int eventDWheel = Mouse.getEventDWheel();
        final int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth - this.guiLeft;
        final int prevOff = this.listOffset;
        if (eventDWheel > 0) {
            this.listOffset -= 1;
        }
        else if (eventDWheel < 0) {
            this.listOffset += 1;
        }

        if (this.selected != null) {
            this.selected.updateSlider(mouseX - 62);
        }
        this.listOffset = Math.max(this.listOffset, 0);
        this.listOffset = Math.min(this.listOffset, this.enchantmentListAll.size() - 4);
        if (this.enchantmentList.size() < 4) {
            this.listOffset = 0;
        }
        if (this.listOffset != prevOff) {
            this.updateLabels();
            this.scrollbar.sliderY = 70 / (this.enchantmentListAll.size() - 4) * this.listOffset - 7;
            this.scrollbar.sliderY = Math.max(1, this.scrollbar.sliderY);
            this.scrollbar.sliderY = Math.min(56, this.scrollbar.sliderY);
        }
    }

    @Override
    protected void mouseClicked (int mouseX, int mouseY, int mouseButton) throws IOException {

        super.mouseClicked(mouseX, mouseY, mouseButton);

        this.selected = this.getLabelUnderMouse(mouseX, mouseY);

        if (this.selected != null && !this.selected.isLocked() && this.selected.isVisible()) {

            this.selected.setDragging(true);
        }

        else {

            this.selected = null;
        }

        if (this.enchantmentListAll.size() > 4 && mouseX > this.guiLeft + 206 && mouseX < this.guiLeft + 218) {
            if (mouseY > this.guiTop + 16 + this.scrollbar.sliderY && mouseY < this.guiTop + 31 + this.scrollbar.sliderY) {
                this.isSliding = true;
            }
        }
    }

    @Override
    protected void mouseReleased (int mouseX, int mouseY, int state) {

        super.mouseReleased(mouseX, mouseY, state);
        if (this.selected != null) {
            this.selected.setDragging(false);
            this.selected = null;

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

            if (mouseX < this.selected.getxPos() || mouseX > this.selected.getxPos() + this.selected.getWidth()) {

                this.selected.setDragging(false);
                this.selected = null;
                return;
            }

            this.lockLabels();
        }
    }

    @Override
    protected void actionPerformed (GuiButton button) throws IOException {

        super.actionPerformed(button);

        if (button.id == 0 && this.canClientAfford()) {

            EnchantingPlus.NETWORK.sendToServer(new MessageEnchant(((ContainerAdvancedTable) this.inventorySlots).pos));
            this.getTable().getLogic().enchantItem(PlayerUtils.getClientPlayerSP());
        }
    }

    @Override
    protected void renderHoveredToolTip (int x, int y) {

        super.renderHoveredToolTip(x, y);

        // Info Box
        GuiUtils.drawHoveringText(this.getInfoBox(), -3, this.guiTop + 27, this.xSize, this.ySize, this.guiLeft - 18, this.fontRenderer);

        // Enchant button tooltip
        if (this.enchantButton.isMouseOver()) {

            final List<String> text = new ArrayList<>();

            if (!this.canClientAfford()) {

                text.add(I18n.format("gui.eplus.tooltip.tooexpensive"));
            }

            else if (this.getTable().getLogic().getCost() == 0) {

                text.add(I18n.format("gui.eplus.tooltip.nochange"));
            }

            else {

                text.add(I18n.format("gui.eplus.tooltip.enchant"));
            }

            GuiUtils.drawHoveringText(text, x, y, this.width, this.height, this.width / 4, this.fontRenderer);
        }

        // Descriptions
        if (GameSettings.isKeyDown(keyBindSneak)) {

            final GuiEnchantmentLabel label = this.getLabelUnderMouse(x, y);

            if (label != null && label.isVisible()) {

                GuiUtils.drawHoveringText(Collections.singletonList(label.getDescription()), x, y, this.width, this.height, this.width / 3, this.fontRenderer);
            }
        }
    }

    private List<String> getInfoBox () {

        final List<String> info = new ArrayList<>();

        if (this.getTable().getItem().isEmpty()) {

            info.add(I18n.format("gui.eplus.info.noitem"));
        }

        else if (this.enchantmentListAll.isEmpty()) {

            info.add(I18n.format("gui.eplus.info.neench"));
        }

        else {

            final boolean isCreative = PlayerUtils.getClientPlayerSP().isCreative();
            final int playerXP = isCreative ? Integer.MAX_VALUE : PlayerUtils.getClientPlayerSP().experienceTotal;
            final int cost = this.getTable().getLogic().getCost();

            info.add(isCreative ? I18n.format("eplus.info.infinity") : I18n.format("eplus.info.playerxp", playerXP));
            info.add(I18n.format("eplus.info.costxp", cost));

            if (cost > playerXP) {

                info.add(" ");
                info.add(TextFormatting.RED + I18n.format("gui.eplus.info.tooexpensive"));
            }
        }

        if (this.currentTip != 0) {

            info.add(" ");
            info.add(TextFormatting.YELLOW + I18n.format("eplus.info.tip.prefix") + TextFormatting.RESET + I18n.format("eplus.info.tip." + this.tips[this.currentTip], keyBindSneak.getDisplayName()));

            if (PlayerUtils.isPlayersBirthdate(PlayerUtils.getClientPlayerSP())) {

                info.add(" ");
                info.add(TextFormatting.LIGHT_PURPLE + I18n.format("eplus.info.tip.bonus") + TextFormatting.RESET + I18n.format("eplus.info.tip.birthday"));
            }
        }

        MinecraftForge.EVENT_BUS.post(new InfoBoxEvent(this, info));
        return info;
    }

    public boolean canClientAfford () {

        return this.getTable().getLogic().getCost() <= PlayerUtils.getClientPlayerSP().experienceTotal || PlayerUtils.getClientPlayerSP().isCreative();
    }

    public TileEntityAdvancedTable getTable () {

        return this.table;
    }
}