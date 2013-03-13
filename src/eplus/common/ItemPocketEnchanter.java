package eplus.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import eplus.common.localization.LocalizationHelper;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

/**
 * User: Stengel
 * Date: 2/24/13
 * Time: 2:06 PM
 */
@SuppressWarnings("ALL")
public class ItemPocketEnchanter extends Item {

    public ItemPocketEnchanter(int par1) {
        super(par1);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return EnchantingPlus.eplusTab;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        par3List.add(LocalizationHelper.getLocalString("pocketEnchanter.info"));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (EnchantingPlus.useMod) {
            player.swingItem();
            player.openGui(EnchantingPlus.instance, 1, world, (int)player.posX, (int)player.posY, (int)player.posZ);
        }
        return itemStack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void func_94581_a(IconRegister par1IconRegister) {
        this.iconIndex = par1IconRegister.func_94245_a("eplus:pocketenchanter");
    }

    @SideOnly(Side.CLIENT) // created by Slash
    @Override
    public boolean hasEffect(ItemStack par1ItemStack)
    {
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return EnumRarity.epic;
    }
}
