package net.epoxide.eplus.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;

import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInterModComms;

import net.darkhax.bookshelf.client.gui.GuiNotification;

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
    
    public static GuiNotification notificationHandler;
    
    @Override
    public void registerRenderers () {
        
        notificationHandler = new GuiNotification(Minecraft.getMinecraft());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnchantTable.class, new EnchantmentTableRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArcaneInscriber.class, new ArcaneDisenchanterRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnchantmentBook.class, new EnchantmentBookRenderer());
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ContentHandler.blockEnchantmentBook), new RenderItemEnchantedBook());
        MinecraftForgeClient.registerItemRenderer(ContentHandler.book, new RenderItemEnchantedTome());
        FMLCommonHandler.instance().bus().register(new RenderingHandler());
        FMLInterModComms.sendMessage("llibrary", "update-checker", "https://github.com/Epoxide-MC/Enchanting-Plus/master/versions.json");
    }
}
