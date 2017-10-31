package net.darkhax.eplus;

import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.network.NetworkHandler;
import net.darkhax.eplus.common.network.GuiHandler;
import net.darkhax.eplus.common.network.packet.PacketEnchantItem;
import net.darkhax.eplus.common.network.packet.PacketRequestSync;
import net.darkhax.eplus.common.network.packet.PacketSyncEnchantUnlock;
import net.darkhax.eplus.common.network.packet.PacketSyncEnchantUnlocks;
import net.darkhax.eplus.handler.ConfigurationHandler;
import net.darkhax.eplus.handler.ContentHandler;
import net.darkhax.eplus.handler.IMCHandler;
import net.darkhax.eplus.handler.PlayerHandler;
import net.darkhax.eplus.libs.Content;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "eplus", name = "Enchanting Plus", version = "@VERSION@", dependencies = "required-after:bookshelf", certificateFingerprint = "@FINGERPRINT@")
public final class EnchantingPlus {

    public static NetworkHandler NETWORK = new NetworkHandler("eplus");
    public static final LoggingHelper LOG = new LoggingHelper("Enchanting Plus");

    @Instance("eplus")
    public static EnchantingPlus instance;

    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        ConfigurationHandler.initConfig(event.getSuggestedConfigurationFile());

        NETWORK.register(PacketRequestSync.class, Side.SERVER);
        NETWORK.register(PacketSyncEnchantUnlock.class, Side.CLIENT);
        NETWORK.register(PacketSyncEnchantUnlocks.class, Side.CLIENT);
        NETWORK.register(PacketEnchantItem.class, Side.SERVER);

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        PlayerHandler.init();

        Content.registerBlocks();
        Content.registerItems();
    }

    @EventHandler
    public void init (FMLInitializationEvent event) {

    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent event) {

        ContentHandler.initBlacklist();
    }

    @EventHandler
    public void processIMC (IMCEvent event) {

        for (final IMCMessage message : event.getMessages()) {
            IMCHandler.handleMessage(message);
        }
    }
}