package net.darkhax.eplus.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public final class ConfigurationHandler {
    
    public static Configuration config;
    
    public static boolean printDebug = true;
    public static boolean useQuestMode = true;
    public static boolean allowRepairs = true;
    public static boolean allowDisenchanting = true;
    public static boolean allowDamagedEnchanting = true;
    public static boolean allowModifierTooltips = true;
    public static boolean needsBookShelves = true;
    public static boolean secureItems = true;
    public static boolean allowUnownedModifications = true;
    public static boolean allowVillagers = true;
    public static int villagerID = 935153;
    public static int maxEnchantmentAmount = 5;
    public static float costFactor = 1.0f;
    public static int repairFactor = 5;
    public static int bonusShelves = 2;
    public static float scrollDrop = 0.01f;
    public static String[] blacklistedItems = new String[] {};
    public static String[] blacklistedEnchantments = new String[] {};
    public static boolean allowScrollLoot = true;
    
    public static void initConfig (File configFile) {
        
        config = new Configuration(configFile);
        
        final String settings = "settings";
        printDebug = config.getBoolean("printDebug", settings, printDebug, "If true, Enchanting Plus will ocasionally write messages to the console.");
        useQuestMode = config.getBoolean("useQuestMode", settings, useQuestMode, "Quest Mode requires that users collect scrolls to unlock enchantments, before they can make use of them at the table.");
        allowRepairs = config.getBoolean("allowRepairs", settings, allowRepairs, "If this is true, players will be able to repair damaged items at the advanced enchanting table, using experience points");
        allowDisenchanting = config.getBoolean("allowDisenchanting", settings, allowDisenchanting, "If this is true, players will be able to disenchant enchanted items they come across.");
        allowDamagedEnchanting = config.getBoolean("allowDamagedEnchanting", settings, allowDamagedEnchanting, "If this is true, players will be able to enchant items which are not at full durability.");
        allowModifierTooltips = config.getBoolean("allowModifierTooltips", settings, allowModifierTooltips, "If true, special information will be written to the Scroll Modifier item tooltips.");
        bonusShelves = config.getInt("bonusShelves", settings, bonusShelves, 0, 128, "An additional amount of bookshelves to factor in to the max level calculation. For each bonus shelf, 2 additional levels will be added to the max level cap of the table.");
        secureItems = config.getBoolean("secureItems", settings, secureItems, "If enabled, players will only be able to enchant enchanted items if they enchanted them first.");
        allowUnownedModifications = config.getBoolean("allowUnownedModifications", settings, allowUnownedModifications, "If enabled, players will be able to modify items which are not currently owned by any other player. IE: Dungeon Loot");
        allowVillagers = config.getBoolean("allowVillagers", settings, allowVillagers, "If enabled, a custom E+ villager will spawn and generate in villages.");
        needsBookShelves = config.getBoolean("needBookshelves", settings, needsBookShelves, "Should the table restrict the level of effects based on the amount of bookshelves?");
        maxEnchantmentAmount = config.getInt("maxEnchantmentAmount", settings, maxEnchantmentAmount, 0, 4096, "What is the maximum number of enchantments that could be applied at the Enchantment Table?");
        villagerID = config.getInt("villagerID", settings, villagerID, Integer.MIN_VALUE, Integer.MAX_VALUE, "A unique ID for the E+ villager. This should almost never need to be changed.");
        costFactor = config.getFloat("costFactor", settings, costFactor, 0f, 128f, "A number used when calculated enchantment cost. This number is treated as a % based factor. 0.30 = 30% of the original cost. 1.5 = 150% of the original cost.");
        repairFactor = config.getInt("repairFactor", settings, repairFactor, 0, 128, "A number used when calculating the repair costs. A higher factor means higher repair costs.");
        scrollDrop = config.getFloat("scrollDropRate", settings, scrollDrop, 0f, 1f, "The percent chance that a hostile mob should drop a scroll. Default is 0.01 which is 1%");
        blacklistedItems = config.getStringList("blacklistedItems", settings, blacklistedItems, "A list of blacklisted items and blocks. Things in this list won't be enchantable at the eplus table. The format is the same as minecraft's id system. For example, minecraft:chainmail_helmet will prevent chainmail helmets from becoming enchanted.");
        blacklistedEnchantments = config.getStringList("blacklistedEnchantments", settings, blacklistedEnchantments, "A list of blacklisted enchantment ids. Each entry should be an integer.");
        allowScrollLoot = config.getBoolean("allowScrollLoot", settings, true, "If disabled, scrolls will only spawn in custom village chests.");
        
        if (config.hasChanged())
            config.save();
    }
}