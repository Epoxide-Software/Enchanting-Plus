package net.epoxide.eplus.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class EPlusConfigurationHandler {
    
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

    // TODO implement these
    public static int costFactor = 5;
    public static int repairFactor = 5;
    public static int minimumBookshelfs = 5;
    
    public static void initConfig (File configFile) {
        
        config = new Configuration(configFile);
        
        String settings = "settings";
        printDebug = config.getBoolean("printDebug", settings, printDebug, "If true, Enchanting Plus will ocasionally write messages to the console.");
        useQuestMode = config.getBoolean("useQuestMode", settings, useQuestMode, "Quest Mode requires that users collect scrolls to unlock enchantments, before they can make use of them at the table.");
        allowRepairs = config.getBoolean("allowRepairs", settings, allowRepairs, "If this is true, players will be able to repair damaged items at the advanced enchanting table, using experience points");
        allowDisenchanting = config.getBoolean("allowDisenchanting", settings, allowDisenchanting, "If this is true, players will be able to disenchant enchanted items they come across.");
        allowDamagedEnchanting = config.getBoolean("allowDamagedEnchanting", settings, allowDamagedEnchanting, "If this is true, players will be able to enchant items which are not at full durability.");
        allowModifierTooltips = config.getBoolean("allowModifierTooltips", settings, allowModifierTooltips, "If true, special information will be written to the Scroll Modifier item tooltips.");
        minimumBookshelfs = config.getInt("minBookshelfs", settings, minimumBookshelfs, 0, 24, "The lowest number of bookshelfs required for the table to function properly.");
        secureItems = config.getBoolean("secureItems", settings, secureItems, "If enabled, players will only be able to enchant enchanted items if they enchanted them first.");
        allowUnownedModifications = config.getBoolean("allowUnownedModifications", settings, allowUnownedModifications, "If enabled, players will be able to modify items which are not currently owned by any other player. IE: Dungeon Loot");
        allowVillagers = config.getBoolean("allowVillagers", settings, allowVillagers, "If enabled, a custom E+ villager will spawn and generate in villages.");
        villagerID = config.getInt("villagerID", settings, villagerID, Integer.MIN_VALUE, Integer.MAX_VALUE, "A unique ID for the E+ villager. This should almost never need to be changed.");
        
        if (config.hasChanged())
            config.save();
    }
}