package eplus.handlers;

import cpw.mods.fml.common.registry.LanguageRegistry;
import eplus.EnchantingPlus;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Freyja Lesser GNU Public License v3
 *         (http://www.gnu.org/licenses/lgpl.html)
 */
public class LanguageHandler {

    private List<String> languages = new ArrayList<String>();

    private static LanguageHandler INSTANCE = new LanguageHandler();

    private final String location = "/assets/eplus/lang/";

    private LanguageHandler()
    {

    }

    public static LanguageHandler getInstance()
    {
        return INSTANCE;
    }

    public List<String> getLanguages()
    {
        return languages;
    }

    public void addLanguage(String uri)
    {
        if (!languages.contains(uri)) {
            languages.add(uri);
        }
    }

    public void loadLangauges()
    {
        for (String lang : languages) {
            LanguageRegistry.instance().loadLocalization(lang,
                    getLocalFromFileName(lang), isXMLlangfile(lang));
            EnchantingPlus.log.info("Localization "
                    + getLocalFromFileName(lang) + " loaded");
        }
    }

    private boolean isXMLlangfile(String lang)
    {
        return lang.endsWith(".xml");
    }

    private String getLocalFromFileName(String lang)
    {
        return lang.substring(lang.lastIndexOf("/") + 1, lang.lastIndexOf("."));
    }

    public String getTranslatedString(String string)
    {
        return (LanguageRegistry.instance().getStringLocalization(string)
                .isEmpty()) ? LanguageRegistry.instance()
                .getStringLocalization(string, "en_US") : LanguageRegistry
                .instance().getStringLocalization(string);
    }

    public void addLanguages(String langs)
    {
        InputStream resourceAsStream = getClass().getResourceAsStream(langs);

        Scanner scanner = new Scanner(resourceAsStream);

        while (scanner.hasNextLine()) {
            addLanguage(location + scanner.nextLine());
        }
    }
}
