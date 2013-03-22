package eplus;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import eplus.blocks.Blocks;
import eplus.handlers.ConfigurationHandler;
import eplus.lib.References;
import eplus.network.GuiHandler;
import eplus.network.PacketHandler;
import eplus.network.PlayerTracker;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

@Mod(name = References.MODNAME, modid = References.MODID)
@NetworkMod(channels = {References.MODID}, packetHandler = PacketHandler.class)
public class EnchantingPlus
{

    @Mod.Instance(References.MODID)
    public static EnchantingPlus INSTANCE;

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
    }

    @Mod.Init
    public void init(FMLInitializationEvent event)
    {
        Blocks.init();
        GameRegistry.registerPlayerTracker(new PlayerTracker());
        NetworkRegistry.instance().registerGuiHandler(INSTANCE, new GuiHandler());
    }

    @Mod.PostInit
    public void postInit(FMLPostInitializationEvent event)
    {

    }
}
