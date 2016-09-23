package net.darkhax.eplus.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public final class ConfigurationHandler {
    
    public static Configuration config;
    
    public static boolean printDebug = true;
    public static boolean useQuestMode = true;
    public static boolean allowRepairs = true;
    public static boolean allowDisenchanting = true;
    public static int maxEnchantmentAmount = 5;
    public static float costFactor = 1.0f;
    public static int repairFactor = 5;
    public static int bonusShelves = 2;
    public static String[] blacklistedItems = new String[] {};
    public static String[] blacklistedEnchantments = new String[] {};
    
    public static boolean allowScrollLoot = true;
    public static int scrollWeight = 2;
    
    public static boolean allowScrollDrop = true;
    public static float scrollDropChance = 0.01f;
    
    public static void initConfig (File configFile) {
        
        config = new Configuration(configFile);
        
        final String settings = "settings";
        printDebug = config.getBoolean("printDebug", settings, true, "If true, Enchanting Plus will ocasionally write messages to the console.");
        useQuestMode = config.getBoolean("useQuestMode", settings, true, "Quest Mode requires that users collect scrolls to unlock enchantments, before they can make use of them at the table.");
        allowRepairs = config.getBoolean("allowRepairs", settings, true, "If this is true, players will be able to repair damaged items at the advanced enchanting table, using experience points");
        allowDisenchanting = config.getBoolean("allowDisenchanting", settings, true, "If this is true, players will be able to disenchant enchanted items they come across.");
        bonusShelves = config.getInt("bonusShelves", settings, 2, 0, 128, "An additional amount of bookshelves to factor in to the max level calculation. For each bonus shelf, 2 additional levels will be added to the max level cap of the table.");
        maxEnchantmentAmount = config.getInt("maxEnchantmentAmount", settings, 5, 0, 4096, "What is the maximum number of enchantments that could be applied at the Enchantment Table?");
        costFactor = config.getFloat("costFactor", settings, 1f, 0f, 128f, "A number used when calculated enchantment cost. This number is treated as a % based factor. 0.30 = 30% of the original cost. 1.5 = 150% of the original cost.");
        repairFactor = config.getInt("repairFactor", settings, 5, 0, 128, "A number used when calculating the repair costs. A higher factor means higher repair costs.");
        scrollDropChance = config.getFloat("scrollDropRate", settings, scrollDropChance, 0f, 1f, "The percent chance that a hostile mob should drop a scroll. Default is 0.01 which is 1%");
        blacklistedItems = config.getStringList("blacklistedItems", settings, new String[] {}, "A list of blacklisted items and blocks. Things in this list won't be enchantable at the eplus table. The format is the same as minecraft's id system. For example, minecraft:chainmail_helmet will prevent chainmail helmets from becoming enchanted.");
        blacklistedEnchantments = config.getStringList("blacklistedEnchantments", settings, new String[] {}, "A list of blacklisted enchantment ids. Each entry should be an integer.");
        allowScrollLoot = config.getBoolean("allowScrollLoot", settings, true, "If disabled, scrolls will only spawn in custom village chests.");
        scrollWeight = config.getInt("scrollWeight", settings, 2, Integer.MAX_VALUE, 0, "The weight of enchantment scrolls spawning in dungeon chests");
        
        if (config.hasChanged())
            config.save();
    }
}