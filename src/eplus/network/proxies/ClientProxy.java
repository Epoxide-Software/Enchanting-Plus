package eplus.network.proxies;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeHooks;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import eplus.exceptions.FingerprintException;
import eplus.handlers.CapeTickHandler;
import eplus.handlers.VersionTickHandler;
import eplus.inventory.TileEnchantTable;
import eplus.lib.EnchantmentHelp;
import eplus.renders.EnchantmentTableRender;

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
        
        ClientRegistry.bindTileEntitySpecialRenderer(TileEnchantTable.class, new EnchantmentTableRender());
        EnchantmentHelp.init();
    }

    @Override
    public void throwFingerprintError(String s)
    {
        throw new FingerprintException(s);
    }
}
