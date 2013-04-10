package eplus.network;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import eplus.handlers.CapeTickHandler;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void registerTickHandlers()
    {
        TickRegistry.registerTickHandler(new CapeTickHandler(), Side.CLIENT);
    }
}
