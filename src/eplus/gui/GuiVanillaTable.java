package eplus.gui;

import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class GuiVanillaTable extends GuiEnchantment
{
    public GuiVanillaTable(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5, String par6Str)
    {
        super(par1InventoryPlayer, par2World, par3, par4, par5, par6Str);
    }
}
