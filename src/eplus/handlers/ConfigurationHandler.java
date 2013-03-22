package eplus.handlers;

import eplus.lib.ConfigurationSettings;
import net.minecraftforge.common.Configuration;

import java.io.File;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ConfigurationHandler
{
    public static Configuration configuration;

    public static void init(File suggestedConfigurationFile)
    {
        configuration = new Configuration(suggestedConfigurationFile, false);

        try
        {
            configuration.load();

            ConfigurationSettings.useMod = configuration.get(Configuration.CATEGORY_GENERAL, "useMod", ConfigurationSettings.useModDefault, "Set to true to use custom Enchantment Table in place of Vanilla").getBoolean(ConfigurationSettings.useModDefault);


        } catch (Exception e)
        {

        } finally
        {
            if (configuration.hasChanged())
                configuration.save();
        }


    }
}
