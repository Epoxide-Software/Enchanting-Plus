package eplus.common;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import eplus.common.localization.LocalizationHandler;
import eplus.common.localization.LocalizationRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import java.lang.reflect.Field;
import java.util.logging.Level;

@Mod(modid = "eplus", name = "Enchanting Plus", useMetadata = true)
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
    public static boolean hasLight = true; // created by Slash
    public static boolean needBookShelves = true; // created by Slash
    public static boolean hasParticles = true; // created by Slash
    public static boolean allowPocketEnchanting;

    public static double enchantFactor;
    public static double disenchantFactor;
    public static double transferFactor;
    public static double repairFactor;

    public static int pocketId;
    
    @SidedProxy(clientSide = "eplus.client.ClientProxy", serverSide = "eplus.common.CommonProxy")
    public static CommonProxy proxy;

    @Instance("eplus")
    public static EnchantingPlus instance;

    public static Configuration config;

    @PreInit
    public void preInit(FMLPreInitializationEvent var1)
    {
        config = new Configuration(var1.getSuggestedConfigurationFile());

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

            Property textureIndexCheckProp = config.get("general", "TextureIndex", 2); // created by Slash
            textureIndexCheckProp.comment = "(0,1 or 2) index of texture to use"; // created by Slash
            textureIndex = textureIndexCheckProp.getInt(2); // created by Slash
            if (textureIndex < 0 | textureIndex > 2) textureIndex = 2;

            Property allowhasLightProp = config.get("general", "hasLight", true); // created by Slash
            allowhasLightProp.comment = "set to true if you want the Table shine like a torch"; // created by Slash
            hasLight = allowhasLightProp.getBoolean(true); // created by Slash
            
            Property allowneedBookShelvesProp = config.get("general", "needBookShelves", true); // created by Slash
            allowneedBookShelvesProp.comment = "set to true if you want the bookshelves around the table to be a must"; // created by Slash
            needBookShelves = allowneedBookShelvesProp.getBoolean(true); // created by Slash

            Property allowhasParticlesProp = config.get("general", "hasParticles", true); // created by Slash
            allowhasParticlesProp.comment = "set to true if you want the table emitting particles (just for fun)"; // created by Slash
            hasParticles = allowhasParticlesProp.getBoolean(true); // created by Slash

            Property enchantFactorProp = config.get("Factors", "EnchantFactor", 1.0d);
            enchantFactorProp.comment = "Change to set the factor at which the enchanting cost is multiplied by\nInput is from 0 - 10";
            clamp(enchantFactorProp.getDouble(1.0d), 0.0d, 10.0d, "enchantFactor");

            Property disenchantFactorProp = config.get("Factors", "DisenchantFactor", 1.0d);
            disenchantFactorProp.comment = "Change to set the factor at which the disenchanting cost is multiplied by\nInput is from 0 - 10";
            clamp(disenchantFactorProp.getDouble(1.0d), 0.0d, 10.0d, "disenchantFactor");

            Property transferFactorProp = config.get("Factors", "TransferFactor", 1.0d);
            transferFactorProp.comment = "Change to set the factor at which the transfer cost is multiplied by\nInput is from 0 - 10";
            clamp(transferFactorProp.getDouble(1.0d), 0.0d, 10.0d, "transferFactor");

            Property repairFactorProp = config.get("Factors", "RepairFactor", 1.0d);
            repairFactorProp.comment = "Change to set the factor at which the repair cost is multiplied by\nInput is from 0 - 10";
            clamp(repairFactorProp.getDouble(1.0d), 0.0d, 10.0d, "repairFactor");

            Property enablePocket = config.get("Items", "EnablePocketEnchanter", true);
            enablePocket.comment = "set to true if you want the pocket enchanter to be craftable.";
            allowPocketEnchanting = enablePocket.getBoolean(true);

            Property pocketIDProp = config.getItem("Items","PocketEnchanter", 152);
            pocketId = pocketIDProp.getInt();

        } catch (Exception e) {
            FMLLog.log(Level.SEVERE, e, "Enchanting Plus failed to load configurations.");
        } finally {
            if(config.hasChanged()){
                config.save();
            }
        }

        LocalizationRegistry.Instance().addLocalizationFile("/eplus/lang/pt_BR.xml");
        LocalizationRegistry.Instance().addLocalizationFile("/eplus/lang/en_US.xml");
        LocalizationHandler.addLanguages();

        Version.init(var1.getVersionProperties());
        if(allowUpdateCheck) Version.check();  // modified by Slash
        var1.getModMetadata().version = Version.getCurrentModVersion();

    }

    private void clamp(double value, double min, double max, String factorString) {
        Class<?> clazz = instance.getClass();

        try {
            Field field = clazz.getField(factorString);

            if(value < min ) {
                field.setDouble(field, min);
            }else if(value > max) {
                field.setDouble(field, max);
            }else {
                field.setDouble(field, value);
            }
        } catch (Exception ex) {
            Game.log(Level.INFO, "Failed to clamp {0}'s value {1}", new Object[]{ factorString, value });
        }
    }

    @Init
    public void init(FMLInitializationEvent var1)
    {
        NetworkRegistry.instance().registerGuiHandler(instance, proxy);
        proxy.init();
        int var2 = Block.enchantmentTable.blockID;
        Block.blocksList[var2] = null;
        Item.itemsList[var2] = null;
        Block table = new BlockEnchantingTable(var2).setHardness(5.0F).setResistance(2000.0F).setUnlocalizedName("enchantmentTable");
        GameRegistry.registerBlock(table, "enchantmentTable");

        if(allowPocketEnchanting) {
            Item pocketEnchanter = new ItemPocketEnchanter(pocketId).setUnlocalizedName("pocketEnchanter");
            GameRegistry.registerItem(pocketEnchanter, "pocketEnchanter");

            GameRegistry.addRecipe(new ItemStack(pocketEnchanter), "GEG"," B ", " B ", Character.valueOf('G'), Item.ghastTear, Character.valueOf('E'), Block.enchantmentTable, Character.valueOf('B'), Item.blazeRod);
        }
    }

    @ServerStarting
    public void onServerStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new eplusCommand());
    }
    
    public static String getTranslatedTextureIndex() {
    	if (textureIndex == 0)
    		return "";
    	else
    		return String.valueOf(textureIndex);
    }
}
