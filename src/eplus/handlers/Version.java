package eplus.handlers;

import cpw.mods.fml.common.Loader;
import eplus.EnchantingPlus;
import eplus.lib.References;
import net.minecraftforge.common.Property;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 * @author Freyja Lesser GNU Public License v3
 *         (http://www.gnu.org/licenses/lgpl.html)
 */
public class Version implements Runnable {
    public static Version instance = new Version();

    private static final String REMOTE_VERSION_FILE = "https://dl.dropboxusercontent.com/u/21347544/EnchantingPlus/eplusvers.txt";// "https://raw.github.com/odininon/EnchantingPlus/1.4.6/resources/version";
    private static final String REMOTE_CHANGELOG = "https://dl.dropboxusercontent.com/u/21347544/EnchantingPlus/changelogs/";

    public static EnumUpdateState currentVersion = EnumUpdateState.CURRENT;

    private static boolean updated;
    private static boolean versionCheckCompleted;
    private static String recommendedVersion;
    private static String currentModVersion;
    private static String[] cachedChangelog;
    private static String recommendedDownload;

    public static String getRecommendedVersion()
    {
        return recommendedVersion;
    }

    public static String getCurrentModVersion()
    {
        return currentModVersion;
    }

    public static boolean versionSeen()
    {
        if (!(currentVersion == EnumUpdateState.OUTDATED)) {
            return false;
        }

        Property property = ConfigurationHandler.configuration.get("version",
                "SeenVersion", currentModVersion);
        String seenVersion = property.getString();

        if (recommendedVersion == null
                || recommendedVersion.equals(seenVersion))
            return false;

        property.set(getRecommendedVersion());
        ConfigurationHandler.configuration.save();
        return true;
    }

    public static String[] getChangelog()
    {
        if (cachedChangelog == null) {
            cachedChangelog = grabChangelog(recommendedVersion);
        }
        return cachedChangelog;
    }

    public static String[] grabChangelog(String recommendedVersion)
    {

        recommendedVersion = recommendedVersion.replace(".", "_") + ".txt";

        String changelogURL = REMOTE_CHANGELOG + recommendedVersion;

        try {
            URL url = new URL(changelogURL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    url.openStream()));

            String line = null;
            ArrayList<String> changelog = new ArrayList<String>();
            while ((line = reader.readLine()) != null) {

                if (line.startsWith("#"))
                    continue;

                if (line.isEmpty())
                    continue;

                changelog.add(line);
            }
            return changelog.toArray(new String[0]);
        } catch (Exception ex) {

        }
        return new String[]{"Failed to grab changelog."};
    }

    public static void versionCheck()
    {
        Properties props = new Properties();
        try {
            URL url = new URL(REMOTE_VERSION_FILE);

            InputStreamReader inputStreamReader = new InputStreamReader(
                    url.openStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = null;
            String mcVersion = getMinecraftVersion();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(mcVersion)) {
                    if (line.contains(References.MODID)) {

                        String[] tokens = line.split(":");
                        recommendedVersion = tokens[2];
                        recommendedDownload = tokens[3];

                        if (line.endsWith(currentModVersion)) {
                            EnchantingPlus.log
                                    .info("Using the latest version ["
                                            + getCurrentModVersion()
                                            + "] for Minecraft " + mcVersion);
                            currentVersion = EnumUpdateState.CURRENT;
                            return;
                        }
                    }
                }
            }
            EnchantingPlus.log.warning("Using outdated version ["
                    + getCurrentModVersion() + "] for Minecraft " + mcVersion
                    + ". Consider updating.");
            currentVersion = EnumUpdateState.OUTDATED;

        } catch (Exception ex) {
            EnchantingPlus.log.log(Level.WARNING,
                    "Unable to read from remote version authority.",
                    new Object[0]);
            ex.printStackTrace();
            currentVersion = EnumUpdateState.CONNECTION_ERROR;
            recommendedVersion = "0.0.0";
            return;
        }

        if (currentModVersion != null
                && currentModVersion.equals(recommendedVersion)) {
            EnchantingPlus.log.log(Level.INFO,
                    "Using the latest version for Minecraft "
                            + getMinecraftVersion(), new Object[0]);
            currentVersion = EnumUpdateState.CURRENT;
            updated = false;
        } else {
            EnchantingPlus.log.log(Level.INFO,
                    "An updated version of Enchanting Plus is available: {0}",
                    new Object[]{getRecommendedVersion()});
            currentVersion = EnumUpdateState.OUTDATED;
            updated = true;
        }
        props.clear();
    }

    public static String getMinecraftVersion()
    {
        return Loader.instance().getMinecraftModContainer().getVersion();
    }

    public static String getRecommendedDownload()
    {
        return recommendedDownload;
    }

    @Override
    public void run()
    {
        int count = 0;
        currentVersion = null;

        EnchantingPlus.log.info("Starting version check thread");

        while (count < 3
                && (currentVersion == null || currentVersion == EnumUpdateState.CONNECTION_ERROR)) {
            versionCheck();
            count++;
        }

        EnchantingPlus.log
                .info("Version check complete with " + currentVersion);
        versionCheckCompleted = true;
    }

    public static void init(Properties versionProperties)
    {
        if (versionProperties == null) {
            currentModVersion = "0.0.0";
            return;
        }

        String major = versionProperties.getProperty("eplus.major.number");
        String minor = versionProperties.getProperty("eplus.minor.number");
        String build = versionProperties.getProperty("eplus.revision.number");
        currentModVersion = major + "." + minor;
    }

    public static enum EnumUpdateState {
        CURRENT, OUTDATED, CONNECTION_ERROR
    }

    public static boolean hasUpdated()
    {
        return updated;
    }

    public static boolean isVersionCheckComplete()
    {
        return versionCheckCompleted;
    }

    public static void check()
    {
        new Thread(instance).start();
    }
}
