package eplus.gui;

import eplus.inventory.ContainerEnchantTable;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
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

    class GuiItem extends Gui {
        private final Enchantment enchantment;
        private final int enchantmentLevel;

        private final int xPos;
        public int yPos;
        private final int height;
        private final int width;
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

    public GuiModTable(InventoryPlayer inventory, World world, int x, int y, int z) {
        super(new ContainerEnchantTable(inventory, world, x, y, z));
        this.player = inventory.player;

        this.container = (ContainerEnchantTable) this.inventorySlots;

        xSize = 209;
        ySize = 238;
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
}
