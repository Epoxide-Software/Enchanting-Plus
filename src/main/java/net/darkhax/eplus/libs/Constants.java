package net.darkhax.eplus.libs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Constants {
    
    public static final String MOD_ID = "eplus";
    public static final String MOD_NAME = "Enchanting Plus";
    public static final String VERSION_NUMBER = "4.1.0.0";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);
    public static final String DEPENDENCIES = "required-after:bookshelf@[1.4.1.320,)";
    
    public static final String FACTORY = "";
    public static final String CLIENT_PROXY_CLASS = "net.darkhax.eplus.client.ProxyClient";
    public static final String SERVER_PROXY_CLASS = "net.darkhax.eplus.common.ProxyCommon";
}
