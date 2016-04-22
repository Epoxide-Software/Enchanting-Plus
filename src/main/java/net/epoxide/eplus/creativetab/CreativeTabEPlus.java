package net.epoxide.eplus.creativetab;

import net.epoxide.eplus.handler.ContentHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabEPlus extends CreativeTabs {
    
    public CreativeTabEPlus() {
        
        super("eplus");
    }
    
    @Override
    public Item getTabIconItem () {
        
        return Item.getItemFromBlock(ContentHandler.blockEnchantmentBook);
    }
}
