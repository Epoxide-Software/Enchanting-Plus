package net.darkhax.eplus;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import java.io.File;

import net.darkhax.bookshelf.util.RegistryUtils;
import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.eplus.api.Blacklist;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

public final class ConfigurationHandler {

    public static Configuration config;

    public static float costFactor = 1.5f;
    public static float treasureFactor = 4f;
    public static float curseFactor = 3f;
    public static int baseCost = 45;

    public static float floatingBookBonus = 1f;
    private static String[] blacklistedItems = new String[] {};
    private static String[] blacklistedEnchantments = new String[] {};

    public static void initConfig (File configFile) {

        config = new Configuration(configFile);

        baseCost = config.getInt("baseCost", CATEGORY_GENERAL, 45, 1, 1024, "The base cost to use for the enchantment formula. ");
        costFactor = config.getFloat("costFactor", CATEGORY_GENERAL, 1f, 0f, 1024f, "A number used when calculated enchantment cost. This number is treated as a % based factor. 0.30 = 30% of the original cost. 1.5 = 150% of the original cost.");
        treasureFactor = config.getFloat("treasureFactor", CATEGORY_GENERAL, 4f, 0f, 1024f, "A factor used to make treasure enchantments like mending cost more to apply. By default they cost 4X more.");
        curseFactor = config.getFloat("curseFactor", CATEGORY_GENERAL, 3f, 0f, 1024f, "A factor used to make curse enchantments like vanishing cost more to apply. By default they cost 3X more.");
        floatingBookBonus = config.getFloat("floatingBookPower", CATEGORY_GENERAL, 1F, 0F, 1024F, "The amount of enchantment power a floating book should give. Bookshelfs have 1 power.");

        blacklistedItems = config.getStringList("blacklistedItems", "blacklist", new String[] {}, "A blacklist of items that can't be enchanted with this mod. Format is itemid#meta");
        blacklistedEnchantments = config.getStringList("blacklistedEnchantments", "blacklist", new String[] {}, "A blacklist of enchantments that are not available in E+. Format is just enchantmentid.");

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void buildBlacklist () {

        for (String origString : blacklistedItems) {
            
            String itemString = origString;
            if (!itemString.contains("#")) {
                
                # Default items to all metas
                itemString = itemString + "#" + OreDictionary.WILDCARD_VALUE;
            }

            final ItemStack stack = StackUtils.createStackFromString(itemString);

            if (stack != null && !stack.isEmpty()) {

                Blacklist.blacklist(stack);
            }

            else {

                EnchantingPlus.LOG.error("Tried to blacklist item {0} but it does not exist.", itemString);
            }
        }

        for (final String enchString : blacklistedEnchantments) {

            final Enchantment ench = RegistryUtils.getEnchantment(enchString);

            if (ench != null) {

                Blacklist.blacklist(ench);
            }

            else {

                EnchantingPlus.LOG.error("Tried to blacklist enchantment {} but it does not exist.", enchString);
            }
        }
    }
}
