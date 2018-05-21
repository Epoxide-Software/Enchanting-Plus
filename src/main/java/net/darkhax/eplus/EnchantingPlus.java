package net.darkhax.eplus;

import java.util.function.Predicate;

import net.darkhax.bookshelf.lib.LoggingHelper;
import net.darkhax.bookshelf.network.NetworkHandler;
import net.darkhax.eplus.handler.ConfigurationHandler;
import net.darkhax.eplus.handler.IMCHandler;
import net.darkhax.eplus.libs.Content;
import net.darkhax.eplus.network.GuiHandler;
import net.darkhax.eplus.network.messages.MessageEnchant;
import net.darkhax.eplus.network.messages.MessageSliderUpdate;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "eplus", name = "Enchanting Plus", version = "@VERSION@", dependencies = "required-after:bookshelf", certificateFingerprint = "@FINGERPRINT@")
public final class EnchantingPlus {

    public static NetworkHandler NETWORK = new NetworkHandler("eplus");
    public static final LoggingHelper LOG = new LoggingHelper("Enchanting Plus");

    public static final Predicate<ItemStack> TEST_ENCHANTABILITY = (stack) -> stack.isItemEnchantable() || stack.isItemEnchanted() || stack.getItem() == Items.BOOK || stack.getItem() == Items.ENCHANTED_BOOK;

    @Instance("eplus")
    public static EnchantingPlus instance;

    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        ConfigurationHandler.initConfig(event.getSuggestedConfigurationFile());

        NETWORK.register(MessageEnchant.class, Side.SERVER);
        NETWORK.register(MessageSliderUpdate.class, Side.SERVER);

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        Content.registerBlocks();
        Content.registerItems();
    }

    @EventHandler
    public void init (FMLInitializationEvent event) {

    }

    @EventHandler
    public void processIMC (IMCEvent event) {

        for (final IMCMessage message : event.getMessages()) {
            IMCHandler.handleMessage(message);
        }
    }
}