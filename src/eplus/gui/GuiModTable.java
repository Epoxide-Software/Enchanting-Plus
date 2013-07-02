package eplus.gui;

import cpw.mods.fml.common.network.PacketDispatcher;
import eplus.EnchantingPlus;
import eplus.helper.MathHelper;
import eplus.inventory.ContainerEnchantTable;
import eplus.inventory.TileEnchantTable;
import eplus.lib.*;
import eplus.network.packets.EnchantPacket;
import eplus.network.packets.GuiPacket;
import eplus.network.packets.RepairPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.ResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class GuiModTable extends GuiContainer {
    private final EntityPlayer player;
    private final ContainerEnchantTable container;
    private final int xPos;
    private final int yPos;
    private final int zPos;
    private ArrayList<GuiItem> enchantmentArray = new ArrayList<GuiItem>();
    private double sliderIndex = 0;
    private double enchantingPages = 0;
    private double sliderY = 0;
    private Map<Integer, Integer> enchantments;
    private boolean clicked = false;
    private boolean sliding = false;
    private int totalCost = 0;
    private boolean dirty = false;
    
    private ResourceLocation texture = new ResourceLocation("textures/gui/enchant.png");

    private String error = "";

    public GuiModTable(InventoryPlayer inventory, World world, int x, int y,
                       int z, TileEnchantTable tileEntity)
    {
        super(new ContainerEnchantTable(inventory, world, x, y, z, tileEntity));
        this.player = inventory.player;

        this.container = (ContainerEnchantTable) this.inventorySlots;

        this.xPos = x;
        this.yPos = y;
        this.zPos = z;

        xSize = 209;
        ySize = 182;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.add(new GuiIcon(0, guiLeft + 9, guiTop + 40, "E")
                .customTexture(0));
        this.buttonList.add(new GuiIcon(1, guiLeft + 9, guiTop + 60, "R")
                .customTexture(0));
        String s = "Vanilla";
        this.buttonList.add(new GuiButton(2, guiLeft + xSize + 10, guiTop + 5,
                fontRenderer.getStringWidth(s) + 10, 20, s));

        this.dirty = true;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {

        this.mc.func_110434_K().func_110577_a(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        for (GuiItem item : enchantmentArray) {
            if (item.yPos < guiTop + 15 || item.yPos >= guiTop + 87) {
                item.show(false);
            } else {
                item.show(true);
            }
            item.draw();
        }
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        HashMap<Integer, Integer> enchants = new HashMap<Integer, Integer>();

        for (GuiItem item : enchantmentArray) {
            Integer id = enchantments.get(item.enchantment.effectId);

            if (item.enchantmentLevel != id && !item.disabled) {
                enchants.put(item.enchantment.effectId, item.enchantmentLevel);
            }
        }

        switch (par1GuiButton.id) {
            case 0:
                if (enchants.size() > 0) {
                    PacketDispatcher.sendPacketToServer(new EnchantPacket(enchants,
                            totalCost).makePacket());
                }
                return;
            case 1:
                if (enchants.size() == 0 && ConfigurationSettings.AllowRepair) {
                    PacketDispatcher.sendPacketToServer(new RepairPacket(totalCost)
                            .makePacket());
                }
                return;
            case 2:
                PacketDispatcher.sendPacketToServer(new GuiPacket(player.username,
                        GuiIds.VanillaTable, xPos, yPos, zPos).makePacket());
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
    private ArrayList<GuiItem> convertMapToGuiItems(Map<Integer, Integer> map,
                                                    int x, int y)
    {
        ArrayList<GuiItem> temp = new ArrayList<GuiItem>();

        int i = 0;
        int yPos = y;
        for (Object obj : map.keySet()) {

            Integer enchantmentId = (Integer) obj;
            Integer enchantmentLevel = map.get(obj);

            temp.add(new GuiItem(enchantmentId, enchantmentLevel, x, yPos));

            i++;
            yPos = y + i * 18;
        }

        return temp;
    }

    @Override
    public void handleMouseInput()
    {
        super.handleMouseInput();

        int eventDWheel = Mouse.getEventDWheel();
        int mouseX = (Mouse.getEventX() * this.width / this.mc.displayWidth)
                - guiLeft;
        int mouseY = (this.height - Mouse.getEventY() * this.height
                / this.mc.displayHeight - 1)
                - guiTop;

        if (eventDWheel != 0) {
            if ((mouseX >= 35 && mouseX <= xSize - 32)
                    || (mouseX >= 180 && mouseX <= 192)) {
                if (mouseY >= 15 && mouseY <= 87) {
                    if (eventDWheel < 0) {
                        sliderIndex += .25;
                        if (sliderIndex >= enchantingPages)
                            sliderIndex = enchantingPages;
                    } else {
                        sliderIndex -= .25;
                        if (sliderIndex <= 0)
                            sliderIndex = 0;
                    }
                }
            }
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        // TODO fix http://mantis.aesireanempire.com/view.php?id=1
        // container.checkItems();

        Map<Integer, Integer> enchantments = container.getEnchantments();

        if (this.enchantments != enchantments) {
            this.enchantments = enchantments;

            enchantmentArray = convertMapToGuiItems(enchantments, 35 + guiLeft,
                    15 + guiTop);

            sliderIndex = enchantingPages = 0;
            clicked = sliding = false;
            error = "";
        }

        if (dirty) {
            ArrayList<GuiItem> temp = convertMapToGuiItems(enchantments,
                    35 + guiLeft, 15 + guiTop);

            for (GuiItem item : enchantmentArray) {
                for (GuiItem tempItem : temp) {
                    if (item.enchantment == tempItem.enchantment) {
                        item.startingXPos = item.xPos = tempItem.xPos;
                        item.startingYPos = item.yPos = tempItem.yPos;
                    }
                }
            }
            dirty = false;
        }

        boolean enabled[] = new boolean[enchantmentArray.size()];

        for (int i = 0; i < enabled.length; i++) {
            enabled[i] = false;
        }

        for (int i = 0; i < enchantmentArray.size(); i++) {
            GuiItem item = enchantmentArray.get(i);
            if (item.enchantmentLevel == 0)
                continue;
            for (int i1 = 0; i1 < enchantmentArray.size(); i1++) {
                GuiItem item2 = enchantmentArray.get(i1);
                if (item == item2)
                    continue;
                enabled[i1] = ((enabled[i1]) || (i != i1)
                        && (!item.enchantment
                        .canApplyTogether(item2.enchantment) || !item2.enchantment
                        .canApplyTogether(item.enchantment)));
            }
        }
        for (int i = 0; i < enchantmentArray.size(); i++) {
            GuiItem item = enchantmentArray.get(i);
            item.disabled = enabled[i];
        }

        this.enchantingPages = (enchantmentArray.size() / 4.0 > 1) ? enchantmentArray
                .size() / 4.0 - 1.0 : 0;
        this.totalCost = 0;

        if (!enchantmentArray.isEmpty() && levelChanged()) {
            for (GuiItem item : enchantmentArray) {
                item.yPos = item.startingYPos - (int) ((18 * 4) * sliderIndex);

                Integer level = enchantments.get(item.enchantment.effectId);
                if (item.enchantmentLevel > level && !item.disabled) {
                    int temp = totalCost
                            + container.enchantmentCost(
                            item.enchantment.effectId,
                            item.enchantmentLevel, level);
                    try {
                        if (container.canPurchase(player, temp))
                            item.locked = false;
                    } catch (Exception e) {
                        item.locked = true;
                        error = e.getMessage();

                        while (item.locked && item.enchantmentLevel > 0) {
                            item.dragging = false;
                            item.enchantmentLevel--;
                            temp = totalCost
                                    + container.enchantmentCost(
                                    item.enchantment.effectId,
                                    item.enchantmentLevel, level);
                            try {
                                if (container.canPurchase(player, temp))
                                    item.locked = false;
                            } catch (Exception ex) {
                            }
                        }
                    }
                    totalCost = temp;
                } else if (item.enchantmentLevel < level && !item.disabled) {
                    this.totalCost += container.disenchantmentCost(
                            item.enchantment.effectId, item.enchantmentLevel,
                            level);
                }
            }
        } else if (ConfigurationSettings.AllowRepair && !levelChanged()) {
            totalCost += container.repairCost();
            for (GuiItem item : enchantmentArray) {
                item.yPos = item.startingYPos - (int) ((18 * 4) * sliderIndex);
            }
        }
    }

    protected boolean levelChanged()
    {
        for (GuiItem item : enchantmentArray) {
            if (item.enchantmentLevel != item.privateLevel) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void mouseClicked(int x, int y, int par3)
    {
        super.mouseClicked(x, y, par3);

        GuiItem itemFromPos = getItemFromPos(x, y);

        if (itemFromPos != null) {
            for (GuiItem item : enchantmentArray) {
                if (item == itemFromPos) {
                    itemFromPos.handleClick(par3);
                }
            }
        }
    }

    /**
     * Gets a GuiItem from mouse position
     *
     * @param x mouse x position
     * @param y mouse y position
     * @return the GuiItem found
     */
    private GuiItem getItemFromPos(int x, int y)
    {
        if (x < guiLeft + 35 || x > guiLeft + xSize - 32)
            return null;

        for (GuiItem item : enchantmentArray) {
            if (!item.show)
                continue;
            if (y >= item.yPos && y <= item.yPos + item.height) {
                return item;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);

        int adjustedMouseX = par1 - guiLeft;
        int adjustedMouseY = par2 - guiTop;

        mc.func_110434_K().func_110577_a(texture);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        int tempY = adjustedMouseY - 16;
        if (tempY <= 0)
            tempY = 0;
        if (tempY >= 57)
            tempY = 57;
        sliderIndex = (sliding) ? MathHelper.round((tempY / 57D), .25)
                : sliderIndex;

        if (sliderIndex >= enchantingPages)
            sliderIndex = enchantingPages;

        sliderY = (sliding) ? tempY : 57 * (sliderIndex / enchantingPages);

        drawTexturedModalRect(guiLeft + 180, guiTop + 16 + (int) sliderY, 0,
                182, 12, 15);

        if (!clicked && Mouse.isButtonDown(0)) {
            for (GuiItem item : enchantmentArray) {
                if (getItemFromPos(par1, par2) == item && !item.disabled
                        && !item.locked) {
                    item.dragging = true;
                }
            }
            if (adjustedMouseX <= 191 && adjustedMouseX >= 180) {
                if (enchantingPages != 0)
                    this.sliding = true;
            }
        }

        for (GuiItem item : enchantmentArray) {
            if (item.dragging && getItemFromPos(par1, par2) != item) {
                item.dragging = false;
            }
        }

        if (!Mouse.isButtonDown(0)) {
            for (GuiItem item : enchantmentArray) {
                if (getItemFromPos(par1, par2) == item) {
                    item.dragging = false;
                }
            }
            if (adjustedMouseX <= 191 && adjustedMouseX >= 180) {
                this.sliding = false;
            }
        }

        clicked = Mouse.isButtonDown(0);

        for (GuiItem item : enchantmentArray) {
            if (item.dragging) {
                item.scroll(adjustedMouseX - 36);
            }
        }
        if (EnchantingPlus.Debug) {
            fontRenderer.drawString(String.format("%s: %s", "Error", error), 5,
                    5, 0xffaabbaa);
        }
        int maxWidth = guiLeft - 20;
        List<List<String>> information = new ArrayList<List<String>>();
        information.add(fontRenderer.listFormattedStringToWidth(String.format(
                "%s: %s", Strings.playerLevel, player.experienceLevel),
                maxWidth));

        if (container.tableInventory.getStackInSlot(0) == null
                || levelChanged() || !levelChanged()
                && !container.tableInventory.getStackInSlot(0).isItemDamaged()) {
            information.add(fontRenderer.listFormattedStringToWidth(
                    String.format("%s: %s", Strings.enchantingCost, totalCost),
                    maxWidth));
        } else if (ConfigurationSettings.AllowRepair && !levelChanged()
                && container.tableInventory.getStackInSlot(0).isItemDamaged()) {
            information.add(fontRenderer.listFormattedStringToWidth(
                    String.format("%s: %s", Strings.repairCost, totalCost),
                    maxWidth));
        }
        information.add(fontRenderer.listFormattedStringToWidth(
                String.format("%s: %s", Strings.maxEnchantLevel,
                        container.bookCases()), maxWidth));

        for (List<String> display : information) {
            int height = (information.indexOf(display) == 0) ? guiTop
                    + (fontRenderer.FONT_HEIGHT + 8) : guiTop
                    + (fontRenderer.FONT_HEIGHT + 8)
                    * (information.indexOf(display) + 1);
            if (information.indexOf(display) > 0) {
                for (int i = information.indexOf(display) - 1; i >= 0; i--) {
                    height += (fontRenderer.FONT_HEIGHT + 3)
                            * (information.get(i).size() - 1);
                }
            }

            try {
                drawHoveringText(display, guiLeft - 20 - maxWidth, height, fontRenderer);
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();

                for (String text : display) {
                    sb.append(text);
                    sb.append(" ");
                }
                drawCreativeTabHoveringText(sb.toString(), guiLeft - 20 - maxWidth, height);
                EnchantingPlus.log.severe("Please update or remove NEI / TMI. It is causing issues.");
            }
        }

        if (isShiftKeyDown() && getItemFromPos(par1, par2) != null) {
            String name = FontFormat.BOLD
                    + getItemFromPos(par1, par2).getTranslatedName();
            String info = EnchantmentHelp
                    .getToolTip(getItemFromPos(par1, par2).enchantment);

            if (info.isEmpty()) {
                info = FontFormat.DARKRED
                        + String.format("%s ", Strings.errorToolTip)
                        + getItemFromPos(par1, par2).enchantment.getName();
            }

            info = FontFormat.PURPLE + info;

            List<String> display = new ArrayList<String>();

            display.add(name);
            display.addAll(fontRenderer.listFormattedStringToWidth(info, 150));
            try {
                drawHoveringText(display, par1, par2, fontRenderer);
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();

                for (String text : display) {
                    sb.append(text);
                    sb.append(" ");
                }
                drawCreativeTabHoveringText(sb.toString(), guiLeft - 20 - maxWidth, height);
                EnchantingPlus.log.severe("Please update or remove NEI / TMI. It is causing issues.");
            }
        }

        if (!error.isEmpty()) {
            drawCreativeTabHoveringText(error, ((xSize + guiLeft) / 2)
                    - (fontRenderer.getStringWidth(error) / 4), guiTop
                    - fontRenderer.FONT_HEIGHT);
        }
    }

    /**
     * Class for the enchantments GUI representation
     */
    class GuiItem extends Gui {
        private final Enchantment enchantment;
        private final int height;
        private final int width;
        private final int privateLevel;
        public int startingXPos;
        public int startingYPos;
        public int yPos;
        public boolean locked = false;
        private int xPos;
        private int enchantmentLevel;
        private boolean show = true;
        private float index;
        private boolean dragging = false;
        private int sliderX;
        private boolean disabled;

        public GuiItem(int id, int level, int x, int y)
        {
            this.enchantment = Enchantment.enchantmentsList[id];
            this.enchantmentLevel = level;
            this.privateLevel = level;
            this.xPos = this.startingXPos = x;
            this.yPos = this.startingYPos = y;

            this.sliderX = xPos + 1;

            this.height = 18;
            this.width = 143;
        }

        public String getTranslatedName()
        {
            String name = enchantment.getTranslatedName(enchantmentLevel);

            if (enchantmentLevel == 0) {
                if (name.lastIndexOf(" ") == -1) {
                    name = enchantment.getName();
                } else {
                    name = name.substring(0, name.lastIndexOf(" "));
                }
            }

            return name;
        }

        /**
         * Draws the gui item
         */
        public void draw()
        {
            if (!show)
                return;

            int indexX = (dragging) ? sliderX
                    : (enchantmentLevel <= enchantment.getMaxLevel()) ? (int) (xPos + 1 + (width - 6)
                    * (enchantmentLevel / (double) enchantment
                    .getMaxLevel()))
                    : (xPos + 1 + (width - 6));

            drawRect(indexX, yPos + 1, indexX + 5, yPos - 1 + height,
                    0xff000000);
            fontRenderer.drawString(getTranslatedName(), xPos + 5, yPos
                    + height / 4, 0x55aaff00);
            if (disabled) {
                drawRect(xPos, yPos + 1, xPos + width, yPos - 1 + height,
                        0x44aaffff);
            } else if (locked) {
                drawRect(xPos, yPos + 1, xPos + width, yPos - 1 + height,
                        0x44ff0000);
            } else {
                drawRect(xPos, yPos + 1, xPos + width, yPos - 1 + height,
                        0x44aa55ff);
            }
        }

        /**
         * Scrolls the item
         *
         * @param xPos the xPost of the mouse to scroll to
         */
        public void scroll(int xPos)
        {
            if (disabled)
                return;
            sliderX = xPos + guiLeft + 36;
            if (sliderX <= guiLeft + 36)
                sliderX = guiLeft + 36;
            if (sliderX >= guiLeft + 173)
                sliderX = guiLeft + 173;

            index = xPos / (float) (width - 12);
            int tempLevel = (int) Math.floor((privateLevel > enchantment
                    .getMaxLevel()) ? privateLevel * index : enchantment
                    .getMaxLevel() * index);
            if (locked) {
                if (tempLevel < enchantmentLevel) {
                    enchantmentLevel = tempLevel;
                    locked = false;
                }
            } else {
                if (tempLevel >= privateLevel
                        || ConfigurationSettings.AllowDisenchanting
                        && !container.tableInventory.getStackInSlot(0)
                        .isItemDamaged()) {
                    enchantmentLevel = tempLevel;
                }
            }

            if (enchantmentLevel <= 0)
                enchantmentLevel = 0;
        }

        /**
         * Changes the show property
         *
         * @param b true to show | false to hide
         */
        public void show(boolean b)
        {
            this.show = b;
        }

        /**
         * Handles the GuiItem being clicked
         *
         * @param mouseButton which mouse button clicked the item (0 - Left, 1 - Right)
         */
        public void handleClick(int mouseButton)
        {
        }
    }

    class GuiIcon extends GuiButton {
        private boolean customTexture;
        private int textureIndex;

        public GuiIcon(int id, int x, int y, String caption)
        {
            this(id, x, y, 16, 16, caption);
        }

        public GuiIcon(int id, int x, int y, int width, int height,
                       String caption)
        {
            super(id, x, y, width, height, caption);

        }

        @Override
        public void drawButton(Minecraft mc, int x, int y)
        {
            if (!customTexture) {
                super.drawButton(mc, x, y);
            } else {
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                mc.func_110434_K().func_110577_a(texture);
                drawTexturedModalRect(xPosition, yPosition,
                        8 + textureIndex * 16, 182, width, height);
            }
        }

        /**
         * Determines if GuiIcon has a customTexture
         *
         * @param texture index of the Texture
         * @return the Icon with according changes
         */
        public GuiIcon customTexture(int texture)
        {
            this.textureIndex = texture;
            this.customTexture = texture != 0;
            if (!customTexture) {
                this.width = 20;
                this.height = 20;
            }

            return this;
        }

        public GuiIcon enabled(boolean enabled)
        {
            this.enabled = enabled;
            return this;
        }
    }
}
