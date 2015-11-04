package net.epoxide.eplus.handler;

import net.darkhax.bookshelf.creativetab.CreativeTabCached;
import net.minecraft.item.Item;

public class CreativeEPlus extends CreativeTabCached {
    
    public CreativeEPlus() {
        
        super("eplus");
    }
    
    @Override
    public Item getTabIconItem () {
        
        return Item.getItemFromBlock(ContentHandler.eplusTable);
    }
}
