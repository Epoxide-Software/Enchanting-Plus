package net.epoxide.eplus.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.epoxide.eplus.EnchantingPlus;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockEnchantmentBook extends ItemBlock {

    public ItemBlockEnchantmentBook(Block block) {
        
        super(block);
        this.setUnlocalizedName("eplus.enchantmentBook");
        this.setCreativeTab(EnchantingPlus.tabEplus);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List itemList) {
        
        itemList.add(new ItemStack(item));
    }
}