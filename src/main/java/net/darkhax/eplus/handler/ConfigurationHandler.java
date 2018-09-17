package net.darkhax.eplus.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public final class ConfigurationHandler {

    public static Configuration config;

    public static float costFactor = 1.5f;
    public static float treasureFactor = 4f;
    public static float curseFactor = 3f;
    
    public static String[] blacklistedItems = new String[] {};
    public static String[] blacklistedEnchantments = new String[] {};

    public static void initConfig (File configFile) {

        config = new Configuration(configFile);

        final String settings = "settings";
        costFactor = config.getFloat("costFactor", settings, 1f, 0f, 1024f, "A number used when calculated enchantment cost. This number is treated as a % based factor. 0.30 = 30% of the original cost. 1.5 = 150% of the original cost.");
        treasureFactor = config.getFloat("treasureFactor", settings, 4f, 0f, 1024f, "A factor used to make treasure enchantments like mending cost more to apply. By default they cost 4X more.");
        curseFactor = config.getFloat("curseFactor", settings, 3f, 0f, 1024f, "A factor used to make curse enchantments like vanishing cost more to apply. By default they cost 3X more.");
        
        blacklistedItems = config.getStringList("blacklistedItems", settings, new String[] {}, "A list of blacklisted items and blocks. Things in this list won't be enchantable at the eplus table. The format is the same as minecraft's id system. For example, minecraft:chainmail_helmet will prevent chainmail helmets from becoming enchanted.");
        blacklistedEnchantments = config.getStringList("blacklistedEnchantments", settings, new String[] {}, "A list of blacklisted enchantment ids. Each entry should be an integer.");

        if (config.hasChanged()) {
            config.save();
        }
    }
}