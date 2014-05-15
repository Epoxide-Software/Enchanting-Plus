package com.aesireanempire.eplus.handlers;

import com.aesireanempire.eplus.EnchantingPlus;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class CapeTickHandler
{

    // TODO Fix when public
    private static final Minecraft mc = FMLClientHandler.instance().getClient();
    private static final String REMOTE_CAPES_LIST = "https://dl.dropboxusercontent.com/u/21347544/EnchantingPlus/capes.txt";
    private static final List<String> modders = new ArrayList<String>();
    private static final String capeURL = "http://i.imgur.com/UyEp9Yb.png";

    public static void loadCapes()
    {
        try
        {
            final URL url = new URL(REMOTE_CAPES_LIST);

            final InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
            final BufferedReader reader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = reader.readLine()) != null)
            {
                if (!modders.contains(line))
                {
                    modders.add(line);
                    setCape(line);
                }
            }
        }
        catch (final Exception ex)
        {
            EnchantingPlus.log.warn("Could not load capes from remote authority.");
        }
    }

    private static void setCape(String username)
    {
        ThreadDownloadImageData cape = new ThreadDownloadImageData(CapeTickHandler.capeURL, null, null);
        Minecraft.getMinecraft().getTextureManager().loadTexture(new ResourceLocation("cloaks/" + username), cape);
    }
}
