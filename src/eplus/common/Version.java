package eplus.common;

import cpw.mods.fml.common.Loader;
import eplus.common.localization.LocalizationHelper;
import net.minecraftforge.common.Property;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

public class Version implements Runnable {
    public static final Version instance = new Version();

    private static final String REMOTE_VERSION_FILE = "https://dl.dropbox.com/u/21347544/eplus/version";//"https://raw.github.com/odininon/EnchantingPlus/1.4.6/resources/version";
    private static final String REMOTE_CHANGELOG_FILE = "https://dl.dropbox.com/u/21347544/eplus/changelog/";

    public static EnumUpdateState currentVersion = EnumUpdateState.CURRENT;

    private static boolean updated;
    private static boolean versionCheckCompleted;
    private static String recommendedVersion;
    private static String currentModVersion;
    private static String[] cachedChangelog;

    public static String getRecommendedVersion()
    {
        return recommendedVersion;
    }

    public static String getCurrentModVersion()
    {
        return currentModVersion;
    }

    public static boolean versionSeen() {
        if(currentVersion == EnumUpdateState.BETA) {
            return true;
        }else if(!(currentVersion == EnumUpdateState.OUTDATED)) {
            return false;
        }

        Property property = EnchantingPlus.config.get("version","SeenVersion", currentModVersion);
        String seenVersion = property.getString();

        if(recommendedVersion == null || recommendedVersion.equals(seenVersion))
            return false;

        property.set(getRecommendedVersion());
        if(EnchantingPlus.config.hasChanged()) {
            EnchantingPlus.config.save();
        }
        return true;
    }
    public static String[] getChangelog(){
        if (cachedChangelog == null) {
            cachedChangelog = grabChangelog();
        }
         return cachedChangelog;
    }

    public static String[] grabChangelog(){
        InputStreamReader streamReader;
        BufferedReader reader;

        try {
            URL url = new URL(REMOTE_CHANGELOG_FILE + recommendedVersion);
            streamReader = new InputStreamReader(url.openStream());
        }
        catch (Exception ex ){
            streamReader = new InputStreamReader(Version.class.getClassLoader().getResourceAsStream("changelog"));
        }

        reader = new BufferedReader(streamReader);

        try {
            String line;
            ArrayList<String> changelog = new ArrayList<String>();
            while((line = reader.readLine()) != null) {
                changelog.add(line);
            }
            return changelog.toArray(new String[changelog.size()]);
        } catch (Exception ignored){
        }

        return new String[] {LocalizationHelper.getLocalString("version.fail.changelog")};
    }

    public static void versionCheck()
    {
        try {
            URL url = new URL(REMOTE_VERSION_FILE);

            InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line;
            while((line = reader.readLine()) != null) {
                if (line.startsWith(getMinecraftVersion())){
                    if(line.contains("eplus")){
                        String[] strings = line.split(":");
                        recommendedVersion = strings[2];
                    }
                }
            }

        } catch (Exception ex) {
            Game.log(Level.WARNING, "Unable to read from remote version authority.", new Object[0]);
            ex.printStackTrace();
            currentVersion = EnumUpdateState.CONNECTION_ERROR;
            return;
        }

        if (currentModVersion != null && currentModVersion.equals(recommendedVersion)) {
            Game.log(Level.INFO, "Using the latest version for Minecraft " + getMinecraftVersion(), new Object[0]);
            currentVersion = EnumUpdateState.CURRENT;
            updated = false;
        } else if (recommendedVersion == null || (currentModVersion != null  && ((Integer.parseInt(currentModVersion.substring(currentModVersion.lastIndexOf(".") + 1))) > (Integer.parseInt(recommendedVersion.substring(recommendedVersion.lastIndexOf(".") + 1)))))) {
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

    public static void init(Properties versionProperties) {
        if(versionProperties == null) {
            currentModVersion = "0.0.0";
            return;
        }

        String major = versionProperties.getProperty("eplus.major.number");
        String minor = versionProperties.getProperty("eplus.minor.number");
        String build = versionProperties.getProperty("eplus.build.number");
        currentModVersion = major + "." + minor + "." + build;
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