package eplus;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import eplus.blocks.Blocks;
import eplus.commands.EplusCommands;
import eplus.handlers.ConfigurationHandler;
import eplus.inventory.TileEnchantTable;
import eplus.lib.References;
import eplus.network.ConnectionHandler;
import eplus.network.GuiHandler;
import eplus.network.PacketHandler;
import eplus.network.PlayerTracker;
import eplus.network.packets.BasePacket;
import eplus.network.proxies.CommonProxy;
import net.minecraft.tileentity.TileEntity;

import java.util.logging.Logger;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

@Mod(name = References.MODNAME, modid = References.MODID, version = "@VERSION@", dependencies = "required-after:Forge@[7.7.1.644,)", certificateFingerprint = References.FINGERPRINT)
@NetworkMod(channels = {BasePacket.CHANNEL}, versionBounds = "[1.14.5,)", packetHandler = PacketHandler.class, connectionHandler = ConnectionHandler.class, clientSideRequired = true)
public class EnchantingPlus {

    @Mod.Instance(References.MODID)
    public static EnchantingPlus INSTANCE;

    public static final Logger log = Logger.getLogger(References.MODID);
    public static final boolean Debug = Boolean.parseBoolean(System.getenv("DEBUG"));

    @SidedProxy(clientSide = "eplus.network.proxies.ClientProxy", serverSide = "eplus.network.proxies.CommonProxy")
    public static CommonProxy proxy;

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        log.setParent(FMLCommonHandler.instance().getFMLLogger());
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
    }

    @Mod.Init
    public void init(FMLInitializationEvent event)
    {
        Blocks.init();

        registerTileEntity(TileEnchantTable.class);
        NetworkRegistry.instance().registerGuiHandler(INSTANCE, new GuiHandler());
        GameRegistry.registerPlayerTracker(new PlayerTracker());
        proxy.registerTickHandlers();
    }

    private void registerTileEntity(Class<? extends TileEntity> tileEntity)
    {
        GameRegistry.registerTileEntity(tileEntity, References.MODID + ":" + tileEntity.getSimpleName());
    }

    @Mod.ServerStarting
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new EplusCommands());
    }


    @Mod.FingerprintWarning
    public void invalidFingerprint(FMLFingerprintViolationEvent event)
    {
        if (Debug) log.severe(event.fingerprints.toString());
        log.severe(String.format("Received incorrect fingerprint Expected %s", event.expectedFingerprint));
        if (!Debug)
            proxy.throwFingerprintError(References.MODNAME + " Received an incorrect fingerprint.\nThis means someone has tampered with the jar.\nPlease downloaded from official links.");
    }
}
