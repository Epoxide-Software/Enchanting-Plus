package net.darkhax.eplus.client;

import net.darkhax.eplus.common.ProxyCommon;
import net.darkhax.eplus.handler.ContentHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ProxyClient extends ProxyCommon {
    
    @Override
    public void onPreInit () {
        
        Item item;
        
        ModelLoader.setCustomModelResourceLocation(ContentHandler.itemTableUpgrade, 0, new ModelResourceLocation("eplus:table_upgrade", "inventory"));
    }
    
    @Override
    public void onInit () {
    
    }
    
    @Override
    public void onPostInit () {
    
    }
}
