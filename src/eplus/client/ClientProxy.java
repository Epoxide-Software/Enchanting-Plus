package eplus.client;

import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import eplus.common.CommonProxy;
import eplus.common.EnchantingPlus;

public class ClientProxy extends CommonProxy
{

    @Override
    public void init() {
    	if (EnchantingPlus.allowUpdateCheck) {
    		TickRegistry.registerScheduledTickHandler(new LatestVersionMessage(), Side.CLIENT);
    	}
    }
}
