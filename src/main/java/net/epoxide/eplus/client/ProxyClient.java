package net.epoxide.eplus.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.epoxide.eplus.client.renderer.tileentity.EnchantmentTableRender;
import net.epoxide.eplus.common.ProxyCommon;
import net.epoxide.eplus.tileentity.TileEntityEnchantTable;

public class ProxyClient extends ProxyCommon {

    @Override
    public void registerRenderers () {

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnchantTable.class, new EnchantmentTableRender());
    }
}
