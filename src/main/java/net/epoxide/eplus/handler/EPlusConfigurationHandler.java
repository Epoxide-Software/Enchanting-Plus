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
    
    // TODO implement these
    public static boolean allowDisenUnowned = true;
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
        
        if (config.hasChanged())
            config.save();
    }
}