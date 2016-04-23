package net.darkhax.eplus.client;

import net.darkhax.eplus.client.renderer.TileEntityAdvancedTableRenderer;
import net.darkhax.eplus.common.ProxyCommon;
import net.darkhax.eplus.handler.ContentHandler;
import net.darkhax.eplus.tileentity.TileEntityAdvancedTable;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ProxyClient extends ProxyCommon {
    
    @Override
    public void onPreInit () {
        
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ContentHandler.blockAdvancedTable), 0, new ModelResourceLocation("eplus:advanced_table", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ContentHandler.itemTableUpgrade, 0, new ModelResourceLocation("eplus:table_upgrade", "inventory"));
        
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvancedTable.class, new TileEntityAdvancedTableRenderer());
    }
    
    @Override
    public void onInit () {
    
    }
    
    @Override
    public void onPostInit () {
    
    }
}
