package eplus.common.localization;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class LocalizationHandler {

	public static void addLanguages()
	{
		for (String localizationFile : LocalizationRegistry.Instance().getLocalizations())
		{
			LanguageRegistry.instance().loadLocalization(localizationFile, LocalizationHelper.getLanguage(localizationFile), LocalizationHelper.isValidXML(localizationFile));
		}
	}

}
