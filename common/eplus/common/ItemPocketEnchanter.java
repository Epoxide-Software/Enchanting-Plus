package eplus.common;

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
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (EnchantingPlus.useMod) {
            player.openGui(EnchantingPlus.instance, 1, world, 0, 0, 0);
        }
        return itemStack;
    }
}
