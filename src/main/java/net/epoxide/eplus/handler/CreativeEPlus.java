package net.epoxide.eplus.handler;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeEPlus extends CreativeTabs {

    public CreativeEPlus () {

        super("eplus");
    }

    @Override
    public Item getTabIconItem () {

        return Item.getItemFromBlock(ContentHandler.eplusTable);
    }
}
