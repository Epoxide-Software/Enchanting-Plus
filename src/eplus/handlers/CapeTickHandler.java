package eplus.handlers;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.entity.player.EntityPlayer;

import java.util.EnumSet;
import java.util.List;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class CapeTickHandler implements ITickHandler {

    private static final Minecraft mc = FMLClientHandler.instance().getClient();
    private final String[] modders = new String[]{"odininon", "FSNTF"};

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {

    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        if (mc.theWorld != null && mc.theWorld.playerEntities.size() > 0) {
            List players = mc.theWorld.playerEntities;

            for (Object player : players) {
                if (player != null) {
                    EntityPlayer thePlayer = (EntityPlayer) player;

                    for (String modder : modders) {
                        if (modder.equalsIgnoreCase(thePlayer.username) && thePlayer.skinUrl.startsWith("http://skins.minecraft.net/MinecraftSkins/"))
                        {
                            String oldCloak = thePlayer.cloakUrl;
                            //thePlayer.cloakUrl = "http://aesireanempire.com/odin/eplusCape.png";
                            thePlayer.cloakUrl = "http://i.imgur.com/UyEp9Yb.png";
                            if (!thePlayer.cloakUrl.equals(oldCloak)) {
                                mc.renderEngine.obtainImageData(thePlayer.cloakUrl, new ImageBufferDownload());
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
    public String getLabel()
    {
        return "CapeTickHandler";
    }
}
