package eplus.common;

import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
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
public class EnchantingPlus {

    public static final int PACKET_ID_CONFIG = 0;
    public static final int PACKET_ID_ENCHANT = 1;
    public static final int PACKET_ID_DISENCHANT = 2;
    public static final int PACKET_ID_REPAIR = 3;
    public static final int PACKET_ID_BOOKSHELVE = 4;

    public static boolean useMod;
    public static boolean allowDisenchanting;
    public static boolean allowRepair;
    public static boolean allowTransfer;
    public static boolean strictEnchant;
    public static boolean allowUpdateCheck; // created by Slash
    public static final int maxBookShelves = 15; // created by Slash
    public static int textureIndex = 2; // created by Slash
    
    @SidedProxy(clientSide = "eplus.client.ClientProxy", serverSide = "eplus.common.CommonProxy")
    public static CommonProxy proxy;

    public static Block table;
    @Instance("eplus")
    public static EnchantingPlus instance;

    @PreInit
    public void preInit(FMLPreInitializationEvent var1)
    {
        Configuration config = new Configuration(var1.getSuggestedConfigurationFile());

        try {
            config.load();

            Property allowDisenchantingProp = config.get("general", "AllowDisenchanting", true);
            allowDisenchantingProp.comment = "set to true if you want to allow disenchantment of items";
            allowDisenchanting = allowDisenchantingProp.getBoolean(true);

            Property useModProp = config.get("general", "UseMod", true);
            useModProp.comment = "set to true if you want to use the mod enchantment interface instead of vanilla";
            useMod = useModProp.getBoolean(true);

            Property allowRepairProp = config.get("general", "AllowRepair", true);
            allowRepairProp.comment = "set to true if you want to allow repair of enchanted items";
            allowRepair = allowRepairProp.getBoolean(true);

            Property allowTransferProp = config.get("general", "AllowTransfer", true);
            allowTransferProp.comment = "set to true if you want to allow transfer of enchantments between items";
            allowTransfer = allowTransferProp.getBoolean(true);

            Property strictEnchantProp = config.get("general", "StrictEnchanting", true);
            strictEnchantProp.comment = "set to false if you want to allow any item to recieve any enchantment. (warning may break the balance of the game)";
            strictEnchant = strictEnchantProp.getBoolean(true);

            Property allowUpdateCheckProp = config.get("general", "AllowUpdateCheck", true); // created by Slash
            allowUpdateCheckProp.comment = "set to true if you want to allow checking for updates"; // created by Slash
            allowUpdateCheck = allowUpdateCheckProp.getBoolean(true); // created by Slash

            Property textureIndexCheckProp = config.get("general", "TextureIndex", 0); // created by Slash
            textureIndexCheckProp.comment = "(0,1 or 2) index of texture to use"; // created by Slash
            textureIndex = textureIndexCheckProp.getInt(2); // created by Slash
            if (textureIndex < 0 | textureIndex > 2) textureIndex = 2;

        } catch (Exception e) {
            FMLLog.log(Level.SEVERE, e, "Enchanting Plus failed to load configurations.");
        } finally {
            config.save();
        }

        if (allowUpdateCheck) Version.versionCheck();  // modified by Slash
    }

    @Init
    public void init(FMLInitializationEvent var1)
    {
        NetworkRegistry.instance().registerGuiHandler(instance, proxy);
        proxy.init();
        int var2 = Block.enchantmentTable.blockID;
        Block.blocksList[var2] = null;
        Item.itemsList[var2] = null;
        table = new BlockEnchantingTable(var2).setHardness(5.0F).setResistance(2000.0F).setBlockName("enchantmentTable");
        GameRegistry.registerBlock(table, "enchantmentTable");
    }

    @ServerStopping
    public void onServerStopping(FMLServerStoppingEvent var1)
    {
        // this.useMod = true;
        // this.allowDisenchanting = true;
    }
    
    public static String getTranslatedTextureIndex() {
    	if (textureIndex == 0)
    		return "";
    	else
    		return String.valueOf(textureIndex);
    }
}
