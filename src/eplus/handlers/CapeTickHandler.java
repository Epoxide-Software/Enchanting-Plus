package eplus.handlers;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import eplus.EnchantingPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.entity.player.EntityPlayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class CapeTickHandler implements ITickHandler {

	// TODO Fix when public
    private static final Minecraft mc = FMLClientHandler.instance().getClient();
    private static final String REMOTE_CAPES_LIST = "https://dl.dropboxusercontent.com/u/21347544/EnchantingPlus/capes.txt";
    private static final List<String> modders = new ArrayList<String>();

    static {
        getCapes();
    }

    private static void getCapes()
    {
        try {
            URL url = new URL(REMOTE_CAPES_LIST);

            InputStreamReader inputStreamReader = new InputStreamReader(
                    url.openStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!modders.contains(line)) {
                    modders.add(line);
                }
            }
        } catch (Exception ex) {
            EnchantingPlus.log
                    .warning("Could not load capes from remote authority.");
        }
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {

    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
    	/*
        if (mc.theWorld != null && mc.theWorld.playerEntities.size() > 0) {
            List players = mc.theWorld.playerEntities;

            for (Object player : players) {
                if (player != null) {
                    EntityPlayer thePlayer = (EntityPlayer) player;

                    for (String modder : modders) {
                        if (modder.equalsIgnoreCase(thePlayer.username)
                                && thePlayer.skinUrl
                                .startsWith("http://skins.minecraft.net/MinecraftSkins/")) {
                            String oldCloak = thePlayer.cloakUrl;
                            // thePlayer.cloakUrl =
                            // "http://aesireanempire.com/odin/eplusCape.png";
                            thePlayer.cloakUrl = "http://i.imgur.com/UyEp9Yb.png";
                            if (!thePlayer.cloakUrl.equals(oldCloak)) {
                                mc.renderEngine.obtainImageData(
                                        thePlayer.cloakUrl,
                                        new ImageBufferDownload());
                            }
                        }
                    }
                }
            }
        }
        */
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT);
    }

    @Override
    public String getLabel()
    {
        return "CapeTickHandler";
    }
}
