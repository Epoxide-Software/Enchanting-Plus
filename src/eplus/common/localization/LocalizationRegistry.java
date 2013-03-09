package eplus.common.localization;

import java.util.ArrayList;

/**
 * Used to register new localizations for the mod
 * 
 * @author odininon
 * 
 */
public class LocalizationRegistry {

	/**
	 * Singleton Instance
	 */
	private static final LocalizationRegistry INSTANCE = new LocalizationRegistry();

	/**
	 * A list of localization files to be loaded
	 */
	private final ArrayList<String> LocalizationFiles = new ArrayList<String>();

	/**
	 * 
	 * @return The Singleton instance of LocalizationRegistry
	 */
	public static LocalizationRegistry Instance()
	{
		return INSTANCE;
	}

	/**
	 * Used to add localization file to the registry
	 * 
	 * @param file
	 *            path to the localization file to be loaded
	 */
	public void addLocalizationFile(String file)
	{
		LocalizationFiles.add(file);
	}

	/**
	 * 
	 * @return A list of localization files to be loaded
	 */
	public ArrayList<String> getLocalizations()
	{
		return LocalizationFiles;
	}

	private LocalizationRegistry() {
	}

}
