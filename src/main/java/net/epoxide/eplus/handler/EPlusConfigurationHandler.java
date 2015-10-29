package net.epoxide.eplus.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class EPlusConfigurationHandler {
    
    /**
     * A representation of the physical configuration file on the hard drive.
     */
    public static Configuration config;
    
    public EPlusConfigurationHandler(File configFile) {
        
        config = new Configuration(configFile);
        
        if (config.hasChanged())
            config.save();
    }
}