package eplus.handlers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import static eplus.handlers.NickyHandler.getNick;
import static eplus.handlers.NickyHandler.getUsername;

/**
 * @user Freyja
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class NickyTickHandler implements ITickHandler
{
    private static final Minecraft mc = FMLClientHandler.instance().getClient();

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {

    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        if(mc.theWorld != null && mc.theWorld.playerEntities.size() > 0)
        {
            List players = mc.theWorld.playerEntities;
            for(Object player: players)
            {
                EntityPlayer entityPlayer = (EntityPlayer) player;
                String nickName = getNick(entityPlayer.username);
                String userName = getUsername(entityPlayer.username);



                if(nickName!= null && !entityPlayer.username.equals(nickName) ) {
                    entityPlayer.username = nickName;
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
        return "Nicky";
    }
}
