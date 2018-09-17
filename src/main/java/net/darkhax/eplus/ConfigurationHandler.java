package net.darkhax.eplus;

import java.io.File;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import net.minecraftforge.common.config.Configuration;

public final class ConfigurationHandler {

    public static Configuration config;

    public static float costFactor = 1.5f;
    public static float treasureFactor = 4f;
    public static float curseFactor = 3f;

    public static float floatingBookBonus = 1f;
    public static String[] blacklistedItems = new String[] {};
    public static String[] blacklistedEnchantments = new String[] {};

    public static void initConfig (File configFile) {

        config = new Configuration(configFile);

        costFactor = config.getFloat("costFactor", CATEGORY_GENERAL, 1f, 0f, 1024f, "A number used when calculated enchantment cost. This number is treated as a % based factor. 0.30 = 30% of the original cost. 1.5 = 150% of the original cost.");
        treasureFactor = config.getFloat("treasureFactor", CATEGORY_GENERAL, 4f, 0f, 1024f, "A factor used to make treasure enchantments like mending cost more to apply. By default they cost 4X more.");
        curseFactor = config.getFloat("curseFactor", CATEGORY_GENERAL, 3f, 0f, 1024f, "A factor used to make curse enchantments like vanishing cost more to apply. By default they cost 3X more.");
        floatingBookBonus = config.getFloat("floatingBookPower", CATEGORY_GENERAL, 1F, 0F, 1024F, "The amount of enchantment power a floating book should give. Bookshelfs have 1 power.");
        
        blacklistedItems = config.getStringList("blacklistedItems", "blacklist", new String[] {}, "A list of blacklisted items and blocks. Things in this list won't be enchantable at the eplus table. The format is the same as minecraft's id system. For example, minecraft:chainmail_helmet will prevent chainmail helmets from becoming enchanted.");
        blacklistedEnchantments = config.getStringList("blacklistedEnchantments", "blacklist", new String[] {}, "A list of blacklisted enchantment ids. Each entry should be an integer.");

        if (config.hasChanged()) {
            config.save();
        }
    }
}