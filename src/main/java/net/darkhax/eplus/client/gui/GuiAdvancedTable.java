package net.darkhax.eplus.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.darkhax.bookshelf.client.gui.GuiGraphicButton;
import net.darkhax.bookshelf.lib.util.EnchantmentUtils;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.darkhax.bookshelf.lib.util.ModUtils;
import net.darkhax.eplus.EnchantingPlus;
import net.darkhax.eplus.common.network.packet.PacketEnchantItem;
import net.darkhax.eplus.common.network.packet.PacketRepairItem;
import net.darkhax.eplus.handler.ConfigurationHandler;
import net.darkhax.eplus.inventory.ContainerAdvancedTable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuiAdvancedTable extends GuiContainer {
    
    private static int guiOffset = 26;
    
    private static final ResourceLocation TEXTURE = new ResourceLocation("eplus", "textures/gui/enchant.png");
    private static final ResourceLocation BUTTON_ENCHANT = new ResourceLocation("eplus", "textures/gui/button_enchant.png");
    private static final ResourceLocation BUTTON_REPAIR = new ResourceLocation("eplus", "textures/gui/button_repair.png");
    
    private List<GuiEnchantmentLabel> enchantmentList = new ArrayList<GuiEnchantmentLabel>();
    private final EntityPlayer player;
    
    private final ContainerAdvancedTable container;
    private double sliderIndex = 0;
    private double enchantingPages = 0;
    private Map<Enchantment, Integer> enchantments;
    private boolean clicked = false;
    private boolean sliding = false;
    private int totalCost = 0;
    private boolean dirty = false;
    private GuiEnchantmentLabel last;
    
    public GuiAdvancedTable(InventoryPlayer inventory, World world, BlockPos pos) {
        
        super(new ContainerAdvancedTable(inventory, world, pos));
        
        this.player = inventory.player;
        this.container = (ContainerAdvancedTable) this.inventorySlots;
        this.xSize = 235;
        this.ySize = 182;
        this.zLevel = -1;
    }
    
    @Override
    protected void actionPerformed (GuiButton button) {
        
        final HashMap<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
        
        for (final GuiEnchantmentLabel label : this.enchantmentList)
            if (label.enchantmentLevel != this.enchantments.get(label.enchantment) && !label.locked)
                enchants.put(label.enchantment, label.enchantmentLevel);
                
        switch (button.id) {
            
            case 0:
                if (enchants.size() > 0)
                    EnchantingPlus.network.sendToServer(new PacketEnchantItem(this.totalCost, enchants));
            case 1:
                if (enchants.size() == 0 && this.totalCost > 0 && ConfigurationHandler.allowRepairs)
                    EnchantingPlus.network.sendToServer(new PacketRepairItem(this.totalCost));
        }
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer (float partialTicks, int mouseX, int mouseY) {
        
        final boolean flag = Mouse.isButtonDown(0);
        
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.color(1f, 1f, 1f);
        
        this.mc.renderEngine.bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        for (final GuiEnchantmentLabel label : this.enchantmentList) {
            
            if (label.yPos < this.guiTop + 15 || label.yPos >= this.guiTop + 87)
                label.show(false);
                
            else
                label.show(true);
                
            label.draw(this.fontRendererObj);
        }
        
        final int adjustedMouseX = mouseX - this.guiLeft;
        final int adjustedMouseY = mouseY - this.guiTop;
        this.mc.renderEngine.bindTexture(TEXTURE);
        int tempY = adjustedMouseY - 16;
        
        if (tempY <= 0)
            tempY = 0;
            
        else if (tempY >= 57)
            tempY = 57;
            
        this.sliderIndex = this.sliding ? Math.round(tempY / 57D * this.enchantingPages / .25) * .25 : this.sliderIndex;
        
        if (this.sliderIndex >= this.enchantingPages)
            this.sliderIndex = this.enchantingPages;
            
        final double sliderY = this.sliding ? tempY : 57 * (this.sliderIndex / this.enchantingPages);
        
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        this.drawTexturedModalRect(this.guiLeft + guiOffset + 180, this.guiTop + 16 + (int) sliderY, 0, 182, 12, 15);
        
        if (!this.clicked && flag) {
            
            for (final GuiEnchantmentLabel label : this.enchantmentList)
                if (this.getSelectedLabel(mouseX, mouseY) == label && !label.locked)
                    label.dragging = true;
                    
            if (adjustedMouseX <= 191 + guiOffset && adjustedMouseX >= 180 + guiOffset)
                if (this.enchantingPages != 0)
                    this.sliding = true;
        }
        
        for (final GuiEnchantmentLabel label : this.enchantmentList)
            if (label.dragging && this.getSelectedLabel(mouseX, mouseY) != label) {
                
                label.dragging = false;
                this.last = label;
            }
            
        if (!flag) {
            for (final GuiEnchantmentLabel label : this.enchantmentList)
                if (this.getSelectedLabel(mouseX, mouseY) == label) {
                    
                    label.dragging = false;
                    this.last = label;
                }
                
            this.sliding = false;
        }
        
        this.clicked = flag;
        
        for (final GuiEnchantmentLabel label : this.enchantmentList)
            if (label.dragging)
                label.scroll(adjustedMouseX - 36, guiOffset + this.guiLeft + 10);
                
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }
    
    @Override
    public void drawScreen (int mouseX, int mouseY, float partialTicks) {
        
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.updateEnchantmentLabels();
        
        final int maxWidth = this.guiLeft - 20;
        final List<List<String>> information = new ArrayList<List<String>>();
        final ItemStack stack = this.container.tableInventory.getStackInSlot(0);
        
        information.add(this.fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", I18n.format("tooltip.eplus.playerlevel"), this.player.experienceLevel), maxWidth));
        
        if (ItemStackUtils.isValidStack(stack))
            if (this.hasLevelChanged()) {
                
                final boolean exp = this.totalCost <= EnchantmentUtils.getExperienceFromLevel(1) && this.totalCost >= -EnchantmentUtils.getExperienceFromLevel(1);
                final boolean negExp = this.totalCost < 0;
                final int finalCost = exp ? this.totalCost : negExp ? -EnchantmentUtils.getLevelsFromExperience(-this.totalCost) : EnchantmentUtils.getLevelsFromExperience(this.totalCost);
                information.add(this.fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", exp ? I18n.format("tooltip.eplus.experienceGained") : I18n.format("tooltip.eplus.enchant"), finalCost), maxWidth));
            }
            
            else if (ConfigurationHandler.allowRepairs && stack.isItemEnchanted() && stack.isItemDamaged())
                information.add(this.fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", I18n.format("tooltip.eplus.repair"), EnchantmentUtils.getLevelsFromExperience(this.totalCost)), maxWidth));
                
        information.add(this.fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", I18n.format("tooltip.eplus.maxlevel"), this.container.getEnchantingPower()), maxWidth));
        
        for (final List<String> display : information) {
            
            int height = information.indexOf(display) == 0 ? this.guiTop + this.fontRendererObj.FONT_HEIGHT + 8 : this.guiTop + (this.fontRendererObj.FONT_HEIGHT + 8) * (information.indexOf(display) + 1);
            
            if (information.indexOf(display) > 0)
                for (int i = information.indexOf(display) - 1; i >= 0; i--)
                    height += (this.fontRendererObj.FONT_HEIGHT + 3) * (information.get(i).size() - 1);
                    
            this.drawHoveringText(display, this.guiLeft - 20 - maxWidth, height, this.fontRendererObj);
        }
        
        final GuiEnchantmentLabel label = this.getSelectedLabel(mouseX, mouseY);
        
        if (isShiftKeyDown() && label != null && label.enchantment != null) {
            
            final String enchName = ChatFormatting.BOLD + label.getTranslatedName();
            String description = I18n.format("description." + label.enchantment.getName());
            
            if (description.startsWith("description."))
                description = ChatFormatting.RED + I18n.format("tooltip.eplus.nodesc") + ": description." + label.enchantment.getName();
                
            else
                description = ChatFormatting.LIGHT_PURPLE + description;
                
            final List<String> display = new ArrayList<String>();
            
            display.add(enchName);
            display.addAll(this.fontRendererObj.listFormattedStringToWidth(description, 215));
            display.add(ChatFormatting.BLUE + "" + ChatFormatting.ITALIC + ModUtils.getModName(label.enchantment));
            this.drawHoveringText(display, mouseX, mouseY, this.fontRendererObj);
        }
    }
    
    @Override
    public void handleMouseInput () throws IOException {
        
        super.handleMouseInput();
        
        final int eventDWheel = Mouse.getEventDWheel();
        final int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth - this.guiLeft;
        final int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1 - this.guiTop;
        
        if (eventDWheel != 0 && (mouseX >= 35 + guiOffset && mouseX <= this.xSize + guiOffset - 32 || mouseX >= 180 + guiOffset && mouseX <= 192 + guiOffset))
            if (mouseY >= 15 && mouseY <= 87)
                if (eventDWheel < 0) {
                    
                    this.sliderIndex += .25;
                    
                    if (this.sliderIndex >= this.enchantingPages)
                        this.sliderIndex = this.enchantingPages;
                }
                
                else {
                    
                    this.sliderIndex -= .25;
                    
                    if (this.sliderIndex <= 0)
                        this.sliderIndex = 0;
                }
    }
    
    @Override
    public void initGui () {
        
        super.initGui();
        this.buttonList.add(new GuiGraphicButton(0, this.guiLeft + guiOffset + 9, this.guiTop + 38, BUTTON_ENCHANT));
        this.buttonList.add(new GuiGraphicButton(1, this.guiLeft + guiOffset + 9, this.guiTop + 63, BUTTON_REPAIR));
        this.dirty = true;
    }
    
    @Override
    public void updateScreen () {
        
        super.updateScreen();
        
        final Map<Enchantment, Integer> enchantments = this.updateEnchantmentState(this.container.getEnchantments());
        
        this.handleChangedScreenSize(enchantments);
        this.updateEnchantmentLabels();
        
        this.enchantingPages = this.enchantmentList.size() / 4.0 > 1 ? this.enchantmentList.size() / 4.0 - 1.0 : 0;
        this.totalCost = 0;
        
        this.updateEnchantmentLabels(enchantments);
    }
    
    /**
     * Gets the GuiEnchantmentLabel that the mouse is currently hovering over.
     * 
     * @param mouseX The X position of the mouse.
     * @param mouseY The Y position of the mouse.
     * @return GuiEnchantmentLabel The current label being hovered over.
     */
    private GuiEnchantmentLabel getSelectedLabel (int mouseX, int mouseY) {
        
        if (mouseX < this.guiLeft + guiOffset + 35 || mouseX > this.guiLeft + this.xSize - 32)
            return null;
            
        for (final GuiEnchantmentLabel label : this.enchantmentList) {
            
            if (!label.show)
                continue;
                
            if (mouseY >= label.yPos && mouseY <= label.yPos + label.height)
                return label;
        }
        
        return null;
    }
    
    /**
     * Updates a specific GuiEnchantmentLabel to show correct position and level. This will
     * also update the cost.
     * 
     * @param level The correct level for the label to display.
     * @param label The label to update.
     */
    private void updateEnchantmentLabel (int level, GuiEnchantmentLabel label) {
        
        label.yPos = label.startingYPos - (int) (18 * 4 * this.sliderIndex);
        
        if (!label.locked && label.enchantmentLevel > level) {
            
            int cost = this.totalCost + this.container.enchantmentCost(label.enchantment, label.enchantmentLevel, level);
            
            if (!this.container.canPurchase(this.player, cost))
                
                while (label.enchantmentLevel > 0) {
                    
                    label.dragging = false;
                    label.enchantmentLevel--;
                    cost = this.totalCost + this.container.enchantmentCost(label.enchantment, label.enchantmentLevel, level);
                    
                    if (this.container.canPurchase(this.player, cost))
                        break;
                }
                
            this.totalCost = cost;
        }
        
        else if (label.enchantmentLevel < level && !label.locked)
            this.totalCost += this.container.getRebate(label.enchantment, label.enchantmentLevel, level);
    }
    
    /**
     * Updates the list of enchantment labels to reflect the state of the current enchantment
     * list.
     * 
     * @param enchantments The current map of enchantments where the key is an Enchantment and
     *        the value is the level of the enchantment.
     */
    private void updateEnchantmentLabels (Map<Enchantment, Integer> enchantments) {
        
        if (!this.enchantmentList.isEmpty() && this.hasLevelChanged()) {
            
            for (final GuiEnchantmentLabel label : this.enchantmentList)
                if (label != this.last)
                    this.updateEnchantmentLabel(enchantments.get(label.enchantment), label);
                    
            if (this.last != null)
                this.updateEnchantmentLabel(enchantments.get(this.last.enchantment), this.last);
        }
        
        else if (ConfigurationHandler.allowRepairs && !this.hasLevelChanged()) {
            
            this.totalCost += this.container.getRepairCost();
            
            for (final GuiEnchantmentLabel label : this.enchantmentList)
                label.yPos = label.startingYPos - (int) (18 * 4 * this.sliderIndex);
        }
    }
    
    /**
     * Resizes all of the enchantment labels to reflect the new size of the screen.
     * 
     * @param enchantments The current map of enchantments where the key is the enchantment and
     *        the value is the level of the enchantment.
     */
    private void handleChangedScreenSize (Map<Enchantment, Integer> enchantments) {
        
        if (this.dirty) {
            final List<GuiEnchantmentLabel> labels = this.getEnchantmentLabels(enchantments, 35 + guiOffset + this.guiLeft, 15 + this.guiTop);
            
            for (final GuiEnchantmentLabel label : this.enchantmentList)
                for (final GuiEnchantmentLabel tempItem : labels)
                    if (label.enchantment == tempItem.enchantment) {
                        
                        label.startingXPos = label.xPos = tempItem.xPos;
                        label.startingYPos = label.yPos = tempItem.yPos;
                    }
                    
            this.dirty = false;
        }
    }
    
    /**
     * Checks if the level of any of the enchantments has changed.
     * 
     * @return boolean Whether or not a level of an enchantment has changed.
     */
    protected boolean hasLevelChanged () {
        
        for (final GuiEnchantmentLabel label : this.enchantmentList)
            if (label.enchantmentLevel != label.currentLevel)
                return true;
                
        return false;
    }
    
    /**
     * Updates through all enchantment labels to update their status. For example, locking or
     * unlocking the slider label.
     */
    private void updateEnchantmentLabels () {
        
        int labelIndex = 0;
        
        for (final GuiEnchantmentLabel label : this.enchantmentList)
            label.locked = false;
            
        for (final GuiEnchantmentLabel mainLabel : this.enchantmentList)
            
            if (mainLabel.enchantmentLevel != 0) {
                
                labelIndex++;
                
                for (final GuiEnchantmentLabel otherLabel : this.enchantmentList)
                    if (mainLabel != otherLabel && !EnchantmentUtils.areEnchantmentsCompatible(mainLabel.enchantment, otherLabel.enchantment))
                        otherLabel.locked = true;
            }
            
            else if (!this.player.capabilities.isCreativeMode && ConfigurationHandler.maxEnchantmentAmount > 0 && labelIndex >= ConfigurationHandler.maxEnchantmentAmount)
                mainLabel.locked = true;
    }
    
    /**
     * Generates a list of GuiEnchantmentLabel which reflects the values in a standard
     * enchantment map.
     * 
     * @param map A map of enchantments where the key is an enchantment and the value is the
     *        level of that enchantment.
     * @param x The X position to start the list at.
     * @param y The Y position to start the list at.
     * @return ArrayList<GuiEnchantmentLable> The list of enchantment labels.
     */
    private List<GuiEnchantmentLabel> getEnchantmentLabels (final Map<Enchantment, Integer> map, int x, int y) {
        
        final List<GuiEnchantmentLabel> labels = new ArrayList<GuiEnchantmentLabel>();
        
        if (map == null)
            return labels;
            
        int pixelHeight = 0;
        int yPos = y;
        
        for (final Enchantment enchantment : map.keySet()) {
            
            labels.add(new GuiEnchantmentLabel(this.container, enchantment, map.get(enchantment), x, yPos));
            pixelHeight++;
            yPos = y + pixelHeight * 18;
        }
        
        return labels;
    }
    
    /**
     * Updates the state of all enchantments on the enchantment map, and the enchantment
     * labels.
     * 
     * @param enchantments The map of enchantments to update the gui to.
     * @return Map<Enchantment, Integer> The updated enchantment map.
     */
    private Map<Enchantment, Integer> updateEnchantmentState (final Map<Enchantment, Integer> enchantments) {
        
        if (this.enchantments != enchantments) {
            
            this.enchantments = enchantments;
            this.enchantmentList = this.getEnchantmentLabels(enchantments, 35 + guiOffset + this.guiLeft, 15 + this.guiTop);
            this.sliderIndex = this.enchantingPages = 0;
            this.clicked = this.sliding = false;
            return this.enchantments;
        }
        
        return enchantments;
    }
}