package net.epoxide.eplus.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.darkhax.bookshelf.client.gui.GuiGraphicButton;
import net.epoxide.eplus.EnchantingPlus;
import net.epoxide.eplus.common.network.PacketEnchant;
import net.epoxide.eplus.common.network.PacketGui;
import net.epoxide.eplus.common.network.PacketRepair;
import net.epoxide.eplus.handler.EPlusConfigurationHandler;
import net.epoxide.eplus.inventory.ContainerEnchantTable;
import net.epoxide.eplus.inventory.EnchantHelper;
import net.epoxide.eplus.tileentity.TileEntityEnchantTable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class GuiModEnchantmentTable extends GuiContainer {
    
    private final ResourceLocation texture = new ResourceLocation("eplus:textures/gui/enchant.png");
    
    private ArrayList<GuiEnchantmentLabel> enchantmentArray = new ArrayList<GuiEnchantmentLabel>();
    
    private final EntityPlayer player;
    private final ContainerEnchantTable container;
    private final int x;
    private final int y;
    private final int z;
    
    private static int guiOffset = 26;
    private double sliderIndex = 0;
    private double enchantingPages = 0;
    private Map<Integer, Integer> enchantments;
    private boolean clicked = false;
    private boolean sliding = false;
    private int totalCost = 0;
    private boolean dirty = false;
    
    public GuiModEnchantmentTable(InventoryPlayer inventory, World world, int x, int y, int z, TileEntityEnchantTable tileEntity) {
        
        super(new ContainerEnchantTable(inventory, world, x, y, z, tileEntity));
        
        this.player = inventory.player;
        this.container = (ContainerEnchantTable) inventorySlots;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xSize = 235;
        this.ySize = 182;
        this.zLevel = -1;
    }
    
    @Override
    protected void actionPerformed (GuiButton par1GuiButton) {
        
        final HashMap<Integer, Integer> enchants = new HashMap<Integer, Integer>();
        final HashMap<Integer, Integer> levels = new HashMap<Integer, Integer>();
        
        for (final GuiEnchantmentLabel label : enchantmentArray) {
            final Integer id = enchantments.get(label.enchantment.effectId);
            if (label.enchantmentLevel != id && !label.locked) {
                enchants.put(label.enchantment.effectId, label.enchantmentLevel);
                levels.put(label.enchantment.effectId, label.currentLevel);
            }
        }
        
        switch (par1GuiButton.id) {
            case 0:
                if (enchants.size() > 0) {
                    EnchantingPlus.network.sendToServer(new PacketEnchant(enchants, levels, totalCost));
                }
                return;
            case 1:
                if (enchants.size() == 0 && EPlusConfigurationHandler.allowRepairs) {
                    EnchantingPlus.network.sendToServer(new PacketRepair(totalCost));
                }
                return;
            case 2:
                EnchantingPlus.network.sendToServer(new PacketGui(player.getDisplayName(), 1, x, y, z));
        }
    }
    
    /**
     * Converts map to arraylist of gui items
     *
     * @param map the map of enchantments to convert
     * @param x starting x position
     * @param y starting y position
     * @return the arraylist of gui items
     */
    private ArrayList<GuiEnchantmentLabel> convertMapToGuiItems (final Map<Integer, Integer> map, int x, int y) {
        
        final ArrayList<GuiEnchantmentLabel> temp = new ArrayList<GuiEnchantmentLabel>();
        
        if (map == null)
            return temp;
            
        int i = 0;
        int yPos = y;
        for (Integer obj : map.keySet()) {
            
            temp.add(new GuiEnchantmentLabel(container, obj, map.get(obj), x, yPos));
            i++;
            yPos = y + i * 18;
        }
        
        return temp;
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer (float partialTicks, int mouseX, int mouseY) {
        
        boolean flag = Mouse.isButtonDown(0);
        
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        
        for (final GuiEnchantmentLabel label : enchantmentArray) {
            if (label.yPos < guiTop + 15 || label.yPos >= guiTop + 87) {
                label.show(false);
            }
            else {
                label.show(true);
            }
            label.draw(fontRendererObj);
        }
        
        final int adjustedMouseX = mouseX - guiLeft;
        final int adjustedMouseY = mouseY - guiTop;
        mc.renderEngine.bindTexture(texture);
        int tempY = adjustedMouseY - 16;
        if (tempY <= 0) {
            tempY = 0;
        }
        else if (tempY >= 57) {
            tempY = 57;
        }
        sliderIndex = sliding ? Math.round((tempY / 57D * enchantingPages) / .25) * .25 : sliderIndex;
        
        if (sliderIndex >= enchantingPages) {
            sliderIndex = enchantingPages;
        }
        
        double sliderY = sliding ? tempY : 57 * (sliderIndex / enchantingPages);
        
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        drawTexturedModalRect(guiLeft + guiOffset + 180, guiTop + 16 + (int) sliderY, 0, 182, 12, 15);
        
        if (!clicked && flag) {
            for (final GuiEnchantmentLabel label : enchantmentArray) {
                if (getItemFromPos(mouseX, mouseY) == label && !label.locked) {
                    label.dragging = true;
                }
            }
            if (adjustedMouseX <= 191 + guiOffset && adjustedMouseX >= 180 + guiOffset) {
                if (enchantingPages != 0) {
                    sliding = true;
                }
            }
        }
        
        for (final GuiEnchantmentLabel label : enchantmentArray) {
            if (label.dragging && getItemFromPos(mouseX, mouseY) != label) {
                label.dragging = false;
            }
        }
        
        if (!flag) {
            for (final GuiEnchantmentLabel label : enchantmentArray) {
                if (getItemFromPos(mouseX, mouseY) == label) {
                    label.dragging = false;
                }
            }
            sliding = false;
        }
        
        clicked = flag;
        
        for (final GuiEnchantmentLabel label : enchantmentArray) {
            if (label.dragging) {
                label.scroll(adjustedMouseX - 36, guiOffset + guiLeft + 10);
            }
        }
    }
    
    @Override
    public void drawScreen (int mouseX, int mouseY, float partialTicks) {
        
        super.drawScreen(mouseX, mouseY, partialTicks);
        updateEnchantmentLabels();
        
        final int maxWidth = guiLeft - 20;
        final List<List<String>> information = new ArrayList<List<String>>();
        information.add(fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", StatCollector.translateToLocal("tooltip.eplus.playerlevel"), player.experienceLevel), maxWidth));
        
        if (container.tableInventory.getStackInSlot(0) == null || levelChanged() || !levelChanged() && !container.tableInventory.getStackInSlot(0).isItemDamaged()) {
            information.add(fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", StatCollector.translateToLocal("tooltip.eplus.enchant"), totalCost), maxWidth));
        }
        else if (EPlusConfigurationHandler.allowRepairs && !levelChanged() && container.tableInventory.getStackInSlot(0).isItemDamaged()) {
            information.add(fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", StatCollector.translateToLocal("tooltip.eplus.repair"), totalCost), maxWidth));
        }
        information.add(fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", StatCollector.translateToLocal("tooltip.eplus.maxlevel"), container.bookCases()), maxWidth));
        
        for (final List<String> display : information) {
            int height = information.indexOf(display) == 0 ? guiTop + fontRendererObj.FONT_HEIGHT + 8 : guiTop + (fontRendererObj.FONT_HEIGHT + 8) * (information.indexOf(display) + 1);
            if (information.indexOf(display) > 0) {
                for (int i = information.indexOf(display) - 1; i >= 0; i--) {
                    height += (fontRendererObj.FONT_HEIGHT + 3) * (information.get(i).size() - 1);
                }
            }
            
            try {
                drawHoveringText(display, guiLeft - 20 - maxWidth, height, fontRendererObj);
            }
            catch (NoSuchMethodError e) {
                final StringBuilder sb = new StringBuilder();
                
                for (final String text : display) {
                    sb.append(text);
                    sb.append(" ");
                }
                drawCreativeTabHoveringText(sb.toString(), guiLeft - 20 - maxWidth, height);
            }
        }
        
        GuiEnchantmentLabel guiItem = getItemFromPos(mouseX, mouseY);
        if (isShiftKeyDown() && guiItem != null) {
            final String name = EnumChatFormatting.BOLD + guiItem.getTranslatedName();
            String info = StatCollector.translateToLocal(guiItem.enchantment.getName());
            
            // TODO FIX Strings.errorToolTip
            if (info.isEmpty()) {
                info = EnumChatFormatting.DARK_RED + String.format("%s ", "ERROR") + guiItem.enchantment.getName();
            }
            
            info = EnumChatFormatting.LIGHT_PURPLE + info;
            
            final List<String> display = new ArrayList<String>();
            
            display.add(name);
            display.addAll(fontRendererObj.listFormattedStringToWidth(info, 150));
            try {
                drawHoveringText(display, mouseX, mouseY, fontRendererObj);
            }
            catch (NoSuchMethodError e) {
                final StringBuilder sb = new StringBuilder();
                
                for (final String text : display) {
                    sb.append(text);
                    sb.append(" ");
                }
                drawCreativeTabHoveringText(sb.toString(), guiLeft - 20 - maxWidth, height);
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
        
        if (x < guiLeft + guiOffset + 35 || x > guiLeft + xSize - 32) {
            return null;
        }
        
        for (final GuiEnchantmentLabel label : enchantmentArray) {
            if (!label.show) {
                continue;
            }
            if (y >= label.yPos && y <= label.yPos + label.height) {
                return label;
            }
        }
        return null;
    }
    
    @Override
    public void handleMouseInput () {
        
        super.handleMouseInput();
        
        final int eventDWheel = Mouse.getEventDWheel();
        final int mouseX = Mouse.getEventX() * width / mc.displayWidth - guiLeft;
        final int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1 - guiTop;
        
        if (eventDWheel != 0 && (mouseX >= 35 + guiOffset && mouseX <= xSize + guiOffset - 32 || mouseX >= 180 + guiOffset && mouseX <= 192 + guiOffset)) {
            if (mouseY >= 15 && mouseY <= 87) {
                if (eventDWheel < 0) {
                    sliderIndex += .25;
                    if (sliderIndex >= enchantingPages) {
                        sliderIndex = enchantingPages;
                    }
                }
                else {
                    sliderIndex -= .25;
                    if (sliderIndex <= 0) {
                        sliderIndex = 0;
                    }
                }
            }
        }
    }
    
    @Override
    public void initGui () {
        
        super.initGui();
        buttonList.add(new GuiGraphicButton(0, guiLeft + guiOffset + 9, guiTop + 38, "eplus:textures/gui/button_enchant"));
        buttonList.add(new GuiGraphicButton(1, guiLeft + guiOffset + 9, guiTop + 63, "eplus:textures/gui/button_repair"));
        buttonList.add(new GuiButton(2, guiLeft - 20, guiTop + 110, fontRendererObj.getStringWidth(StatCollector.translateToLocal("Vanilla")) + 10, 20, StatCollector.translateToLocal("Vanilla")));
        
        dirty = true;
    }
    
    protected boolean levelChanged () {
        
        for (final GuiEnchantmentLabel label : enchantmentArray)
            if (label.enchantmentLevel != label.currentLevel)
                return true;
                
        return false;
    }
    
    @Override
    public void updateScreen () {
        
        super.updateScreen();
        
        final Map<Integer, Integer> enchantments = updateEnchantments(container.getEnchantments());
        
        handleChangedScreenSize(enchantments);
        updateEnchantmentLabels();
        
        enchantingPages = enchantmentArray.size() / 4.0 > 1 ? enchantmentArray.size() / 4.0 - 1.0 : 0;
        totalCost = 0;
        
        handleChangedEnchantments(enchantments);
    }
    
    /**
     * Updates the locked status of every single enchantment label.
     */
    private void updateEnchantmentLabels () {
        
        for (GuiEnchantmentLabel label : enchantmentArray)
            label.locked = false;
            
        for (GuiEnchantmentLabel mainLabel : enchantmentArray)
            if (mainLabel.enchantmentLevel != 0)
                for (final GuiEnchantmentLabel otherLabel : enchantmentArray)
                    if (mainLabel != otherLabel && !EnchantHelper.isEnchantmentsCompatible(mainLabel.enchantment, otherLabel.enchantment))
                        otherLabel.locked = true;
    }
    
    private void handleChangedEnchantments (Map<Integer, Integer> enchantments) {
        
        if (!enchantmentArray.isEmpty() && levelChanged())
            for (final GuiEnchantmentLabel label : enchantmentArray)
                handleChangedEnchantment(enchantments, label);
                
        else if (EPlusConfigurationHandler.allowRepairs && !levelChanged()) {
            
            totalCost += container.repairCostMax();
            
            for (final GuiEnchantmentLabel label : enchantmentArray)
                label.yPos = label.startingYPos - (int) (18 * 4 * sliderIndex);
        }
    }
    
    private void handleChangedEnchantment (Map<Integer, Integer> enchantments, GuiEnchantmentLabel label) {
        
        label.yPos = label.startingYPos - (int) (18 * 4 * sliderIndex);
        
        final Integer level = enchantments.get(label.enchantment.effectId);
        if (!label.locked && label.enchantmentLevel > level) {
            int temp = totalCost + container.enchantmentCost(label.enchantment, label.enchantmentLevel, level);
            
            if (!container.canPurchase(player, temp)) {
                while (label.enchantmentLevel > 0) {
                    label.dragging = false;
                    label.enchantmentLevel--;
                    temp = totalCost + container.enchantmentCost(label.enchantment, label.enchantmentLevel, level);
                    if (container.canPurchase(player, temp)) {
                        break;
                    }
                    
                }
            }
            totalCost = temp;
        }
        else if (label.enchantmentLevel < level && !label.locked) {
            if (EnchantHelper.hasRestriction(container.tableInventory.getStackInSlot(0).getTagCompound().getTagList("restrictions", 10), label.enchantment.effectId, label.enchantmentLevel) || EPlusConfigurationHandler.allowDisenUnowned) {
                totalCost += container.disenchantmentCost(label.enchantment, label.enchantmentLevel, level);
            }
            else {
                totalCost = 0;
            }
        }
    }
    
    private void handleChangedScreenSize (Map<Integer, Integer> enchantments) {
        
        if (dirty) {
            final ArrayList<GuiEnchantmentLabel> temp = convertMapToGuiItems(enchantments, 35 + guiOffset + guiLeft, 15 + guiTop);
            
            for (final GuiEnchantmentLabel label : enchantmentArray) {
                for (final GuiEnchantmentLabel tempItem : temp) {
                    if (label.enchantment == tempItem.enchantment) {
                        label.startingXPos = label.xPos = tempItem.xPos;
                        label.startingYPos = label.yPos = tempItem.yPos;
                    }
                }
            }
            dirty = false;
        }
    }
    
    private Map<Integer, Integer> updateEnchantments (final Map<Integer, Integer> enchantments) {
        
        if (this.enchantments != enchantments) {
            
            this.enchantments = enchantments;
            enchantmentArray = convertMapToGuiItems(enchantments, 35 + guiOffset + guiLeft, 15 + guiTop);
            sliderIndex = enchantingPages = 0;
            clicked = sliding = false;
            return this.enchantments;
        }
        
        return enchantments;
    }
}
