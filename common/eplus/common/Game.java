package eplus.common;

import cpw.mods.fml.common.FMLCommonHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Game
{
    private static final Logger log = Logger.getLogger("eplus");
    
    
    public static void log(Level level, String msg, Object[] params) {
        String m = msg;

        for (int i = 0; i < params.length; i++) {
          m = m.replace("{" + i + "}", params[i].toString());
        }

        log.log(level, m);
      }
    
    static {
        log.setParent(FMLCommonHandler.instance().getFMLLogger());
    }
}
