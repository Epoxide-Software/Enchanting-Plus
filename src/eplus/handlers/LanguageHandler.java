package eplus.handlers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cpw.mods.fml.common.registry.LanguageRegistry;
import eplus.EnchantingPlus;

/**
 * @author Freyja Lesser GNU Public License v3
 *         (http://www.gnu.org/licenses/lgpl.html)
 */
public class LanguageHandler
{

    private final List<String> languages = new ArrayList<String>();

    private static LanguageHandler INSTANCE = new LanguageHandler();

    public static LanguageHandler getInstance()
    {
        return INSTANCE;
    }

    private final String location = "/assets/eplus/lang/";

    private LanguageHandler()
    {

    }

    public void addLanguage(String uri)
    {
        if (!languages.contains(uri))
        {
            languages.add(uri);
        }
    }

    public void addLanguages(String langs)
    {
        final InputStream resourceAsStream = getClass().getResourceAsStream(langs);

        final Scanner scanner = new Scanner(resourceAsStream);

        while (scanner.hasNextLine())
        {
            addLanguage(location + scanner.nextLine());
        }
        scanner.close();
    }

    public List<String> getLanguages()
    {
        return languages;
    }

    private String getLocalFromFileName(String lang)
    {
        return lang.substring(lang.lastIndexOf("/") + 1, lang.lastIndexOf("."));
    }

    public String getTranslatedString(String string)
    {
        return LanguageRegistry.instance().getStringLocalization(string).isEmpty() ? LanguageRegistry.instance().getStringLocalization(string, "en_US") : LanguageRegistry
             .instance().getStringLocalization(string);
        
    }

    private boolean isXMLlangfile(String lang)
    {
        return lang.endsWith(".xml");
    }

    public void loadLangauges()
    {
        for (final String lang : languages)
        {
            LanguageRegistry.instance().loadLocalization(lang, getLocalFromFileName(lang), isXMLlangfile(lang));
            EnchantingPlus.log.info("Localization " + getLocalFromFileName(lang) + " loaded");
        }
    }
}
