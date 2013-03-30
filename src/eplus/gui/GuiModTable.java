package eplus.gui;

import cpw.mods.fml.common.network.PacketDispatcher;
import eplus.inventory.ContainerEnchantTable;
import eplus.network.packets.DisenchantPacket;
import eplus.network.packets.EnchantPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.EnumOptions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;
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
    private ArrayList<GuiItem> enchantmentArray = new ArrayList<GuiItem>();
    private ArrayList<GuiItem> disenchantmentArray = new ArrayList<GuiItem>();
    private Map enchantments;
    private Map disenchantments;
    private boolean keydown = false;
    private boolean keyup = false;
    private boolean keyperiod = false;

    public GuiModTable(InventoryPlayer inventory, World world, int x, int y, int z) {
        super(new ContainerEnchantTable(inventory, world, x, y, z));
        this.player = inventory.player;

        this.container = (ContainerEnchantTable) this.inventorySlots;

        xSize = 209;
        ySize = 238;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiIcon(0, guiLeft + 9, guiTop + 77, 20, 20, "E"));
        this.buttonList.add(new GuiIcon(1, guiLeft + 9, guiTop + 98, 20, 20, "D"));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

        mc.renderEngine.bindTexture("/mods/eplus/gui/enchant.png");
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        Map enchantments = container.getEnchantments();
        Map disenchantments = container.getDisenchantments();

        if (this.enchantments != enchantments || this.disenchantments != disenchantments) {
            this.enchantments = enchantments;
            this.disenchantments = disenchantments;

            enchantmentArray = new ArrayList<GuiItem>();
            disenchantmentArray = new ArrayList<GuiItem>();

            convertMapToGuiItems(enchantments, enchantmentArray, 35 + guiLeft, 15 + guiTop);
            convertMapToGuiItems(disenchantments, disenchantmentArray, 35 + guiLeft, 90 + guiTop);
        }

        if (keydown) {
            keydown = !keydown;

            if (enchantmentArray.isEmpty() || enchantmentArray.get(enchantmentArray.size() - 1).yPos <= 15 + guiTop) return;


            for (GuiItem item : enchantmentArray) {
                item.yPos -= 18;
            }

        }

        if(keyperiod) {
            keyperiod = !keyperiod;

            HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

            map.put(Enchantment.efficiency.effectId, Enchantment.efficiency.getMaxLevel());

            PacketDispatcher.sendPacketToServer(new DisenchantPacket(map, 0).makePacket());
        }

        if (keyup) {
            keyup = !keyup;
            if (enchantmentArray.get(0).yPos >= 15 + guiTop) return;

            for (GuiItem item : enchantmentArray) {
                item.yPos += 18;
            }
        }

        for (GuiItem item : enchantmentArray) {
            if (item.yPos >= 15 + guiTop && item.yPos <= 69 + guiTop)
                item.draw();
        }
        for (GuiItem item : disenchantmentArray) {
            item.draw();
        }
    }

    @Override
    public void handleKeyboardInput() {
        super.handleKeyboardInput();

        if (Keyboard.getEventKeyState()) {
            int i = Keyboard.getEventKey();
            char c0 = Keyboard.getEventCharacter();

            if (i == Keyboard.KEY_DOWN) {
                keydown = !keydown;
            }

            if (i == Keyboard.KEY_UP) {
                keyup = !keyup;
            }

            if (i == Keyboard.KEY_PERIOD)
                keyperiod = !keyperiod;
        }
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {

        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(Enchantment.efficiency.effectId, Enchantment.efficiency.getMaxLevel());

        switch (par1GuiButton.id) {
            case 0:
                PacketDispatcher.sendPacketToServer(new EnchantPacket(map, 0).makePacket());
                return;
            case 1:
                PacketDispatcher.sendPacketToServer(new DisenchantPacket(map, 0).makePacket());
                return;
        }
    }

    private void convertMapToGuiItems(Map map, ArrayList<GuiItem> guiItems, int x, int y) {

        int i = 0;
        int yPos = y;
        for (Object obj : map.keySet()) {

            Integer enchantmentId = (Integer) obj;
            Integer enchantmentLevel = (Integer) map.get(obj);

            guiItems.add(new GuiItem(enchantmentId, enchantmentLevel, x, yPos));

            i++;
            yPos = y + i * 18;
        }
    }

    class GuiItem extends Gui {
        private final Enchantment enchantment;
        private final int enchantmentLevel;
        private final int xPos;
        private final int height;
        private final int width;
        public int yPos;
        private boolean show = true;

        public GuiItem(int id, int level, int x, int y) {
            this.enchantment = Enchantment.enchantmentsList[id];
            this.enchantmentLevel = level;
            this.xPos = x;
            this.yPos = y;

            this.height = 18;
            this.width = 144;

        }

        public void draw() {
            if (!show) return;

            String name = enchantment.getTranslatedName(enchantmentLevel);

            if (enchantmentLevel == 0) {
                if (name.lastIndexOf(" ") == -1) {
                    name = enchantment.getName();
                } else {
                    name = name.substring(0, name.lastIndexOf(" "));
                }
            }

            fontRenderer.drawString(name, xPos + 5, yPos + height / 4, 0x55aaff00);
            drawVerticalLine(xPos, yPos, xPos + width, 0xffffff);

            drawRect(xPos + 1, yPos + 1, xPos - 1 + width, yPos - 1 + height, 0x44aa55ff);
        }

        public void show(boolean b) {
            this.show = b;
        }
    }

    class GuiSlide extends GuiSlider {

        public GuiSlide(int par1, int par2, int par3, EnumOptions par4EnumOptions, String par5Str, float par6) {
            super(par1, par2, par3, par4EnumOptions, par5Str, par6);
        }
    }

    class GuiIcon extends GuiButton {

        public GuiIcon(int par1, int par2, int par3, int par4, int par5, String par6Str) {
            super(par1, par2, par3, par4, par5, par6Str);
        }

        @Override
        public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
            super.drawButton(par1Minecraft, par2, par3);
        }
    }
}
