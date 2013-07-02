package eplus.network.proxies;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import eplus.exceptions.FingerprintException;
import eplus.handlers.CapeTickHandler;
import eplus.handlers.VersionTickHandler;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void registerTickHandlers()
    {
        TickRegistry.registerTickHandler(new CapeTickHandler(), Side.CLIENT);
        TickRegistry.registerScheduledTickHandler(new VersionTickHandler(), Side.CLIENT);

    }

    @Override
    public void throwFingerprintError(String s)
    {
        throw new FingerprintException(s);
    }
}
