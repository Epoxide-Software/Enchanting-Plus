package eplus.lib;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eplus.handlers.LanguageHandler;

/**
 * @author Freyja Lesser GNU Public License v3
 *         (http://www.gnu.org/licenses/lgpl.html)
 */

public class Strings
{
    public static String enchantingCost = LanguageHandler.getInstance().getTranslatedString("enchanting.cost");
    public static String playerLevel = LanguageHandler.getInstance().getTranslatedString("player.level");
    public static String repairCost = LanguageHandler.getInstance().getTranslatedString("repair.cost");
    public static String maxEnchantLevel = LanguageHandler.getInstance().getTranslatedString("enchant.level.max");
    public static String errorToolTip = LanguageHandler.getInstance().getTranslatedString("error.no.description");

    public static String enchantmentUnbreaking = LanguageHandler.getInstance().getTranslatedString("description.enchantment.unbreaking");
    public static String enchantmentThorns = LanguageHandler.getInstance().getTranslatedString("description.enchantment.thorns");
    public static String enchantmentAquaAffinity = LanguageHandler.getInstance().getTranslatedString("description.enchantment.aquaAffinity");
    public static String enchantmentRespiration = LanguageHandler.getInstance().getTranslatedString("description.enchantment.respiration");
    public static String enchantmentProtectionProjectile = LanguageHandler.getInstance().getTranslatedString("description.enchantment.protection.projectile");
    public static String enchantmentProtectionBlast = LanguageHandler.getInstance().getTranslatedString("description.enchantment.protection.blast");
    public static String enchantmentFeatherFalling = LanguageHandler.getInstance().getTranslatedString("description.enchantment.featherFalling");
    public static String enchantmentProtectionFire = LanguageHandler.getInstance().getTranslatedString("description.enchantment.protection.fire");
    public static String enchantmentProtection = LanguageHandler.getInstance().getTranslatedString("description.enchantment.protection");

    public static String enchantmentSharpness = LanguageHandler.getInstance().getTranslatedString("description.enchantment.sharpness");
    public static String enchantmentSmite = LanguageHandler.getInstance().getTranslatedString("description.enchantment.smite");
    public static String enchantmentBaneOfAthropods = LanguageHandler.getInstance().getTranslatedString("description.enchantment.baneOfArthropods");
    public static String enchantmentKnockBack = LanguageHandler.getInstance().getTranslatedString("description.enchantment.knockBack");
    public static String enchantmentFireAspect = LanguageHandler.getInstance().getTranslatedString("description.enchantment.fireAspect");
    public static String enchantmentLooting = LanguageHandler.getInstance().getTranslatedString("description.enchantment.looting");

    public static String enchantmentEfficiency = LanguageHandler.getInstance().getTranslatedString("description.enchantment.efficiency");
    public static String enchantmentSilkTouch = LanguageHandler.getInstance().getTranslatedString("description.enchantment.silkTouch");
    public static String enchantmentFortune = LanguageHandler.getInstance().getTranslatedString("description.enchantment.fortune");

    public static String enchantmentPower = LanguageHandler.getInstance().getTranslatedString("description.enchantment.power");
    public static String enchantmentPunch = LanguageHandler.getInstance().getTranslatedString("description.enchantment.punch");
    public static String enchantmentFlame = LanguageHandler.getInstance().getTranslatedString("description.enchantment.flame");
    public static String enchantmentInfinity = LanguageHandler.getInstance().getTranslatedString("description.enchantment.infinity");

    private static Map<String, String> defaultStrings = new HashMap<String, String>();

    static
    {
        putDefaultString("enchanting.cost", "Enchanting Cost");
        putDefaultString("player.level", "Player Level");
        putDefaultString("repair.cost", "Repair Cost");
        putDefaultString("enchant.level.max", "Max Enchantment Level");
        putDefaultString("error.no.description", "PLEASE REPORT THIS: Please add:");

        putDefaultString("description.enchantment.unbreaking", "Increases durability");

        // Armour
        putDefaultString("description.enchantment.thorns", "Chance of dealing damage to mobs or players attacking the wearer");
        putDefaultString("description.enchantment.aquaAffinity", "Increases underwater mining rate");
        putDefaultString("description.enchantment.respiration", "Decreases the rate of air loss underwater; increases time between damage while suffocating and drowning");
        putDefaultString("description.enchantment.protection.projectile", "Protection against damage from projectile entities");
        putDefaultString("description.enchantment.protection.blast", "Protection against explosion damage; reduces explosion recoil");
        putDefaultString("description.enchantment.featherFalling", "Protection against fall damage");
        putDefaultString("description.enchantment.protection.fire", "Protection against fire damage; fire is extinguished faster");
        putDefaultString("description.enchantment.protection", "Reduces damage from all sources");

        // Weapons
        putDefaultString("description.enchantment.sharpness", "Extra damage");
        putDefaultString("description.enchantment.smite", "Extra damage to undead mobs");
        putDefaultString("description.enchantment.baneOfArthropods", "Extra damage to spiders, cave spiders and silverfish");
        putDefaultString("description.enchantment.knockBack", "Increases knockback");
        putDefaultString("description.enchantment.fireAspect", "Lights the target on fire");
        putDefaultString("description.enchantment.looting", "Mobs can drop more loot");

        // Tools
        putDefaultString("description.enchantment.efficiency", "Faster resource gathering while in use");
        putDefaultString("description.enchantment.silkTouch", "Mined blocks will drop themselves instead of the item(s) it should drop");
        putDefaultString("description.enchantment.fortune", "Increases the drop rate of items from blocks");

        // Bows
        putDefaultString("description.enchantment.power", "Increases damage");
        putDefaultString("description.enchantment.punch", "Increases knockback");
        putDefaultString("description.enchantment.flame", "Flaming arrows");
        putDefaultString("description.enchantment.infinity", "Shooting consumes no arrows");
    }

    public static void buildDefaultXML(String locale)
    {
        try
        {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            final Document document = documentBuilder.newDocument();
            document.normalizeDocument();

            final Element properties = document.createElement("properties");
            document.appendChild(properties);

            final Attr version = document.createAttribute("version");
            version.setValue("1.0");
            properties.setAttributeNode(version);

            final Element comment = document.createElement("comment");
            comment.appendChild(document.createTextNode(locale + " Localization File"));

            properties.appendChild(comment);

            for (final String entry : defaultStrings.keySet())
            {
                final Element element = document.createElement("entry");
                element.setAttribute("key", entry);
                element.appendChild(document.createTextNode(defaultStrings.get(entry)));
                properties.appendChild(element);
            }

            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            final DOMSource domSource = new DOMSource(document);

            final String prefix = "C:\\Users\\Mike\\Documents\\MinecraftMods\\EnchantingPlus\\resources\\mods\\eplus\\lang\\";

            final File file = new File(prefix + locale + ".xml");

            final StreamResult streamResult = new StreamResult(file);

            transformer.setOutputProperty("method", "xml");
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://java.sun.com/dtd/properties.dtd");
            System.out.println(transformer.getOutputProperties().toString());

            transformer.transform(domSource, streamResult);

        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String getDefaultString(String unlocal)
    {
        return defaultStrings.get(unlocal);
    }

    public static void putDefaultString(String unlocal, String def)
    {
        defaultStrings.put(unlocal, def);
    }
}
