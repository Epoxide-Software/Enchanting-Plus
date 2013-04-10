package eplus;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
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
import eplus.network.packets.BasePacket;
import net.minecraft.tileentity.TileEntity;

import java.util.logging.Logger;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

@Mod(name = References.MODNAME, modid = References.MODID, certificateFingerprint = References.FINGERPRINT)
@NetworkMod(channels = {BasePacket.CHANNEL}, packetHandler = PacketHandler.class, connectionHandler = ConnectionHandler.class)
public class EnchantingPlus {

    @Mod.Instance(References.MODID)
    public static EnchantingPlus INSTANCE;

    public static Logger log = Logger.getLogger(References.MODID);
    public static boolean Debug = false;

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
    }

    private void registerTileEntity(Class<? extends TileEntity> tileEntity)
    {
        GameRegistry.registerTileEntity(tileEntity, References.MODID + ":" + tileEntity.getSimpleName());
    }

    @Mod.PostInit
    public void postInit(FMLPostInitializationEvent event){}

    @Mod.ServerStarting
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new EplusCommands());
    }


    @Mod.FingerprintWarning
    public void invalidFingerprint(FMLFingerprintViolationEvent event)
    {
        log.severe(String.format("Received incorrect fingerprint Expected %s", event.expectedFingerprint));
    }
}
