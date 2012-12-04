package eplus.common;

import net.minecraftforge.common.Configuration;

public class Config
{
    public static boolean       allowDisenchanting = true;

    public static boolean       allowRepair        = true;

    public static Configuration serverConfig;

    public static void setupServerConfig(Configuration var1)
    {
        serverConfig = var1;
        serverConfig.load();
        allowDisenchanting = serverConfig.get("disenchanting", "general", true).getBoolean(true);
        allowRepair = serverConfig.get("repair", "general", true).getBoolean(true);
        serverConfig.save();
    }
}
