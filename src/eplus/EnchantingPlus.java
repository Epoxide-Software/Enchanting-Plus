package eplus;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.ItemData;
import eplus.blocks.Blocks;
import eplus.commands.EplusCommands;
import eplus.handlers.ConfigurationHandler;
import eplus.handlers.LanguageHandler;
import eplus.handlers.PluginHandler;
import eplus.handlers.Version;
import eplus.inventory.TileEnchantTable;
import eplus.lib.EnchantmentHelp;
import eplus.lib.References;
import eplus.network.ConnectionHandler;
import eplus.network.GuiHandler;
import eplus.network.PacketHandler;
import eplus.network.PlayerTracker;
import eplus.network.packets.BasePacket;
import eplus.network.proxies.CommonProxy;
import eplus.plugins.EplusPlugin;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

@Mod(name = References.MODNAME, modid = References.MODID, dependencies = "required-after:Forge@[7.8.0.684,)")
@NetworkMod(channels = {BasePacket.CHANNEL}, packetHandler = PacketHandler.class, connectionHandler = ConnectionHandler.class, clientSideRequired = true)
public class EnchantingPlus {

    @Mod.Instance(References.MODID)
    public static EnchantingPlus INSTANCE;

    public static Logger log;
    public static final boolean Debug = Boolean.parseBoolean(System
            .getenv("DEBUG"));

    @SidedProxy(clientSide = "eplus.network.proxies.ClientProxy", serverSide = "eplus.network.proxies.CommonProxy")
    public static CommonProxy proxy;
    public static Map<Integer, String> itemMap = new HashMap<Integer, String>();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log = event.getModLog();
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
        Version.init(event.getVersionProperties());

        Version.check();
        event.getModMetadata().version = Version.getCurrentModVersion();

        NBTTagList list = new NBTTagList();
        GameData.writeItemData(list);
        for (int i = 0; i < list.tagCount(); i++) {
            ItemData itemData = new ItemData((NBTTagCompound) list.tagAt(i));
            this.itemMap.put(itemData.getItemId(), itemData.getModId());
        }

        PluginHandler.init(event.getAsmData().getAll(
                EplusPlugin.class.getCanonicalName()));
        PluginHandler.initPlugins(event.getModState());

        // Strings.buildDefaultXML("en_US");
        try {
            LanguageHandler.getInstance().addLanguages(
                    "/assets/eplus/lang/langs.txt");
        } catch (NullPointerException e) {
            log.severe(String.format("Can not load %s",
                    "/mods/eplus/lang/langs.txt"));
        }
        LanguageHandler.getInstance().loadLangauges();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        Blocks.init();

        registerTileEntity(TileEnchantTable.class);
        NetworkRegistry.instance().registerGuiHandler(INSTANCE,
                new GuiHandler());
        GameRegistry.registerPlayerTracker(new PlayerTracker());

        proxy.registerTickHandlers();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        EnchantmentHelp.init();
        PluginHandler.initPlugins(event.getModState());
    }

    private void registerTileEntity(Class<? extends TileEntity> tileEntity)
    {
        GameRegistry.registerTileEntity(tileEntity, References.MODID + ":"
                + tileEntity.getSimpleName());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new EplusCommands());
    }

    @Mod.EventHandler
    public void processIMC(FMLInterModComms.IMCEvent event)
    {

        for (FMLInterModComms.IMCMessage imcMessage : event.getMessages()) {

            if (imcMessage.key.equalsIgnoreCase("enchant-tooltip")) {
                if (imcMessage.isStringMessage()) {
                    String[] strings = imcMessage.getStringValue().split(":");
                    if (EnchantmentHelp.putToolTips(strings[0], strings[1])) {
                        EnchantingPlus.log
                                .info(String
                                        .format("Add custom enchantment tool-tip for %s. Request sent from %s",
                                                strings[0],
                                                imcMessage.getSender()));
                    }
                } else if (imcMessage.isNBTMessage()) {
                    NBTTagCompound nbtValue = imcMessage.getNBTValue();
                    NBTTagList enchantments = nbtValue
                            .getTagList("Enchantments");

                    for (int i = 0; i < enchantments.tagCount(); i++) {
                        NBTTagCompound nbtBase = (NBTTagCompound) enchantments
                                .tagAt(i);
                        String name = nbtBase.getString("Name");
                        String description = nbtBase.getString("Description");
                        if (EnchantmentHelp.putToolTips(name, description)) {
                            EnchantingPlus.log
                                    .info(String
                                            .format("Add custom enchantment tool-tip for %s. Request sent from %s",
                                                    name,
                                                    imcMessage.getSender()));
                        }
                    }
                } else {
                    EnchantingPlus.log.warning(String.format(
                            "Invalid IMC Message from %s",
                            imcMessage.getSender()));
                }
            } else if (imcMessage.key.equalsIgnoreCase("blacklist-enchantment")) {
                if (imcMessage.isStringMessage()) {
                    String string = imcMessage.getStringValue();
                    if (EnchantmentHelp.putBlackList(string)) {
                        EnchantingPlus.log
                                .info(String
                                        .format("Add custom enchantment blacklist for %s. Request sent from %s",
                                                string, imcMessage.getSender()));
                    }
                } else if (imcMessage.isNBTMessage()) {
                    NBTTagCompound nbtValue = imcMessage.getNBTValue();
                    NBTTagList enchantments = nbtValue
                            .getTagList("Enchantments");

                    for (int i = 0; i < enchantments.tagCount(); i++) {
                        NBTTagCompound nbtBase = (NBTTagCompound) enchantments
                                .tagAt(i);
                        String name = nbtBase.getString("Name");
                        if (EnchantmentHelp.putBlackList(name)) {
                            EnchantingPlus.log
                                    .info(String
                                            .format("Add custom enchantment blacklist for %s. Request sent from %s",
                                                    name,
                                                    imcMessage.getSender()));
                        }
                    }
                } else {
                    EnchantingPlus.log.warning(String.format(
                            "Invalid IMC Message from %s",
                            imcMessage.getSender()));
                }
            } else if (imcMessage.key.equalsIgnoreCase("blacklist-item")) {
                if (imcMessage.isStringMessage()) {
                    Integer itemId = Integer.valueOf(imcMessage
                            .getStringValue());
                    if (EnchantmentHelp.putBlackListItem(itemId)) {
                        EnchantingPlus.log
                                .info(String
                                        .format("Add custom item blacklist for item id %d. Request sent from %s",
                                                itemId, imcMessage.getSender()));
                    }
                } else if (imcMessage.isNBTMessage()) {
                    NBTTagCompound nbtValue = imcMessage.getNBTValue();
                    NBTTagList enchantments = nbtValue.getTagList("items");

                    for (int i = 0; i < enchantments.tagCount(); i++) {
                        NBTTagCompound nbtBase = (NBTTagCompound) enchantments
                                .tagAt(i);
                        Integer itemId = Integer.valueOf(nbtBase
                                .getString("itemId"));
                        if (EnchantmentHelp.putBlackListItem(itemId)) {
                            EnchantingPlus.log
                                    .info(String
                                            .format("Add custom item blacklist for item id %d. Request sent from %s",
                                                    itemId,
                                                    imcMessage.getSender()));
                        }
                    }
                } else {
                    EnchantingPlus.log.warning(String.format(
                            "Invalid IMC Message from %s",
                            imcMessage.getSender()));
                }
            }
        }
    }
}
