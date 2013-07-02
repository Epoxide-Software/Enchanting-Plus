package eplus.network;

import cpw.mods.fml.common.network.IGuiHandler;
import eplus.EnchantingPlus;
import eplus.gui.GuiModTable;
import eplus.gui.GuiVanillaTable;
import eplus.inventory.ContainerEnchantTable;
import eplus.inventory.TileEnchantTable;
import eplus.lib.GuiIds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class GuiHandler implements IGuiHandler {
    static {
        EnchantingPlus.log.info("Initializing GUI Handler.");
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world,
                                      int x, int y, int z)
    {
        TileEntity tileEntity;
        switch (ID) {
            case GuiIds.ModTable:
                tileEntity = world.getBlockTileEntity(x, y, z);
                if (tileEntity == null || !(tileEntity instanceof TileEnchantTable)) {
                    return null;
                }
                return new ContainerEnchantTable(player.inventory, world, x, y, z,
                        (TileEnchantTable) tileEntity);
            case GuiIds.VanillaTable:
                tileEntity = world.getBlockTileEntity(x, y, z);
                if (tileEntity == null || !(tileEntity instanceof TileEnchantTable)) {
                    return null;
                }
                return new ContainerEnchantment(player.inventory, world, x, y, z);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world,
                                      int x, int y, int z)
    {
        TileEntity tileEntity;
        switch (ID) {
            case GuiIds.ModTable:
                tileEntity = world.getBlockTileEntity(x, y, z);
                if (tileEntity == null || !(tileEntity instanceof TileEnchantTable)) {
                    return null;
                }
                return new GuiModTable(player.inventory, world, x, y, z,
                        (TileEnchantTable) tileEntity);
            case GuiIds.VanillaTable:
                tileEntity = world.getBlockTileEntity(x, y, z);
                if (tileEntity == null || !(tileEntity instanceof TileEnchantTable)) {
                    return null;
                }
                return new GuiVanillaTable(player.inventory, world, x, y, z, "");
            default:
                return null;
        }
    }
}
