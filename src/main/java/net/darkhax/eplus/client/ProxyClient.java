package net.darkhax.eplus.client;

import net.darkhax.eplus.client.renderer.TileEntityAdvancedTableRenderer;
import net.darkhax.eplus.client.renderer.TileEntityDecorationRenderer;
import net.darkhax.eplus.common.ProxyCommon;
import net.darkhax.eplus.handler.ContentHandler;
import net.darkhax.eplus.item.ItemBook;
import net.darkhax.eplus.tileentity.TileEntityAdvancedTable;
import net.darkhax.eplus.tileentity.TileEntityDecoration;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ProxyClient extends ProxyCommon {
    
    @Override
    public void onInit () {
    
    }
    
    @Override
    public void onPostInit () {
    
    }
    
    @Override
    public void onPreInit () {
        
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ContentHandler.blockAdvancedTable), 0, new ModelResourceLocation("eplus:advanced_table", "inventory"));
        
        ModelLoader.setCustomModelResourceLocation(ContentHandler.itemTableUpgrade, 0, new ModelResourceLocation("eplus:table_upgrade", "inventory"));
        
        for (int meta = 0; meta < ItemBook.TYPES.length; meta++)
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ContentHandler.blockDecoration), meta, new ModelResourceLocation("eplus:book_" + ItemBook.getName(meta), "inventory"));
            
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvancedTable.class, new TileEntityAdvancedTableRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDecoration.class, new TileEntityDecorationRenderer());
    }
}
