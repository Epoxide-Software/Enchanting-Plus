package eplus.client;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.registry.TickRegistry;
import eplus.common.CommonProxy;

public class ClientProxy extends CommonProxy
{

    @Override
    public void init() {
        TickRegistry.registerScheduledTickHandler(new LatestVersionMessage(), Side.CLIENT);
    }
}
