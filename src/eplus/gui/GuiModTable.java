package eplus.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import eplus.EnchantingPlus;
import eplus.helper.EnchantHelper;
import eplus.helper.MathHelper;
import eplus.inventory.ContainerEnchantTable;
import eplus.inventory.TileEnchantTable;
import eplus.lib.ConfigurationSettings;
import eplus.lib.EnchantmentHelp;
import eplus.lib.FontFormat;
import eplus.lib.GuiIds;
import eplus.lib.Strings;
import eplus.network.packets.EnchantPacket;
import eplus.network.packets.GuiPacket;
import eplus.network.packets.RepairPacket;

/**
 * Enchanting Plus
 * 
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class GuiModTable extends GuiContainer
{
    class GuiIcon extends GuiButton
    {
        private boolean customTexture;
        private int textureIndex;

        public GuiIcon(int id, int x, int y, int width, int height, String caption)
        {
            super(id, x, y, width, height, caption);

        }

        public GuiIcon(int id, int x, int y, String caption)
        {
            this(id, x, y, 16, 16, caption);
        }

        /**
         * Determines if GuiIcon has a customTexture
         * 
         * @param texture
         *            index of the Texture
         * @return the Icon with according changes
         */
        public GuiIcon customTexture(int texture)
        {
            textureIndex = texture;
            customTexture = texture != 0;
            if (!customTexture)
            {
                width = 20;
                height = 20;
            }

            return this;
        }

        @Override
        public void drawButton(Minecraft mc, int x, int y)
        {
            if (!customTexture)
            {
                super.drawButton(mc, x, y);
            }
            else
            {
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                mc.renderEngine.bindTexture(texture);
                drawTexturedModalRect(xPosition, yPosition, 8 + textureIndex * 16, 182, width, height);
            }
        }

        public GuiIcon enabled(boolean enabled)
        {
            this.enabled = enabled;
            return this;
        }
    }

    /**
     * Class for the enchantments GUI representation
     */
    class GuiItem extends Gui
    {
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
            enchantment = Enchantment.enchantmentsList[id];
            enchantmentLevel = level;
            privateLevel = level;
            xPos = startingXPos = x;
            yPos = startingYPos = y;

            sliderX = xPos + 1;

            height = 18;
            width = 143;
        }

        /**
         * Draws the gui item
         */
        public void draw()
        {
            if (!show)
            {
                return;
            }

            final int indexX = dragging ? sliderX : enchantmentLevel <= enchantment.getMaxLevel() ? (int) (xPos + 1 + (width - 6)
                    * (enchantmentLevel / (double) enchantment.getMaxLevel())) : xPos + 1 + width - 6;

            drawRect(indexX, yPos + 1, indexX + 5, yPos - 1 + height, 0xff000000);
            fontRenderer.drawString(getTranslatedName(), xPos + 5, yPos + height / 4, 0x55aaff00);
            if (disabled)
            {
                drawRect(xPos, yPos + 1, xPos + width, yPos - 1 + height, 0x44aaffff);
            }
            else if (locked)
            {
                drawRect(xPos, yPos + 1, xPos + width, yPos - 1 + height, 0x44ff0000);
            }
            else
            {
                drawRect(xPos, yPos + 1, xPos + width, yPos - 1 + height, 0x44aa55ff);
            }
        }

        public String getTranslatedName()
        {
            String name = enchantment.getTranslatedName(enchantmentLevel);

            if (enchantmentLevel == 0)
            {
                if (name.lastIndexOf(" ") == -1)
                {
                    name = enchantment.getName();
                }
                else
                {
                    name = name.substring(0, name.lastIndexOf(" "));
                }
            }

            return name;
        }

        /**
         * Handles the GuiItem being clicked
         * 
         * @param mouseButton
         *            which mouse button clicked the item (0 - Left, 1 - Right)
         */
        public void handleClick(int mouseButton)
        {
        }

        /**
         * Scrolls the item
         * 
         * @param xPos
         *            the xPost of the mouse to scroll to
         */
        public void scroll(int xPos)
        {
            
            int start = guiOffset + guiLeft + 10;
            
            if (disabled)
            {
                return;
            }
            sliderX = start + xPos;
            
            
            if(sliderX <= start)
            {
                sliderX = start;
            }
            
            if(sliderX >= start + width + 20) {
                sliderX = start + width + 20;
            }

            index = xPos / (float) (width + 10);
            final int tempLevel = (int) Math.floor(privateLevel > enchantment.getMaxLevel() ? privateLevel * index : enchantment.getMaxLevel() * index);
            if (locked)
            {
                if (tempLevel < enchantmentLevel && (!container.tableInventory.getStackInSlot(0).isItemDamaged() || ConfigurationSettings.AllowEnchantDamaged))
                {
                    enchantmentLevel = tempLevel;
                    locked = false;
                }
            }
            else
            {
                if (tempLevel >= privateLevel || ConfigurationSettings.AllowDisenchanting && (!container.tableInventory.getStackInSlot(0).isItemDamaged())
                        || ConfigurationSettings.AllowEnchantDamaged)
                {
                    enchantmentLevel = tempLevel;
                }
            }

            if (enchantmentLevel <= 0)
            {
                enchantmentLevel = 0;
            }
        }

        /**
         * Changes the show property
         * 
         * @param b
         *            true to show | false to hide
         */
        public void show(boolean b)
        {
            show = b;
        }
    }

    private static boolean TMInagged = false;

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
    private int repairAmount = 0;

    private boolean dirty = false;

    private final String texture = "/assets/eplus/textures/gui/enchant.png";

    public String error = "";

    private static int guiOffset = 26;

    public GuiModTable(InventoryPlayer inventory, World world, int x, int y, int z, TileEnchantTable tileEntity)
    {
        super(new ContainerEnchantTable(inventory, world, x, y, z, tileEntity));
        player = inventory.player;

        container = (ContainerEnchantTable) inventorySlots;

        xPos = x;
        yPos = y;
        zPos = z;

        xSize = 235;
        ySize = 182;
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        final HashMap<Integer, Integer> enchants = new HashMap<Integer, Integer>();
        final HashMap<Integer, Integer> levels = new HashMap<Integer, Integer>();

        for (final GuiItem item : enchantmentArray)
        {
            final Integer id = enchantments.get(item.enchantment.effectId);

            if (item.enchantmentLevel != id && !item.disabled)
            {
                enchants.put(item.enchantment.effectId, item.enchantmentLevel);
                levels.put(item.enchantment.effectId, item.privateLevel);
            }
        }

        switch (par1GuiButton.id)
        {
        case 0:
            if (enchants.size() > 0)
            {
                PacketDispatcher.sendPacketToServer(new EnchantPacket(enchants, levels, totalCost).makePacket());
            }
            return;
        case 1:
            if (enchants.size() == 0 && ConfigurationSettings.AllowRepair)
            {
                PacketDispatcher.sendPacketToServer(new RepairPacket(totalCost, repairAmount).makePacket());
            }
            return;
        case 2:
            PacketDispatcher.sendPacketToServer(new GuiPacket(player.username, GuiIds.VanillaTable, xPos, yPos, zPos).makePacket());
        }
    }

    /**
     * Converts map to arraylist of gui items
     * 
     * @param map
     *            the map of enchantments to convert
     * @param x
     *            starting x position
     * @param y
     *            starting y position
     * @return the arraylist of gui items
     */
    private ArrayList<GuiItem> convertMapToGuiItems(Map<Integer, Integer> map, int x, int y)
    {
        final ArrayList<GuiItem> temp = new ArrayList<GuiItem>();

        if (map == null)
        {
            return temp;
        }

        int i = 0;
        int yPos = y;
        for (final Object obj : map.keySet())
        {

            final Integer enchantmentId = (Integer) obj;
            final Integer enchantmentLevel = map.get(obj);

            temp.add(new GuiItem(enchantmentId, enchantmentLevel, x, yPos));

            i++;
            yPos = y + i * 18;
        }

        return temp;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        for (final GuiItem item : enchantmentArray)
        {
            if (item.yPos < guiTop + 15 || item.yPos >= guiTop + 87)
            {
                item.show(false);
            }
            else
            {
                item.show(true);
            }
            item.draw();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);

        final int adjustedMouseX = par1 - guiLeft;
        final int adjustedMouseY = par2 - guiTop;

        mc.renderEngine.bindTexture(texture);

        int tempY = adjustedMouseY - 16;
        if (tempY <= 0)
        {
            tempY = 0;
        }
        if (tempY >= 57)
        {
            tempY = 57;
        }
        sliderIndex = sliding ? MathHelper.round(tempY / 57D * enchantingPages, .25) : sliderIndex;

        if (sliderIndex >= enchantingPages)
        {
            sliderIndex = enchantingPages;
        }

        sliderY = sliding ? tempY : 57 * (sliderIndex / enchantingPages);
        
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        drawTexturedModalRect(guiLeft + guiOffset + 180, guiTop + 16 + (int) sliderY, 0, 182, 12, 15);

        if (!clicked && Mouse.isButtonDown(0))
        {
            for (final GuiItem item : enchantmentArray)
            {
                if (getItemFromPos(par1, par2) == item && !item.disabled && !item.locked)
                {
                    item.dragging = true;
                }
            }
            if (adjustedMouseX <= 191 + guiOffset && adjustedMouseX >= 180 + guiOffset)
            {
                if (enchantingPages != 0)
                {
                    sliding = true;
                }
            }
        }

        for (final GuiItem item : enchantmentArray)
        {
            if (item.dragging && getItemFromPos(par1, par2) != item)
            {
                item.dragging = false;
            }
        }

        if (!Mouse.isButtonDown(0))
        {
            for (final GuiItem item : enchantmentArray)
            {
                if (getItemFromPos(par1, par2) == item)
                {
                    item.dragging = false;
                }
            }
            if (adjustedMouseX <= 191 + guiOffset && adjustedMouseX >= 180 + guiOffset)
            {
                sliding = false;
            }
        }

        clicked = Mouse.isButtonDown(0);

        for (final GuiItem item : enchantmentArray)
        {
            if (item.dragging)
            {
                item.scroll(adjustedMouseX - 36);
            }
        }
        if (EnchantingPlus.Debug)
        {
            fontRenderer.drawString(String.format("%s: %s", "Error", error), 5, 5, 0xffaabbaa);
        }
        final int maxWidth = guiLeft - 20;
        final List<List<String>> information = new ArrayList<List<String>>();
        information.add(fontRenderer.listFormattedStringToWidth(String.format("%s: %s", Strings.playerLevel, player.experienceLevel), maxWidth));

        if (container.tableInventory.getStackInSlot(0) == null || levelChanged() || !levelChanged() && !container.tableInventory.getStackInSlot(0).isItemDamaged())
        {
            information.add(fontRenderer.listFormattedStringToWidth(String.format("%s: %s", Strings.enchantingCost, totalCost), maxWidth));
        }
        else if (ConfigurationSettings.AllowRepair && !levelChanged() && container.tableInventory.getStackInSlot(0).isItemDamaged())
        {
            information.add(fontRenderer.listFormattedStringToWidth(String.format("%s: %s", Strings.repairCost, totalCost), maxWidth));
        }
        information.add(fontRenderer.listFormattedStringToWidth(String.format("%s: %s", Strings.maxEnchantLevel, container.bookCases()), maxWidth));

        for (final List<String> display : information)
        {
            int height = information.indexOf(display) == 0 ? guiTop + fontRenderer.FONT_HEIGHT + 8 : guiTop + (fontRenderer.FONT_HEIGHT + 8) * (information.indexOf(display) + 1);
            if (information.indexOf(display) > 0)
            {
                for (int i = information.indexOf(display) - 1; i >= 0; i--)
                {
                    height += (fontRenderer.FONT_HEIGHT + 3) * (information.get(i).size() - 1);
                }
            }

            try
            {
                drawHoveringText(display, guiLeft - 20 - maxWidth, height, fontRenderer);
            } catch (NoSuchMethodError e)
            {
                final StringBuilder sb = new StringBuilder();

                for (final String text : display)
                {
                    sb.append(text);
                    sb.append(" ");
                }
                drawCreativeTabHoveringText(sb.toString(), guiLeft - 20 - maxWidth, height);
                if (!TMInagged)
                {
                    EnchantingPlus.log.severe("Please update or remove NEI / TMI. It is causing issues.");
                    TMInagged = true;
                }
            }
        }

        if (isShiftKeyDown() && getItemFromPos(par1, par2) != null)
        {
            final String name = FontFormat.BOLD + getItemFromPos(par1, par2).getTranslatedName();
            String info = EnchantmentHelp.getToolTip(getItemFromPos(par1, par2).enchantment);

            if (info.isEmpty())
            {
                info = FontFormat.DARKRED + String.format("%s ", Strings.errorToolTip) + getItemFromPos(par1, par2).enchantment.getName();
            }

            info = FontFormat.PURPLE + info;

            final List<String> display = new ArrayList<String>();

            display.add(name);
            display.addAll(fontRenderer.listFormattedStringToWidth(info, 150));
            try
            {
                drawHoveringText(display, par1, par2, fontRenderer);
            } catch (NoSuchMethodError e)
            {
                final StringBuilder sb = new StringBuilder();

                for (final String text : display)
                {
                    sb.append(text);
                    sb.append(" ");
                }
                drawCreativeTabHoveringText(sb.toString(), guiLeft - 20 - maxWidth, height);
                if (!TMInagged)
                {
                    EnchantingPlus.log.severe("Please update or remove NEI / TMI. It is causing issues.");
                    TMInagged = true;
                }
            }
        }

        if (!error.isEmpty())
        {
            drawCreativeTabHoveringText(error, (xSize + guiLeft) / 2 - fontRenderer.getStringWidth(error) / 4, guiTop - fontRenderer.FONT_HEIGHT);
        }
    }

    /**
     * Gets a GuiItem from mouse position
     * 
     * @param x
     *            mouse x position
     * @param y
     *            mouse y position
     * @return the GuiItem found
     */
    private GuiItem getItemFromPos(int x, int y)
    {
        if (x < guiLeft + guiOffset + 35 || x > guiLeft + xSize - 32)
        {
            return null;
        }

        for (final GuiItem item : enchantmentArray)
        {
            if (!item.show)
            {
                continue;
            }
            if (y >= item.yPos && y <= item.yPos + item.height)
            {
                return item;
            }
        }
        return null;
    }

    @Override
    public void handleMouseInput()
    {
        super.handleMouseInput();

        final int eventDWheel = Mouse.getEventDWheel();
        final int mouseX = Mouse.getEventX() * width / mc.displayWidth - guiLeft;
        final int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1 - guiTop;

        if (eventDWheel != 0)
        {
            if (mouseX >= 35 + guiOffset && mouseX <= xSize + guiOffset - 32 || mouseX >= 180 + guiOffset && mouseX <= 192 + guiOffset)
            {
                if (mouseY >= 15 && mouseY <= 87)
                {
                    if (eventDWheel < 0)
                    {
                        sliderIndex += .25;
                        if (sliderIndex >= enchantingPages)
                        {
                            sliderIndex = enchantingPages;
                        }
                    }
                    else
                    {
                        sliderIndex -= .25;
                        if (sliderIndex <= 0)
                        {
                            sliderIndex = 0;
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();
        buttonList.add(new GuiIcon(0, guiLeft + guiOffset + 9, guiTop + 38, "E").customTexture(0));
        buttonList.add(new GuiIcon(1, guiLeft + guiOffset + 9, guiTop + 63, "R").customTexture(0));
        final String s = "Vanilla";
        buttonList.add(new GuiButton(2, guiLeft + xSize + 10, guiTop + 5, fontRenderer.getStringWidth(s) + 10, 20, s));

        dirty = true;
    }

    protected boolean levelChanged()
    {
        for (final GuiItem item : enchantmentArray)
        {
            if (item.enchantmentLevel != item.privateLevel)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void mouseClicked(int x, int y, int par3)
    {
        super.mouseClicked(x, y, par3);

        final GuiItem itemFromPos = getItemFromPos(x, y);

        if (itemFromPos != null)
        {
            for (final GuiItem item : enchantmentArray)
            {
                if (item == itemFromPos)
                {
                    itemFromPos.handleClick(par3);
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

        final Map<Integer, Integer> enchantments = container.getEnchantments();

        if (this.enchantments != enchantments)
        {
            this.enchantments = enchantments;

            enchantmentArray = convertMapToGuiItems(enchantments, 35 + guiOffset + guiLeft, 15 + guiTop);

            sliderIndex = enchantingPages = 0;
            clicked = sliding = false;
            error = "";
        }

        if (dirty)
        {
            final ArrayList<GuiItem> temp = convertMapToGuiItems(enchantments, 35 + guiOffset + guiLeft, 15 + guiTop);

            for (final GuiItem item : enchantmentArray)
            {
                for (final GuiItem tempItem : temp)
                {
                    if (item.enchantment == tempItem.enchantment)
                    {
                        item.startingXPos = item.xPos = tempItem.xPos;
                        item.startingYPos = item.yPos = tempItem.yPos;
                    }
                }
            }
            dirty = false;
        }

        final boolean enabled[] = new boolean[enchantmentArray.size()];

        for (int i = 0; i < enabled.length; i++)
        {
            enabled[i] = false;
        }

        for (int i = 0; i < enchantmentArray.size(); i++)
        {
            final GuiItem item = enchantmentArray.get(i);
            if (item.enchantmentLevel == 0)
            {
                continue;
            }
            for (int i1 = 0; i1 < enchantmentArray.size(); i1++)
            {
                final GuiItem item2 = enchantmentArray.get(i1);
                if (item == item2)
                {
                    continue;
                }
                enabled[i1] = enabled[i1] || i != i1 && (!item.enchantment.canApplyTogether(item2.enchantment) || !item2.enchantment.canApplyTogether(item.enchantment));
            }
        }
        for (int i = 0; i < enchantmentArray.size(); i++)
        {
            final GuiItem item = enchantmentArray.get(i);
            item.disabled = enabled[i];
        }

        enchantingPages = enchantmentArray.size() / 4.0 > 1 ? enchantmentArray.size() / 4.0 - 1.0 : 0;
        totalCost = 0;
        repairAmount = 0;

        if (!enchantmentArray.isEmpty() && levelChanged())
        {
            for (final GuiItem item : enchantmentArray)
            {
                item.yPos = item.startingYPos - (int) (18 * 4 * sliderIndex);

                final Integer level = enchantments.get(item.enchantment.effectId);
                if (item.enchantmentLevel > level && !item.disabled)
                {
                    int temp = totalCost + container.enchantmentCost(item.enchantment.effectId, item.enchantmentLevel, level);
                    try
                    {
                        if (container.canPurchase(player, temp))
                        {
                            item.locked = false;
                        }
                    } catch (final Exception e)
                    {
                        item.locked = true;
                        error = e.getMessage();

                        while (item.locked && item.enchantmentLevel > 0)
                        {
                            item.dragging = false;
                            item.enchantmentLevel--;
                            temp = totalCost + container.enchantmentCost(item.enchantment.effectId, item.enchantmentLevel, level);
                            try
                            {
                                if (container.canPurchase(player, temp))
                                {
                                    item.locked = false;
                                }
                            } catch (final Exception ex)
                            {
                            }
                        }
                    }
                    totalCost = temp;
                }
                else if (item.enchantmentLevel < level && !item.disabled)
                {
                    if (EnchantHelper.containsKey(container.tableInventory.getStackInSlot(0).getTagCompound().getTagList("restrictions"), item.enchantment.effectId,
                            item.enchantmentLevel) || ConfigurationSettings.allowDisenUnowned)
                    {
                        totalCost += container.disenchantmentCost(item.enchantment.effectId, item.enchantmentLevel, level);
                    }
                    else
                    {
                        totalCost = 0;
                        
                        //item.enchantmentLevel++;
                        //error = "Can not disenchant level not placed by yourself via eplus";
                    }
                }
            }
        }
        else if (ConfigurationSettings.AllowRepair && !levelChanged())
        {
            totalCost += container.repairCostMax(player);
            int itemDamage = container.tableInventory.getStackInSlot(0) != null ? container.tableInventory.getStackInSlot(0).getItemDamage() : 0;
            int playerLevel = player.experienceLevel;

            repairAmount = (itemDamage > playerLevel) ? playerLevel : itemDamage;

            for (final GuiItem item : enchantmentArray)
            {
                item.yPos = item.startingYPos - (int) (18 * 4 * sliderIndex);
            }
        }
    }
}
