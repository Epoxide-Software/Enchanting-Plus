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
import org.lwjgl.input.Mouse;

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

        if (keyup) {
            keyup = !keyup;
            if (enchantmentArray.isEmpty() || enchantmentArray.get(0).yPos >= 15 + guiTop) return;

            for (GuiItem item : enchantmentArray) {
                item.yPos += 18;
            }
        }

        for (GuiItem item : enchantmentArray) {
            if (item.yPos < guiTop + 15 || item.yPos >= guiTop + 87) {
                item.show(false);
            } else {
                item.show(true);
            }
            item.draw();
        }
        for (GuiItem item : disenchantmentArray) {
            if (item.yPos < guiTop + 90 || item.yPos >= guiTop + 140) {
                item.show(false);
            } else {
                item.show(true);
            }
            item.draw();
        }

        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;


        fontRenderer.drawString(String.format("Mouse x: %s, y: %s, wheel: %s", x, y, Mouse.getEventDWheel()), 10, 10, 0xffaaee);
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

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        HashMap<Integer, Integer> enchants = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> disechants = new HashMap<Integer, Integer>();


        for (GuiItem item : enchantmentArray) {
            Integer id = (Integer) enchantments.get(item.enchantment.effectId);

            if (item.enchantmentLevel > id) {
                enchants.put(item.enchantment.effectId, item.enchantmentLevel);
            }
        }

        for (GuiItem item : disenchantmentArray) {
            Integer id = (Integer) disenchantments.get(item.enchantment.effectId);

            if (item.enchantmentLevel < id) {
                disechants.put(item.enchantment.effectId, item.enchantmentLevel);
            }
        }

        switch (par1GuiButton.id) {
            case 0:
                if (enchants.size() > 0)
                    PacketDispatcher.sendPacketToServer(new EnchantPacket(enchants, 0).makePacket());
                return;
            case 1:
                if (disechants.size() > 0)
                    PacketDispatcher.sendPacketToServer(new DisenchantPacket(disechants, 0).makePacket());
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

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();

        int eventDWheel = Mouse.getEventDWheel();
        int mouseX = (Mouse.getEventX() * this.width / this.mc.displayWidth) - guiLeft;
        int mouseY = (this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1) - guiTop;


        if (eventDWheel != 0) {
            if (mouseX >= 35 && mouseX <=  xSize - 32) {
                if (mouseY >= 15 && mouseY <= 87) {
                    if (eventDWheel < 0) {
                        if (enchantmentArray.isEmpty() || enchantmentArray.get(enchantmentArray.size() - 1).yPos <= 87)
                            return;

                        for (GuiItem item : enchantmentArray) {
                            item.yPos -= 18;
                        }
                    } else {
                        if (enchantmentArray.get(0).yPos >= 15) return;

                        for (GuiItem item : enchantmentArray) {
                            item.yPos += 18;
                        }
                    }
                } else if (mouseY >= 90 && mouseY <= 140) {
                    if (eventDWheel < 0) {
                        if (disenchantmentArray.isEmpty() || disenchantmentArray.get(disenchantmentArray.size() - 1).yPos <= 140)
                            return;

                        for (GuiItem item : disenchantmentArray) {
                            item.yPos -= 18;
                        }
                    } else {
                        if (disenchantmentArray.get(0).yPos >= 90) return;

                        for (GuiItem item : disenchantmentArray) {
                            item.yPos += 18;
                        }
                    }
                }
            }
        }


    }

    @Override
    protected void mouseClicked(int x, int y, int par3) {
        super.mouseClicked(x, y, par3);

        GuiItem itemFromPos = getItemFromPos(x, y);

        if (itemFromPos != null) {
            for (GuiItem item : disenchantmentArray) {
                if (item == itemFromPos) {
                    itemFromPos.handleClick(par3, item.enchantmentLevel < (Integer) disenchantments.get(item.enchantment.effectId));
                }
            }
            for (GuiItem item : enchantmentArray) {
                if (item == itemFromPos) {
                    itemFromPos.handleClick(par3, true);
                }
            }
        }
    }

    private GuiItem getItemFromPos(int x, int y) {
        if (x < guiLeft + 35 || x > guiLeft + xSize - 32) return null;

        for (GuiItem item : disenchantmentArray) {
            if (!item.show) continue;
            if (y >= item.yPos && y <= item.yPos + item.height) {
                return item;
            }
        }

        for (GuiItem item : enchantmentArray) {
            if (!item.show) continue;
            if (y >= item.yPos && y <= item.yPos + item.height) {
                return item;
            }
        }


        return null;
    }

    class GuiItem extends Gui {
        private final Enchantment enchantment;
        private final int xPos;
        private final int height;
        private final int width;
        public int yPos;
        private int enchantmentLevel;
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

        public void handleClick(int par3, boolean canIncrement) {
            if (par3 == 0 && canIncrement) {
                enchantmentLevel++;
                if (enchantmentLevel > enchantment.getMaxLevel()) enchantmentLevel = enchantment.getMaxLevel();
            } else if (par3 == 1) {
                enchantmentLevel--;
                if (enchantmentLevel < 0) enchantmentLevel = 0;
            }
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
