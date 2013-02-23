package eplus.common;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;

public class Version implements Runnable {
    public static Version instance = new Version();

    public static final String VERSION = "1.12.9";
    private static final String REMOTE_VERSION_FILE = "https://raw.github.com/odininon/EnchantingPlus/master/resources/version";//"https://raw.github.com/odininon/EnchantingPlus/1.4.6/resources/version";
    public static EnumUpdateState currentVersion = EnumUpdateState.CURRENT;

    private static boolean updated;
    private static boolean versionCheckCompleted;
    private static String recommendedVersion;
    private static String currentModVersion;

    public static String getRecommendedVersion()
    {
        return recommendedVersion;
    }

    public static String getCurrentModVersion()
    {
        return currentModVersion;
    }

    public static void versionCheck()
    {

        Properties props = new Properties();
        try {
            URL url = new URL(REMOTE_VERSION_FILE);

            InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());

            if (inputStreamReader != null) {
                props.load(inputStreamReader);
                String major = props.getProperty("eplus.major.number");
                String minor = props.getProperty("eplus.minor.number");
                String build = props.getProperty("eplus.build.number");
                recommendedVersion = major + "." + minor + "." + build;
            }
        } catch (Exception ex) {
            Game.log(Level.WARNING, "Unable to read from remote version authority.", new Object[0]);
            ex.printStackTrace();
            currentVersion = EnumUpdateState.CONNECTION_ERROR;
            recommendedVersion = "0.0.0";
            return;
        }

        InputStream inputStream = Version.class.getClassLoader().getResourceAsStream("version");

        if (inputStream != null) {
            try {
                props.load(inputStream);
                String major = props.getProperty("eplus.major.number");
                String minor = props.getProperty("eplus.minor.number");
                String build = props.getProperty("eplus.build.number");
                currentModVersion = major + "." + minor + "." + build;
            } catch (Exception ex) {
                Game.log(Level.INFO, "Couldn't read current version", new Object[0]);
                ex.printStackTrace();
                currentModVersion = "0.0.0";
            }
        }

        if (currentModVersion != null && currentModVersion.equals(recommendedVersion)) {
            Game.log(Level.INFO, "Using the latest version for Minecraft " + getMinecraftVersion(), new Object[0]);
            currentVersion = EnumUpdateState.CURRENT;
            updated = false;
        } else if (currentModVersion != null && ((Integer.parseInt(currentModVersion.substring(currentModVersion.lastIndexOf(".") + 1))) > (Integer.parseInt(recommendedVersion.substring(recommendedVersion.lastIndexOf(".") + 1))))) {
            Game.log(Level.INFO, "Using the beta build {0}", new Object[] { currentModVersion });
            currentVersion = EnumUpdateState.BETA;
            updated = false;
        } else {
            Game.log(Level.INFO, "An updated version of Enchanting Plus is available: {0}", new Object[] { getRecommendedVersion() });
            currentVersion = EnumUpdateState.OUTDATED;
            updated = true;
        }
    }

    public static String getMinecraftVersion()
    {
        return Loader.instance().getMinecraftModContainer().getVersion();
    }

    @Override
    public void run() {
        int count = 0;
        currentVersion = null;

        Game.log(Level.INFO, "Starting version check thread", new Object[]{0});

        while (count < 3 && (currentVersion == null || currentVersion == EnumUpdateState.CONNECTION_ERROR)){
            versionCheck();
            count++;
        }

        Game.log(Level.INFO, "Version check complete with {0}", new Object[]{currentVersion.toString()});
        versionCheckCompleted = true;
    }

    public static enum EnumUpdateState {
        CURRENT, OUTDATED, CONNECTION_ERROR, BETA
    }

    public static boolean hasUpdated()
    {
        return updated;
    }

    public static boolean isVersionCheckComplete()
    {
        return versionCheckCompleted;
    }

    public static void check() {
        new Thread(instance).start();
    }
}