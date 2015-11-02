package net.epoxide.eplus.client.gui;

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
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public GuiModEnchantmentTable (InventoryPlayer inventory, World world, int x, int y, int z, TileEntityEnchantTable tileEntity) {

        super(new ContainerEnchantTable(inventory, world, x, y, z, tileEntity));
        this.player = inventory.player;

        this.container = (ContainerEnchantTable) inventorySlots;

        this.x = x;
        this.y = y;
        this.z = z;

        this.xSize = 235;
        this.ySize = 182;
    }

    @Override
    protected void actionPerformed (GuiButton par1GuiButton) {

        final HashMap<Integer, Integer> enchants = new HashMap<Integer, Integer>();
        final HashMap<Integer, Integer> levels = new HashMap<Integer, Integer>();

        for (final GuiEnchantmentLabel item : enchantmentArray) {
            final Integer id = enchantments.get(item.enchantment.effectId);
            if (item.enchantmentLevel != id && !item.locked) {
                enchants.put(item.enchantment.effectId, item.enchantmentLevel);
                levels.put(item.enchantment.effectId, item.currentLevel);
            }
        }

        switch (par1GuiButton.id) {
            case 0:
                if (enchants.size() > 0) {
                    EnchantingPlus.network.sendToServer(new PacketEnchant(enchants, levels, totalCost));
                }
                return;
            case 1:
                if (enchants.size() == 0 && EPlusConfigurationHandler.allowRepair) {
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
     * @param x   starting x position
     * @param y   starting y position
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

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        for (final GuiEnchantmentLabel item : enchantmentArray) {
            if (item.yPos < guiTop + 15 || item.yPos >= guiTop + 87) {
                item.show(false);
            }
            else {
                item.show(true);
            }
            item.draw(fontRendererObj);
        }
    }

    @Override
    public void drawScreen (int mouseX, int mouseY, float partialTicks) {

        super.drawScreen(mouseX, mouseY, partialTicks);
        enableEnchantments();
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

        if (!clicked && Mouse.isButtonDown(0)) {
            for (final GuiEnchantmentLabel item : enchantmentArray) {
                if (getItemFromPos(mouseX, mouseY) == item && !item.locked) {
                    item.dragging = true;
                }
            }
            if (adjustedMouseX <= 191 + guiOffset && adjustedMouseX >= 180 + guiOffset) {
                if (enchantingPages != 0) {
                    sliding = true;
                }
            }
        }

        for (final GuiEnchantmentLabel item : enchantmentArray) {
            if (item.dragging && getItemFromPos(mouseX, mouseY) != item) {
                item.dragging = false;
            }
        }

        if (!Mouse.isButtonDown(0)) {
            for (final GuiEnchantmentLabel item : enchantmentArray) {
                if (getItemFromPos(mouseX, mouseY) == item) {
                    item.dragging = false;
                }
            }
            if (adjustedMouseX <= 191 + guiOffset && adjustedMouseX >= 180 + guiOffset) {
                sliding = false;
            }
        }

        clicked = Mouse.isButtonDown(0);

        for (final GuiEnchantmentLabel item : enchantmentArray) {
            if (item.dragging) {
                item.scroll(adjustedMouseX - 36, guiOffset + guiLeft + 10);
            }
        }

        final int maxWidth = guiLeft - 20;
        final List<List<String>> information = new ArrayList<List<String>>();
        information.add(fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", StatCollector.translateToLocal("player.level"), player.experienceLevel), maxWidth));

        if (container.tableInventory.getStackInSlot(0) == null || levelChanged() || !levelChanged() && !container.tableInventory.getStackInSlot(0).isItemDamaged()) {
            information.add(fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", StatCollector.translateToLocal("enchanting.cost"), totalCost), maxWidth));
        }
        else if (EPlusConfigurationHandler.allowRepair && !levelChanged() && container.tableInventory.getStackInSlot(0).isItemDamaged()) {
            information.add(fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", StatCollector.translateToLocal("repair.cost"), totalCost), maxWidth));
        }
        information.add(fontRendererObj.listFormattedStringToWidth(String.format("%s: %s", StatCollector.translateToLocal("enchant.level.max"), container.bookCases()), maxWidth));

        for (final List<String> display : information) {
            int height = information.indexOf(display) == 0 ? guiTop + fontRendererObj.FONT_HEIGHT + 8 : guiTop + (fontRendererObj.FONT_HEIGHT + 8) * (information.indexOf(display) + 1);
            if (information.indexOf(display) > 0) {
                for (int i = information.indexOf(display) - 1; i >= 0; i--) {
                    height += (fontRendererObj.FONT_HEIGHT + 3) * (information.get(i).size() - 1);
                }
            }

            try {
                drawHoveringText(display, guiLeft - 20 - maxWidth, height, fontRendererObj);
            } catch (NoSuchMethodError e) {
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

            //TODO FIX Strings.errorToolTip
            if (info.isEmpty()) {
                info = EnumChatFormatting.DARK_RED + String.format("%s ", "ERROR") + guiItem.enchantment.getName();
            }

            info = EnumChatFormatting.LIGHT_PURPLE + info;

            final List<String> display = new ArrayList<String>();

            display.add(name);
            display.addAll(fontRendererObj.listFormattedStringToWidth(info, 150));
            try {
                drawHoveringText(display, mouseX, mouseY, fontRendererObj);
            } catch (NoSuchMethodError e) {
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

        for (final GuiEnchantmentLabel item : enchantmentArray) {
            if (!item.show) {
                continue;
            }
            if (y >= item.yPos && y <= item.yPos + item.height) {
                return item;
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
        buttonList.add(new GuiIcon(0, guiLeft + guiOffset + 9, guiTop + 38, "E").customTexture(0));
        buttonList.add(new GuiIcon(1, guiLeft + guiOffset + 9, guiTop + 63, "R").customTexture(0));
        buttonList.add(new GuiButton(2, guiLeft + xSize + 10, guiTop + 5, fontRendererObj.getStringWidth(StatCollector.translateToLocal("Vanilla")) + 10, 20, StatCollector.translateToLocal("Vanilla")));

        dirty = true;
    }

    protected boolean levelChanged () {

        for (final GuiEnchantmentLabel item : enchantmentArray) {
            if (item.enchantmentLevel != item.currentLevel) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateScreen () {

        super.updateScreen();

        final Map<Integer, Integer> enchantments = updateEnchantments(container.getEnchantments());

        handleChangedScreenSize(enchantments);
        enableEnchantments();

        enchantingPages = enchantmentArray.size() / 4.0 > 1 ? enchantmentArray.size() / 4.0 - 1.0 : 0;
        totalCost = 0;

        handleChangedEnchantments(enchantments);
    }

    private void enableEnchantments () {

        for (GuiEnchantmentLabel item : enchantmentArray) {
            if (item.enchantmentLevel != 0) {
                for (final GuiEnchantmentLabel item2 : enchantmentArray) {
                    if (item == item2)
                        continue;

                    item2.locked = !item.enchantment.canApplyTogether(item2.enchantment) || !item2.enchantment.canApplyTogether(item.enchantment);
                }
            }
        }
    }

    private void handleChangedEnchantments (Map<Integer, Integer> enchantments) {

        if (!enchantmentArray.isEmpty() && levelChanged()) {
            for (final GuiEnchantmentLabel item : enchantmentArray) {
                handleChangedEnchantment(enchantments, item);
            }
        }
        else if (EPlusConfigurationHandler.allowRepair && !levelChanged()) {
            totalCost += container.repairCostMax();

            for (final GuiEnchantmentLabel item : enchantmentArray) {
                item.yPos = item.startingYPos - (int) (18 * 4 * sliderIndex);
            }
        }
    }

    private void handleChangedEnchantment (Map<Integer, Integer> enchantments, GuiEnchantmentLabel item) {

        item.yPos = item.startingYPos - (int) (18 * 4 * sliderIndex);

        final Integer level = enchantments.get(item.enchantment.effectId);
        if (!item.locked && item.enchantmentLevel > level) {
            int temp = totalCost + container.enchantmentCost(item.enchantment, item.enchantmentLevel, level);

            if (!container.canPurchase(player, temp)) {
                while (item.enchantmentLevel > 0) {
                    item.dragging = false;
                    item.enchantmentLevel--;
                    temp = totalCost + container.enchantmentCost(item.enchantment, item.enchantmentLevel, level);
                    if (container.canPurchase(player, temp)) {
                        break;
                    }

                }
            }
            totalCost = temp;
        }
        else if (item.enchantmentLevel < level && !item.locked) {
            if (EnchantHelper.containsEnchantment(container.tableInventory.getStackInSlot(0).getTagCompound().getTagList("restrictions", 10), item.enchantment.effectId, item.enchantmentLevel) || EPlusConfigurationHandler.allowDisenUnowned) {
                totalCost += container.disenchantmentCost(item.enchantment, item.enchantmentLevel, level);
            }
            else {
                totalCost = 0;
            }
        }
    }

    private void handleChangedScreenSize (Map<Integer, Integer> enchantments) {

        if (dirty) {
            final ArrayList<GuiEnchantmentLabel> temp = convertMapToGuiItems(enchantments, 35 + guiOffset + guiLeft, 15 + guiTop);

            for (final GuiEnchantmentLabel item : enchantmentArray) {
                for (final GuiEnchantmentLabel tempItem : temp) {
                    if (item.enchantment == tempItem.enchantment) {
                        item.startingXPos = item.xPos = tempItem.xPos;
                        item.startingYPos = item.yPos = tempItem.yPos;
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
