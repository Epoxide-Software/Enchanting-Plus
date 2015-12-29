package net.epoxide.eplus;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

import net.darkhax.bookshelf.creativetab.CreativeTabCached;
import net.darkhax.bookshelf.lib.util.Utilities;

import net.epoxide.eplus.common.ProxyCommon;
import net.epoxide.eplus.common.network.GuiHandler;
import net.epoxide.eplus.common.network.PacketArcaneInscriber;
import net.epoxide.eplus.common.network.PacketArcaneInscriberEffects;
import net.epoxide.eplus.common.network.PacketEnchant;
import net.epoxide.eplus.common.network.PacketGui;
import net.epoxide.eplus.common.network.PacketRepair;
import net.epoxide.eplus.common.network.PacketSyncPlayerProperties;
import net.epoxide.eplus.creativetab.CreativeTabEPlus;
import net.epoxide.eplus.handler.ConfigurationHandler;
import net.epoxide.eplus.handler.ContentHandler;
import net.epoxide.eplus.handler.ForgeEventHandler;
import net.epoxide.eplus.handler.IMCHandler;
import net.epoxide.eplus.lib.Constants;

@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.MOD_VERSION, guiFactory = Constants.FACTORY, dependencies = Constants.DEPENDENCIES)
public final class EnchantingPlus {
    
    @SidedProxy(clientSide = Constants.CLIENT_PROXY_CLASS, serverSide = Constants.SERVER_PROXY_CLASS)
    public static ProxyCommon proxy;
    
    @Mod.Instance(Constants.MOD_ID)
    public static EnchantingPlus instance;
    
    /**
     * A CreativeTab used for all EnchantingPlus related items and blocks.
     */
    public static CreativeTabCached tabEplus = new CreativeTabEPlus();
    
    /**
     * A SimpleNetworkWrapper that is used to send EnchantingPlus packets.
     */
    public static SimpleNetworkWrapper network;
    
    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        
        network = NetworkRegistry.INSTANCE.newSimpleChannel("EnchantingPlus");
        Utilities.registerMessage(network, PacketSyncPlayerProperties.class, 0, Side.CLIENT);
        Utilities.registerMessage(network, PacketGui.class, 1, Side.SERVER);
        Utilities.registerMessage(network, PacketEnchant.class, 2, Side.SERVER);
        Utilities.registerMessage(network, PacketRepair.class, 3, Side.SERVER);
        Utilities.registerMessage(network, PacketArcaneInscriberEffects.class, 4, Side.CLIENT);
        Utilities.registerMessage(network, PacketArcaneInscriber.class, 6, Side.CLIENT);
        
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        
        ConfigurationHandler.initConfig(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
        FMLCommonHandler.instance().bus().register(new ForgeEventHandler());
        
        ContentHandler.initBlocks();
        ContentHandler.initItems();
        ContentHandler.initEnchantmentColors();
        ContentHandler.initModifiers();
        ContentHandler.initRecipes();
        ContentHandler.initAchievements();
        ContentHandler.initMisc();
        
        proxy.registerRenderers();
    }
    
    @EventHandler
    public void postInit (FMLPostInitializationEvent event) {
        
        ContentHandler.registerDungeonLoot();
    }
    
    @EventHandler
    public void processIMC (IMCEvent event) {
        
        for (IMCMessage message : event.getMessages())
            IMCHandler.handleMessage(message);
    }
    
    @EventHandler
    public void onServerStarting (FMLServerStartingEvent event) {
        
        // event.registerServerCommand(new CommandUnlockEnchantments());
    }
    
    /**
     * Prints a debug message using the Enchanting Plus logger. If debug messages are disabled
     * in the configuration file, no message will be printed.
     *
     * @param message: The message to print using the Enchanting Plus logger.
     */
    public static void printDebugMessage (String message) {
        
        if (ConfigurationHandler.printDebug)
            Constants.LOG.info(message);
    }
}