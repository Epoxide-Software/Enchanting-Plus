package eplus.client;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import eplus.common.CommonProxy;

public class ClientProxy extends CommonProxy
{

    @Override
    public void init() {
        TickRegistry.registerScheduledTickHandler(new LatestVersionMessage(), Side.CLIENT);
    }
}
