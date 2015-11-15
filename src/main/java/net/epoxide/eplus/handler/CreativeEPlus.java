package net.epoxide.eplus.handler;

import net.minecraft.item.Item;

import net.darkhax.bookshelf.creativetab.CreativeTabCached;

public class CreativeEPlus extends CreativeTabCached {
    
    public CreativeEPlus() {
        
        super("eplus");
    }
    
    @Override
    public Item getTabIconItem () {
        
        return Item.getItemFromBlock(ContentHandler.eplusTable);
    }
}
