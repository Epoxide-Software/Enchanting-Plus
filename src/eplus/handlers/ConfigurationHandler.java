package eplus.handlers;

import eplus.EnchantingPlus;
import eplus.lib.ConfigurationSettings;
import net.minecraftforge.common.Configuration;

import java.io.File;
import java.util.logging.Level;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ConfigurationHandler {
    public static final String CATEGORY_CLIENT = "client";
    public static final String CATEGORY_SERVER = "server";
    public static final String CATEGORY_BOTH = "both";

    public static Configuration configuration;

    public static void init(File suggestedConfigurationFile)
    {
        configuration = new Configuration(suggestedConfigurationFile, false);

        EnchantingPlus.log.info("Initializing Configurations.");

        try {
            configuration.load();

            configuration.addCustomCategoryComment(CATEGORY_CLIENT, "Settings controlled client.\nCan vary from server to server.");
            configuration.addCustomCategoryComment(CATEGORY_SERVER, "Settings controlled by server.");
            configuration.addCustomCategoryComment(CATEGORY_BOTH, "Settings controlled by both.\nClient can be different from server.\nIf client differs from server, server can disable client's setting if false.");

            ConfigurationSettings.useMod = configuration.get(CATEGORY_BOTH, "useMod", ConfigurationSettings.useModDefault, "Set to true to use custom Enchantment Table in place of Vanilla").getBoolean(ConfigurationSettings.useModDefault);
            ConfigurationSettings.bookShelves = configuration.get(CATEGORY_SERVER, "needsBookShelves", ConfigurationSettings.bookShelvesDefault, "Set to true to require book shelves to enchant.").getBoolean(ConfigurationSettings.bookShelvesDefault);
            ConfigurationSettings.light = configuration.get(CATEGORY_SERVER, "hasLight", ConfigurationSettings.lightDefault, "Set to true to have the enchanting table emmit light.").getBoolean(ConfigurationSettings.lightDefault);
            ConfigurationSettings.particles = configuration.get(CATEGORY_CLIENT, "hasParticles", ConfigurationSettings.particlesDefault, "Set to true to have the enchanting table emmit particles.").getBoolean(ConfigurationSettings.particlesDefault);
            ConfigurationSettings.disenchanting = configuration.get(CATEGORY_SERVER, "AllowDisenchanting", ConfigurationSettings.disenchantingDefault, "Set to true to allow disenchanting.").getBoolean(ConfigurationSettings.disenchantingDefault);

        } catch (Exception e) {
            EnchantingPlus.log.info("Error Loading configuration");
            EnchantingPlus.log.log(Level.INFO, "Cause by {0}", e.getLocalizedMessage());
        } finally {
            if (configuration.hasChanged())
                configuration.save();
        }
    }

    public static void set(String categoryName, String propertyName, String newValue)
    {
        configuration.load();
        if (configuration.getCategoryNames().contains(categoryName)) {
            if (configuration.getCategory(categoryName).containsKey(propertyName)) {
                configuration.getCategory(categoryName).get(propertyName).set(newValue);
            }
        }

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static void set(String propertyName, String newValue)
    {
        set("general", propertyName, newValue);
    }
}
