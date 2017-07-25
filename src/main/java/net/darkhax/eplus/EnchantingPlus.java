package net.darkhax.eplus;

import net.darkhax.bookshelf.network.NetworkHandler;
import net.darkhax.eplus.common.ProxyCommon;
import net.darkhax.eplus.common.network.GuiHandler;
import net.darkhax.eplus.common.network.packet.PacketEnchantItem;
import net.darkhax.eplus.common.network.packet.PacketRequestSync;
import net.darkhax.eplus.common.network.packet.PacketSyncEnchantUnlock;
import net.darkhax.eplus.common.network.packet.PacketSyncEnchantUnlocks;
import net.darkhax.eplus.creativetab.CreativeTabEPlus;
import net.darkhax.eplus.handler.ConfigurationHandler;
import net.darkhax.eplus.handler.ContentHandler;
import net.darkhax.eplus.handler.ForgeEventHandler;
import net.darkhax.eplus.handler.IMCHandler;
import net.darkhax.eplus.handler.PlayerHandler;
import net.darkhax.eplus.libs.Constants;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.VERSION_NUMBER, guiFactory = Constants.FACTORY, dependencies = Constants.DEPENDENCIES)
public final class EnchantingPlus {

    @SidedProxy(clientSide = Constants.CLIENT_PROXY_CLASS, serverSide = Constants.SERVER_PROXY_CLASS)
    public static ProxyCommon proxy;

    @Instance(Constants.MOD_ID)
    public static EnchantingPlus instance;

    public static CreativeTabs tabEplus = new CreativeTabEPlus();

    public static NetworkHandler network = new NetworkHandler(Constants.MOD_ID);

    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        network.register(PacketRequestSync.class, Side.SERVER);
        network.register(PacketSyncEnchantUnlock.class, Side.CLIENT);
        network.register(PacketSyncEnchantUnlocks.class, Side.CLIENT);
        network.register(PacketEnchantItem.class, Side.SERVER);

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

        ConfigurationHandler.initConfig(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());

        PlayerHandler.init();

        proxy.onPreInit();
    }

    @EventHandler
    public void init (FMLInitializationEvent event) {

        proxy.onInit();
    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent event) {

        ContentHandler.initBlacklist();
        proxy.onPostInit();
    }

    @EventHandler
    public void processIMC (IMCEvent event) {

        for (final IMCMessage message : event.getMessages()) {
            IMCHandler.handleMessage(message);
        }
    }

    /**
     * Prints a debug message using the Enchanting Plus logger. If debug messages are disabled
     * in the configuration file, no message will be printed.
     *
     * @param message The message to print using the Enchanting Plus logger.
     */
    public static void printDebugMessage (String message) {

        if (ConfigurationHandler.printDebug) {
            Constants.LOG.info(message);
        }
    }
}