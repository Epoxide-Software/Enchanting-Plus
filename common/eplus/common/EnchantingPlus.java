package eplus.common;

import java.util.logging.Level;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "eplus", name = "Enchanting Plus", useMetadata = true, version = Version.VERSION)
@NetworkMod(channels = { "eplus" }, packetHandler = PacketHandler.class, connectionHandler = ConnectionHandler.class)
public class EnchantingPlus
{

    public static final int PACKET_ID_CONFIG = 0;
    public static final int PACKET_ID_ENCHANT = 1;
    public static final int PACKET_ID_DISENCHANT = 2;
    public static final int PACKET_ID_REPAIR = 3;
    public static final int PACKET_ID_BOOKSHELVE = 4;

    public static boolean useMod;
    public static boolean allowDisenchanting;
    public static boolean allowRepair;
    public static boolean allowTransfer;


    @SidedProxy(clientSide = "eplus.client.ClientProxy", serverSide = "eplus.common.CommonProxy")
    public static CommonProxy proxy;

    public static Block table;
    @Instance("eplus")
    public static EnchantingPlus instance;


    @PreInit
    public void preInit(FMLPreInitializationEvent var1) {
        Configuration config = new Configuration(var1.getSuggestedConfigurationFile());

        try {
            config.load();

            allowDisenchanting = config.get("general", "AllowDisenchanting", true).getBoolean(true);
            useMod = config.get("general", "UseMod", true).getBoolean(true);
            allowRepair = config.get("general", "AllowRepair", true).getBoolean(true);
            allowTransfer = config.get("general", "AllowTransfer",true).getBoolean(true);
        } catch (Exception e) {
            FMLLog.log(Level.SEVERE, e, "Enchanting Plus failed to load configurations.");
        } finally {
            config.save();
        }

        Version.versionCheck();
    }

    @Init
    public void init(FMLInitializationEvent var1) {
        NetworkRegistry.instance().registerGuiHandler(instance, proxy);
        proxy.init();
        int var2 = Block.enchantmentTable.blockID;
        Block.blocksList[var2] = null;
        Item.itemsList[var2] = null;
        table = new BlockEnchantingTable(var2).setHardness(5.0F).setResistance(2000.0F).setBlockName("enchantmentTable");
        GameRegistry.registerBlock(table);
    }

    @ServerStopping
    public void onServerStopping(FMLServerStoppingEvent var1) {
        // this.useMod = true;
        // this.allowDisenchanting = true;
    }
}
