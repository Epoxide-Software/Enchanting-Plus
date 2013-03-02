package eplus.common;

import eplus.common.EnchantingPlus;
import eplus.common.localization.LocalizationHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

/**
 * User: Stengel
 * Date: 2/24/13
 * Time: 2:06 PM
 */
public class ItemPocketEnchanter extends Item {

    public ItemPocketEnchanter(int par1) {
        super(par1);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return CreativeTabs.tabDecorations;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        par3List.add(LocalizationHelper.getLocalString("pocketEnchanter.info"));
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
        if (world.isRemote) {
            return true;
        } else {
            if (EnchantingPlus.useMod) {
                player.openGui(EnchantingPlus.instance, 1, world, x, y, z);
            }
            return true;
        }
    }
}
