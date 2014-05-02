package com.aesireanempire.eplus.network;

import com.aesireanempire.eplus.EnchantingPlus;
import com.aesireanempire.eplus.gui.GuiModTable;
import com.aesireanempire.eplus.gui.GuiVanillaTable;
import com.aesireanempire.eplus.inventory.ContainerEnchantTable;
import com.aesireanempire.eplus.inventory.TileEnchantTable;
import com.aesireanempire.eplus.lib.GuiIds;
import cpw.mods.fml.common.network.IGuiHandler;
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
public class GuiHandler implements IGuiHandler
{
    static
    {
        EnchantingPlus.log.info("Initializing GUI Handler.");
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity;
        switch (ID)
        {
            case GuiIds.ModTable:
                tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity == null || !(tileEntity instanceof TileEnchantTable))
                {
                    return null;
                }
                return new GuiModTable(player.inventory, world, x, y, z, (TileEnchantTable) tileEntity);
            case GuiIds.VanillaTable:
                tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity == null || !(tileEntity instanceof TileEnchantTable))
                {
                    return null;
                }
                return new GuiVanillaTable(player.inventory, world, x, y, z, "");
            default:
                return null;
        }
    }

    @Override
    public Object getServerGuiElement(int ID, final EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity;
        switch (ID)
        {
            case GuiIds.ModTable:
                tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity == null || !(tileEntity instanceof TileEnchantTable))
                {
                    return null;
                }
                return new ContainerEnchantTable(player.inventory, world, x, y, z, (TileEnchantTable) tileEntity);
            case GuiIds.VanillaTable:
                tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity == null || !(tileEntity instanceof TileEnchantTable))
                {
                    return null;
                }
                return new ContainerEnchantment(player.inventory, world, x, y, z)
                {
                    @Override
                    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
                    {
                        return !player.isDead;
                    }
                };
            default:
                return null;
        }
    }
}
