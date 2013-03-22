package eplus.gui;

import eplus.inventory.ContainerEnchantTable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class GuiModTable extends GuiContainer
{
    public final EntityPlayer player;

    public GuiModTable(InventoryPlayer inventory, World world, int x, int y, int z)
    {
        super(new ContainerEnchantTable(inventory, world, x, y, z));
        player = inventory.player;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {

    }
}
