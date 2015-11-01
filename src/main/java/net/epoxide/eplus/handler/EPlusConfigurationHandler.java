package net.epoxide.eplus.handler;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class EPlusConfigurationHandler {

    /**
     * A representation of the physical configuration file on the hard drive.
     */
    public static Configuration config;
    public static boolean needsBookShelves = false;
    public static double costFactor = 5;
    public static int repairFactor = 5;
    public static boolean allowRepair = true;
    public static boolean allowDisenchanting = true;
    public static boolean allowEnchantDamaged = true;
    public static boolean allowDisenUnowned = true;
    public static float minimumBook = 5;
    public static boolean debug = true;
    public static boolean useMod =true;
    public static boolean unlockEnchants = true;

    public EPlusConfigurationHandler (File configFile) {

        config = new Configuration(configFile);

        if (config.hasChanged())
            config.save();
    }
}