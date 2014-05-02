package com.aesireanempire.eplus;

import com.aesireanempire.eplus.blocks.Blocks;
import com.aesireanempire.eplus.commands.EplusCommands;
import com.aesireanempire.eplus.handlers.ConfigurationHandler;
import com.aesireanempire.eplus.handlers.LanguageHandler;
import com.aesireanempire.eplus.handlers.PluginHandler;
import com.aesireanempire.eplus.handlers.Version;
import com.aesireanempire.eplus.inventory.TileEnchantTable;
import com.aesireanempire.eplus.items.Items;
import com.aesireanempire.eplus.lib.EnchantmentHelp;
import com.aesireanempire.eplus.lib.References;
import com.aesireanempire.eplus.network.GuiHandler;
import com.aesireanempire.eplus.network.packets.ChannelHandler;
import com.aesireanempire.eplus.network.packets.IPacket;
import com.aesireanempire.eplus.network.proxies.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Enchanting Plus
 * 
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

@Mod(name = References.MODNAME, modid = References.MODID)

public class EnchantingPlus
{

    @Mod.Instance(References.MODID)
    public static EnchantingPlus INSTANCE;

    public static org.apache.logging.log4j.Logger log;
    public static final boolean Debug = Boolean.parseBoolean(System.getenv("DEBUG"));

    @SidedProxy(clientSide = "com.aesireanempire.eplus.network.proxies.ClientProxy", serverSide = "com.aesireanempire.eplus.network.proxies.CommonProxy")
    public static CommonProxy proxy;
    public static Map<Integer, String> itemMap = new HashMap<Integer, String>();

    private static EnumMap<Side, FMLEmbeddedChannel> channels;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {

        registerTileEntity(TileEnchantTable.class);
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiHandler());

        channels = NetworkRegistry.INSTANCE.newChannel(References.MODID, new ChannelHandler());

        proxy.registerTickHandlers();
    }

    public static void sendPacketToServer(IPacket packet)
    {
        channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeOutbound(packet);
    }

    public static void sendPacketToPlayer(IPacket packet, EntityPlayer player)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeOutbound(packet);
    }

    public static void sendPacketToAllClients(IPacket packet)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(Side.SERVER).writeOutbound(packet);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        final NBTTagList list = new NBTTagList();

        PluginHandler.initPlugins(event.getModState());
        ConfigurationHandler.loadEnchantments();
        proxy.registerEnchantments();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log = event.getModLog();
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
        Version.init(event.getVersionProperties());

        Version.check();
        event.getModMetadata().version = Version.getCurrentModVersion();
        
        PluginHandler.init(event.getAsmData().getAll(EplusPlugin.class.getCanonicalName()));
        PluginHandler.initPlugins(event.getModState());

        // Strings.buildDefaultXML("en_US");
        try
        {
            LanguageHandler.getInstance().addLanguages("/assets/eplus/lang/langs.txt");
        } catch (final NullPointerException e)
        {
            log.error(String.format("Can not load %s", "/assets/eplus/lang/langs.txt"));
        }
        LanguageHandler.getInstance().loadLangauges();

        Blocks.init();
        Items.init();
    }

    @Mod.EventHandler
    public void processIMC(FMLInterModComms.IMCEvent event)
    {

        for (final FMLInterModComms.IMCMessage imcMessage : event.getMessages())
        {

            if (imcMessage.key.equalsIgnoreCase("enchant-tooltip"))
            {
                if (imcMessage.isStringMessage())
                {
                    final String[] strings = imcMessage.getStringValue().split(":");
                    if (EnchantmentHelp.putToolTips(strings[0], strings[1]))
                    {
                        EnchantingPlus.log.info(String.format("Add custom enchantment tool-tip for %s. Request sent from %s", strings[0], imcMessage.getSender()));
                    }
                }
                else if (imcMessage.isNBTMessage())
                {
                    final NBTTagCompound nbtValue = imcMessage.getNBTValue();
                    final NBTTagList enchantments = nbtValue.getTagList("Enchantments", 10);

                    for (int i = 0; i < enchantments.tagCount(); i++)
                    {
                        final NBTTagCompound nbtBase = (NBTTagCompound) enchantments.getCompoundTagAt(i);
                        final String name = nbtBase.getString("Name");
                        final String description = nbtBase.getString("Description");
                        if (EnchantmentHelp.putToolTips(name, description))
                        {
                            EnchantingPlus.log.info(String.format("Add custom enchantment tool-tip for %s. Request sent from %s", name, imcMessage.getSender()));
                        }
                    }
                }
                else
                {
                    EnchantingPlus.log.warn(String.format("Invalid IMC Message from %s", imcMessage.getSender()));
                }
            }
            else if (imcMessage.key.equalsIgnoreCase("blacklist-enchantment"))
            {
                if (imcMessage.isStringMessage())
                {
                    final String string = imcMessage.getStringValue();
                    if (EnchantmentHelp.putBlackList(string))
                    {
                        EnchantingPlus.log.info(String.format("Add custom enchantment blacklist for %s. Request sent from %s", string, imcMessage.getSender()));
                    }
                }
                else if (imcMessage.isNBTMessage())
                {
                    final NBTTagCompound nbtValue = imcMessage.getNBTValue();
                    final NBTTagList enchantments = nbtValue.getTagList("Enchantments", 10);

                    for (int i = 0; i < enchantments.tagCount(); i++)
                    {
                        final NBTTagCompound nbtBase = (NBTTagCompound) enchantments.getCompoundTagAt(i);
                        final String name = nbtBase.getString("Name");
                        if (EnchantmentHelp.putBlackList(name))
                        {
                            EnchantingPlus.log.info(String.format("Add custom enchantment blacklist for %s. Request sent from %s", name, imcMessage.getSender()));
                        }
                    }
                }
                else
                {
                    EnchantingPlus.log.warn(String.format("Invalid IMC Message from %s", imcMessage.getSender()));
                }
            }
            else if (imcMessage.key.equalsIgnoreCase("blacklist-item"))
            {
                if (imcMessage.isStringMessage())
                {
                    final Integer itemId = Integer.valueOf(imcMessage.getStringValue());
                    if (EnchantmentHelp.putBlackListItem(itemId))
                    {
                        EnchantingPlus.log.info(String.format("Add custom item blacklist for item id %d. Request sent from %s", itemId, imcMessage.getSender()));
                    }
                }
                else if (imcMessage.isNBTMessage())
                {
                    final NBTTagCompound nbtValue = imcMessage.getNBTValue();
                    final NBTTagList enchantments = nbtValue.getTagList("items", 10);

                    for (int i = 0; i < enchantments.tagCount(); i++)
                    {
                        final NBTTagCompound nbtBase = (NBTTagCompound) enchantments.getCompoundTagAt(i);
                        final Integer itemId = Integer.valueOf(nbtBase.getString("itemId"));
                        if (EnchantmentHelp.putBlackListItem(itemId))
                        {
                            EnchantingPlus.log.info(String.format("Add custom item blacklist for item id %d. Request sent from %s", itemId, imcMessage.getSender()));
                        }
                    }
                }
                else
                {
                    EnchantingPlus.log.warn(String.format("Invalid IMC Message from %s", imcMessage.getSender()));
                }
            }
        }
    }

    private void registerTileEntity(Class<? extends TileEntity> tileEntity)
    {
        GameRegistry.registerTileEntity(tileEntity, References.MODID + ":" + tileEntity.getSimpleName());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new EplusCommands());
    }
}
