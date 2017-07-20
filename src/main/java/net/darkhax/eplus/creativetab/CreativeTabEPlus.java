package net.darkhax.eplus.creativetab;

import net.darkhax.eplus.handler.ContentHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabEPlus extends CreativeTabs {

    public CreativeTabEPlus () {

        super("eplus");
    }

    @Override
    public ItemStack getTabIconItem () {

        return new ItemStack(ContentHandler.itemTableUpgrade);
    }
}
