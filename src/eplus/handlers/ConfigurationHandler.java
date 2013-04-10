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
    public static Configuration configuration;

    public static void init(File suggestedConfigurationFile)
    {
        configuration = new Configuration(suggestedConfigurationFile, false);

        EnchantingPlus.log.info("Initializing Configurations.");

        try {
            configuration.load();

            ConfigurationSettings.useMod = configuration.get(Configuration.CATEGORY_GENERAL, "useMod", ConfigurationSettings.useModDefault, "Set to true to use custom Enchantment Table in place of Vanilla").getBoolean(ConfigurationSettings.useModDefault);


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

        if(configuration.hasChanged()){
            configuration.save();
        }
    }

    public static void set(String propertyName, String newValue) {
        set("general",propertyName,newValue);
    }
}
