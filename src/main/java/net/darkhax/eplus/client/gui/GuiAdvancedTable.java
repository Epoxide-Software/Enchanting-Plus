package net.darkhax.eplus.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.darkhax.bookshelf.client.gui.GuiGraphicButton;
import net.darkhax.bookshelf.lib.util.EnchantmentUtils;
import net.darkhax.bookshelf.lib.util.Utilities;
import net.darkhax.eplus.handler.ConfigurationHandler;
import net.darkhax.eplus.inventory.ContainerAdvancedTable;
import net.darkhax.eplus.tileentity.TileEntityAdvancedTable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class GuiAdvancedTable extends GuiContainer {
    
    private static int guiOffset = 26;
    
    private final ResourceLocation texture = new ResourceLocation("eplus:textures/gui/enchant.png");
    
    private ArrayList<GuiEnchantmentLabel> enchantmentArray = new ArrayList<GuiEnchantmentLabel>();
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
    
    public GuiAdvancedTable(InventoryPlayer inventory, World world, BlockPos pos, TileEntityAdvancedTable tileEntity) {
        
        super(new ContainerAdvancedTable(inventory, world, pos, tileEntity));
        
        this.player = inventory.player;
        this.container = (ContainerAdvancedTable) this.inventorySlots;
        this.xSize = 235;
        this.ySize = 182;
        this.zLevel = -1;
    }
    
    @Override
    protected void actionPerformed (GuiButton par1GuiButton) {
        
        final HashMap<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
        
        for (final GuiEnchantmentLabel label : this.enchantmentArray)
            if (label.enchantmentLevel != this.enchantments.get(label.enchantment) && !label.locked)
                enchants.put(label.enchantment, label.enchantmentLevel);
                
        /*
         * TODO fix packets switch (par1GuiButton.id) { case 0: if (enchants.size() > 0) {
         * EnchantingPlus.network.sendToServer(new PacketEnchant(enchants, totalCost)); }
         * return; case 1: if (enchants.size() == 0 && ConfigurationHandler.allowRepairs) {
         * EnchantingPlus.network.sendToServer(new PacketRepair(totalCost)); } return; case 2:
         * EnchantingPlus.network.sendToServer(new PacketGui(player.getDisplayName(), 1, x, y,
         * z)); }
         */
    }
    
    /**
     * Converts map to arraylist of gui items
     *
     * @param map the map of enchantments to convert
     * @param x starting x position
     * @param y starting y position
     * @return the arraylist of gui items
     */
    private ArrayList<GuiEnchantmentLabel> convertMapToGuiItems (final Map<Enchantment, Integer> map, int x, int y) {
        
        final ArrayList<GuiEnchantmentLabel> temp = new ArrayList<GuiEnchantmentLabel>();
        
        if (map == null)
            return temp;
            
        int pixelHeight = 0;
        int yPos = y;
        
        for (final Enchantment obj : map.keySet()) {
            
            temp.add(new GuiEnchantmentLabel(this.container, obj, map.get(obj), x, yPos));
            pixelHeight++;
            yPos = y + pixelHeight * 18;
        }
        
        return temp;
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer (float partialTicks, int mouseX, int mouseY) {
        
        final boolean flag = Mouse.isButtonDown(0);
        
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(this.texture);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        for (final GuiEnchantmentLabel label : this.enchantmentArray) {
            if (label.yPos < this.guiTop + 15 || label.yPos >= this.guiTop + 87)
                label.show(false);
            else
                label.show(true);
            label.draw(this.fontRendererObj);
        }
        
        final int adjustedMouseX = mouseX - this.guiLeft;
        final int adjustedMouseY = mouseY - this.guiTop;
        this.mc.renderEngine.bindTexture(this.texture);
        int tempY = adjustedMouseY - 16;
        if (tempY <= 0)
            tempY = 0;
        else if (tempY >= 57)
            tempY = 57;
        this.sliderIndex = this.sliding ? Math.round(tempY / 57D * this.enchantingPages / .25) * .25 : this.sliderIndex;
        
        if (this.sliderIndex >= this.enchantingPages)
            this.sliderIndex = this.enchantingPages;
            
        final double sliderY = this.sliding ? tempY : 57 * (this.sliderIndex / this.enchantingPages);
        
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawTexturedModalRect(this.guiLeft + guiOffset + 180, this.guiTop + 16 + (int) sliderY, 0, 182, 12, 15);
        
        if (!this.clicked && flag) {
            for (final GuiEnchantmentLabel label : this.enchantmentArray)
                if (this.getItemFromPos(mouseX, mouseY) == label && !label.locked)
                    label.dragging = true;
            if (adjustedMouseX <= 191 + guiOffset && adjustedMouseX >= 180 + guiOffset)
                if (this.enchantingPages != 0)
                    this.sliding = true;
        }
        
        for (final GuiEnchantmentLabel label : this.enchantmentArray)
            if (label.dragging && this.getItemFromPos(mouseX, mouseY) != label) {
                label.dragging = false;
                this.last = label;
            }
            
        if (!flag) {
            for (final GuiEnchantmentLabel label : this.enchantmentArray)
                if (this.getItemFromPos(mouseX, mouseY) == label) {
                    label.dragging = false;
                    this.last = label;
                }
            this.sliding = false;
        }
        
        this.clicked = flag;
        
        for (final GuiEnchantmentLabel label : this.enchantmentArray)
            if (label.dragging)
                label.scroll(adjustedMouseX - 36, guiOffset + this.guiLeft + 10);
    }
    
    @Override
    public void drawScreen (int mouseX, int mouseY, float partialTicks) {
        
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.updateEnchantmentLabels();
        
        final int maxWidth = this.guiLeft - 20;
        final List<List<String>> information = new ArrayList<List<String>>();
        information.add(this.fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", I18n.translateToLocal("tooltip.eplus.playerlevel"), this.player.experienceLevel), maxWidth));
        
        if (this.container.tableInventory.getStackInSlot(0) == null || this.levelChanged() || !this.levelChanged() && !this.container.tableInventory.getStackInSlot(0).isItemDamaged()) {
            final boolean exp = this.totalCost <= EnchantmentUtils.getExperienceFromLevel(1) && this.totalCost >= -EnchantmentUtils.getExperienceFromLevel(1);
            final boolean negExp = this.totalCost < 0;
            final int finalCost = exp ? this.totalCost : negExp ? -EnchantmentUtils.getLevelsFromExperience(-this.totalCost) : EnchantmentUtils.getLevelsFromExperience(this.totalCost);
            information.add(this.fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", exp ? I18n.translateToLocal("tooltip.eplus.experienceGained") : I18n.translateToLocal("tooltip.eplus.enchant"), finalCost), maxWidth));
        }
        else if (ConfigurationHandler.allowRepairs && !this.levelChanged() && this.container.tableInventory.getStackInSlot(0).isItemDamaged())
            information.add(this.fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", I18n.translateToLocal("tooltip.eplus.repair"), EnchantmentUtils.getLevelsFromExperience(this.totalCost)), maxWidth));
        information.add(this.fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", I18n.translateToLocal("tooltip.eplus.maxlevel"), this.container.bookCases()), maxWidth));
        
        for (final List<String> display : information) {
            int height = information.indexOf(display) == 0 ? this.guiTop + this.fontRendererObj.FONT_HEIGHT + 8 : this.guiTop + (this.fontRendererObj.FONT_HEIGHT + 8) * (information.indexOf(display) + 1);
            if (information.indexOf(display) > 0)
                for (int i = information.indexOf(display) - 1; i >= 0; i--)
                    height += (this.fontRendererObj.FONT_HEIGHT + 3) * (information.get(i).size() - 1);
                    
            try {
                this.drawHoveringText(display, this.guiLeft - 20 - maxWidth, height, this.fontRendererObj);
            }
            catch (final NoSuchMethodError e) {
                final StringBuilder sb = new StringBuilder();
                
                for (final String text : display) {
                    sb.append(text);
                    sb.append(" ");
                }
                this.drawCreativeTabHoveringText(sb.toString(), this.guiLeft - 20 - maxWidth, height);
            }
        }
        
        final GuiEnchantmentLabel label = this.getItemFromPos(mouseX, mouseY);
        
        if (isShiftKeyDown() && label != null && label.enchantment != null) {
            
            final String enchName = ChatFormatting.BOLD + label.getTranslatedName();
            String description = I18n.translateToLocal("description." + label.enchantment.getName());
            
            if (description.startsWith("description."))
                description = ChatFormatting.RED + I18n.translateToLocal("tooltip.eplus.nodesc") + ": description." + label.enchantment.getName();
                
            else
                description = ChatFormatting.LIGHT_PURPLE + description;
                
            final List<String> display = new ArrayList<String>();
            
            display.add(enchName);
            display.addAll(this.fontRendererObj.listFormattedStringToWidth(description, 215));
            display.add(ChatFormatting.BLUE + "" + ChatFormatting.ITALIC + Utilities.getModName(label.enchantment));
            
            try {
                
                this.drawHoveringText(display, mouseX, mouseY, this.fontRendererObj);
            }
            
            catch (final NoSuchMethodError e) {
                
                final StringBuilder sb = new StringBuilder();
                
                for (final String text : display) {
                    
                    sb.append(text);
                    sb.append(" ");
                }
                
                this.drawCreativeTabHoveringText(sb.toString(), this.guiLeft - 20 - maxWidth, this.height);
            }
        }
    }
    
    /**
     * Gets a GuiEnchantmentLabel from mouse position
     *
     * @param x mouse x position
     * @param y mouse y position
     * @return the GuiEnchantmentLabel found
     */
    private GuiEnchantmentLabel getItemFromPos (int x, int y) {
        
        if (x < this.guiLeft + guiOffset + 35 || x > this.guiLeft + this.xSize - 32)
            return null;
            
        for (final GuiEnchantmentLabel label : this.enchantmentArray) {
            if (!label.show)
                continue;
            if (y >= label.yPos && y <= label.yPos + label.height)
                return label;
        }
        return null;
    }
    
    private void handleChangedEnchantment (Map<Enchantment, Integer> enchantments, GuiEnchantmentLabel label) {
        
        label.yPos = label.startingYPos - (int) (18 * 4 * this.sliderIndex);
        
        final Integer level = enchantments.get(label.enchantment);
        if (!label.locked && label.enchantmentLevel > level) {
            int temp = this.totalCost + this.container.enchantmentCost(label.enchantment, label.enchantmentLevel, level);
            
            if (!this.container.canPurchase(this.player, temp))
                while (label.enchantmentLevel > 0) {
                    label.dragging = false;
                    label.enchantmentLevel--;
                    temp = this.totalCost + this.container.enchantmentCost(label.enchantment, label.enchantmentLevel, level);
                    if (this.container.canPurchase(this.player, temp))
                        break;
                        
                }
            this.totalCost = temp;
        }
        else if (label.enchantmentLevel < level && !label.locked)
            this.totalCost += this.container.getRebate(label.enchantment, label.enchantmentLevel, level);
    }
    
    private void handleChangedEnchantments (Map<Enchantment, Integer> enchantments) {
        
        if (!this.enchantmentArray.isEmpty() && this.levelChanged()) {
            for (final GuiEnchantmentLabel label : this.enchantmentArray)
                if (label != this.last)
                    this.handleChangedEnchantment(enchantments, label);
            if (this.last != null)
                this.handleChangedEnchantment(enchantments, this.last);
        }
        else if (ConfigurationHandler.allowRepairs && !this.levelChanged()) {
            
            this.totalCost += this.container.getRepairCost();
            
            for (final GuiEnchantmentLabel label : this.enchantmentArray)
                label.yPos = label.startingYPos - (int) (18 * 4 * this.sliderIndex);
        }
    }
    
    private void handleChangedScreenSize (Map<Enchantment, Integer> enchantments) {
        
        if (this.dirty) {
            final ArrayList<GuiEnchantmentLabel> temp = this.convertMapToGuiItems(enchantments, 35 + guiOffset + this.guiLeft, 15 + this.guiTop);
            
            for (final GuiEnchantmentLabel label : this.enchantmentArray)
                for (final GuiEnchantmentLabel tempItem : temp)
                    if (label.enchantment == tempItem.enchantment) {
                        label.startingXPos = label.xPos = tempItem.xPos;
                        label.startingYPos = label.yPos = tempItem.yPos;
                    }
            this.dirty = false;
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
        this.buttonList.add(new GuiGraphicButton(0, this.guiLeft + guiOffset + 9, this.guiTop + 38, new ResourceLocation("eplus:textures/gui/button_enchant.png")));
        this.buttonList.add(new GuiGraphicButton(1, this.guiLeft + guiOffset + 9, this.guiTop + 63, new ResourceLocation("eplus:textures/gui/button_repair.png")));        
        this.dirty = true;
    }
    
    protected boolean levelChanged () {
        
        for (final GuiEnchantmentLabel label : this.enchantmentArray)
            if (label.enchantmentLevel != label.currentLevel)
                return true;
                
        return false;
    }
    
    /**
     * Updates the locked status of every single enchantment label.
     */
    private void updateEnchantmentLabels () {
        
        for (final GuiEnchantmentLabel label : this.enchantmentArray)
            label.locked = false;
            
        int i = 0;
        for (final GuiEnchantmentLabel mainLabel : this.enchantmentArray)
            if (mainLabel.enchantmentLevel != 0) {
                i++;
                for (final GuiEnchantmentLabel otherLabel : this.enchantmentArray)
                    if (mainLabel != otherLabel && !EnchantmentUtils.areEnchantmentsCompatible(mainLabel.enchantment, otherLabel.enchantment))
                        otherLabel.locked = true;
            }
            else if (!this.player.capabilities.isCreativeMode && ConfigurationHandler.maxEnchantmentAmount > 0 && i >= ConfigurationHandler.maxEnchantmentAmount)
                mainLabel.locked = true;
    }
    
    private Map<Enchantment, Integer> updateEnchantments (final Map<Enchantment, Integer> enchantments) {
        
        if (this.enchantments != enchantments) {
            
            this.enchantments = enchantments;
            this.enchantmentArray = this.convertMapToGuiItems(enchantments, 35 + guiOffset + this.guiLeft, 15 + this.guiTop);
            this.sliderIndex = this.enchantingPages = 0;
            this.clicked = this.sliding = false;
            return this.enchantments;
        }
        
        return enchantments;
    }
    
    @Override
    public void updateScreen () {
        
        super.updateScreen();
        
        final Map<Enchantment, Integer> enchantments = this.updateEnchantments(this.container.getEnchantments());
        
        this.handleChangedScreenSize(enchantments);
        this.updateEnchantmentLabels();
        
        this.enchantingPages = this.enchantmentArray.size() / 4.0 > 1 ? this.enchantmentArray.size() / 4.0 - 1.0 : 0;
        this.totalCost = 0;
        
        this.handleChangedEnchantments(enchantments);
    }
}