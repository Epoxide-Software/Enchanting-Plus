package net.epoxide.eplus;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.epoxide.eplus.common.ProxyCommon;
import net.epoxide.eplus.common.network.GuiHandler;
import net.epoxide.eplus.handler.ContentHandler;
import net.epoxide.eplus.handler.CreativeEPlus;
import net.epoxide.eplus.handler.EPlusConfigurationHandler;
import net.epoxide.eplus.handler.IMCHandler;
import net.epoxide.eplus.lib.Constants;
import net.minecraft.creativetab.CreativeTabs;

@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.MOD_VERSION, guiFactory = Constants.FACTORY, dependencies = "required-after:bookshelf@[1.0.2.56,)")
public class EnchantingPlus {
    
    @SidedProxy(clientSide = Constants.CLIENT_PROXY_CLASS, serverSide = Constants.SERVER_PROXY_CLASS)
    public static ProxyCommon proxy;
    
    @Mod.Instance(Constants.MOD_ID)
    public static EnchantingPlus instance;

    public static CreativeTabs tabEplus = new CreativeEPlus();

    /**
     * A SimpleNetworkWrapper that is used to send EnchantingPlus packets.
     */
    public SimpleNetworkWrapper network;
    
    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {
        
        network = NetworkRegistry.INSTANCE.newSimpleChannel("EnchantingPlus");
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        
        new EPlusConfigurationHandler(event.getSuggestedConfigurationFile());
        
        ContentHandler.initBlocks();
        ContentHandler.initItems();
    }
    
    @EventHandler
    public void processIMC (IMCEvent event) {
        
        for (IMCMessage message : event.getMessages())
            IMCHandler.handleMessage(message);
    }
    
    /**
     * Prints a debug message using the Enchanting Plus logger. If debug messages are disabled
     * in the configuration file, no message will be printed.
     * 
     * @param message: The message to print using the Enchanting Plus logger.
     */
    public static void printDebugMessage (String message) {
        
        // TODO replace to a config value.
        boolean allowDebug = true;
        
        if (allowDebug)
            Constants.LOG.info(message);
    }
}