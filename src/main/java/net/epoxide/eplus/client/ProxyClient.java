package net.epoxide.eplus.client;

import net.minecraft.item.Item;

import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.ClientRegistry;

import net.epoxide.eplus.client.renderer.item.RenderItemEnchantedBook;
import net.epoxide.eplus.client.renderer.item.RenderItemEnchantedTome;
import net.epoxide.eplus.client.renderer.tileentity.ArcaneDisenchanterRender;
import net.epoxide.eplus.client.renderer.tileentity.EnchantmentBookRenderer;
import net.epoxide.eplus.client.renderer.tileentity.EnchantmentTableRender;
import net.epoxide.eplus.common.ProxyCommon;
import net.epoxide.eplus.handler.ContentHandler;
import net.epoxide.eplus.tileentity.TileEntityArcaneInscriber;
import net.epoxide.eplus.tileentity.TileEntityEnchantTable;
import net.epoxide.eplus.tileentity.TileEntityEnchantmentBook;

public class ProxyClient extends ProxyCommon {
    
    @Override
    public void registerRenderers () {
        
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnchantTable.class, new EnchantmentTableRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArcaneInscriber.class, new ArcaneDisenchanterRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnchantmentBook.class, new EnchantmentBookRenderer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ContentHandler.blockEnchantmentBook), new RenderItemEnchantedBook());
        MinecraftForgeClient.registerItemRenderer(ContentHandler.book, new RenderItemEnchantedTome());
    }
}
