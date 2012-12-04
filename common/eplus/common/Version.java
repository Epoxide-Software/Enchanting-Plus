package eplus.common;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

public class Version
{
    public static final String VERSION = "@VERSION@";
    public static final String BUILD_NUMBER = "@BUILD_NUMBER@";
    private static final String REMOTE_VERSION_FILE = "http://odin.aesireanempire.com/version.php";
    public static EnumUpdateState currentVersion = EnumUpdateState.CURRENT;
    public static final int FORGE_VERSION_MAJOR = 4;
    public static final int FORGE_VERSION_MINOR = 0;
    public static final int FORGE_VERSION_PATCH = 0;

    private static boolean updated;
    private static boolean versionCheckCompleted;
    private static String recommendedVersion;

    public static String getRecommendedVersion() {
        return recommendedVersion;
    }

    public static void versionCheck() {
        try {
            String location = REMOTE_VERSION_FILE;
            HttpURLConnection conn = null;
            while ((location != null) && (!location.isEmpty())) {
                URL url = new URL(location);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 (.NET CLR 3.5.30729)");
                conn.connect();
                location = conn.getHeaderField("Location");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = null;
            String mcVersion = getMinecraftVersion();
            while ((line = reader.readLine()) != null) {
                if ((line.startsWith(mcVersion)) && (line.contains("Eplus"))) {
                    String[] tokens = line.split(":");
                    recommendedVersion = tokens[2];

                    if (line.endsWith(VERSION)) {
                        FMLLog.finer("Enchanting Plus: Using the latest version for Minecraft " + mcVersion, new Object[0]);
                        currentVersion = EnumUpdateState.CURRENT;
                        updated = false;
                        return;
                    }
                }

            }
            Game.log(Level.INFO, "An updated version of Enchanting Plus is available: {0}", new Object[] { getRecommendedVersion() });
            currentVersion = EnumUpdateState.OUTDATED;
            updated = true;
        } catch (Exception e) {
            e.printStackTrace();
            FMLLog.warning("Enchanting Plus: Unable to read from remote version authority.", new Object[0]);
            currentVersion = EnumUpdateState.CONNECTION_ERROR;
        }
        versionCheckCompleted = true;
    }

    public static String getMinecraftVersion() {
        return Loader.instance().getMinecraftModContainer().getVersion();
    }

    public static enum EnumUpdateState
    {
        CURRENT, OUTDATED, CONNECTION_ERROR;
    }

    public static boolean hasUpdated() {
        return updated;
    }

    public static boolean isVersionCheckComplete() {
        return versionCheckCompleted;
    }

}