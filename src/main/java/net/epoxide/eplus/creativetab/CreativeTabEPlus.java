package net.epoxide.eplus.creativetab;

import net.minecraft.item.Item;

import net.darkhax.bookshelf.creativetab.CreativeTabCached;

import net.epoxide.eplus.handler.ContentHandler;

public class CreativeTabEPlus extends CreativeTabCached {
    
    public CreativeTabEPlus() {
        
        super("eplus");
    }
    
    @Override
    public Item getTabIconItem () {
        
        return Item.getItemFromBlock(ContentHandler.blockEnchantmentBook);
    }
}
