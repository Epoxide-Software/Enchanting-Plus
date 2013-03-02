package eplus.common.localization;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class LocalizationHelper {

	public static boolean isValidXML(String filename)
	{
		return filename.endsWith(".xml");
	}

	public static String getLanguage(String filename)
	{
		return filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf("."));
	}

    public static String getLocalString(String key){
        return LanguageRegistry.instance().getStringLocalization(key);
    }
}
