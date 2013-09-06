package eplus.handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import eplus.EnchantingPlus;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class CapeTickHandler implements ITickHandler
{

    // TODO Fix when public
    private static final Minecraft mc = FMLClientHandler.instance().getClient();
    private static final String REMOTE_CAPES_LIST = "https://dl.dropboxusercontent.com/u/21347544/EnchantingPlus/capes.txt";
    private static final List<String> modders = new ArrayList<String>();
    private static final String capeURL = "http://i.imgur.com/UyEp9Yb.png";

    static
    {
        getCapes();
    }

    private static void getCapes()
    {
        try
        {
            final URL url = new URL(REMOTE_CAPES_LIST);

            final InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
            final BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                if (!modders.contains(line))
                {
                    modders.add(line);
                }
            }
        } catch (final Exception ex)
        {
            EnchantingPlus.log.warning("Could not load capes from remote authority.");
        }
    }

    @Override
    public String getLabel()
    {
        return "CapeTickHandler";
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {

        if (mc.theWorld != null && mc.theWorld.playerEntities.size() > 0)
        {
            @SuppressWarnings("unchecked")
            List<EntityPlayer> players = mc.theWorld.playerEntities;

            for (EntityPlayer player : players)
            {
                if (player != null)
                {
                    for (String modder : modders)
                    {
                        if (modder.equalsIgnoreCase(player.username) && player.cloakUrl.startsWith("http://skins.minecraft.net/MinecraftCloaks/"))
                        {

                            String oldCloak = player.cloakUrl;
                            player.cloakUrl = capeURL;
                            if (!player.cloakUrl.equals(oldCloak))
                            {
                                mc.renderEngine.obtainImageData(player.cloakUrl, new ImageBufferDownload());
                            }
                        }
                    }
                }
            }

        }

    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT);
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {

    }
}
