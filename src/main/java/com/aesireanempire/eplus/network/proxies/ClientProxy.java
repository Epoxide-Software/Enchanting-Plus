package com.aesireanempire.eplus.network.proxies;

import com.aesireanempire.eplus.exceptions.FingerprintException;
import com.aesireanempire.eplus.handlers.VersionTickHandler;
import com.aesireanempire.eplus.inventory.TileEnchantTable;
import com.aesireanempire.eplus.lib.EnchantmentHelp;
import com.aesireanempire.eplus.renders.EnchantmentTableRender;
import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void registerTickHandlers()
    {
        MinecraftForge.EVENT_BUS.register(new VersionTickHandler());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEnchantTable.class, new EnchantmentTableRender());
    }

    @Override
    public void registerEnchantments()
    {
        EnchantmentHelp.init();
    }

    @Override
    public void throwFingerprintError(String s)
    {
        throw new FingerprintException(s);
    }
}
