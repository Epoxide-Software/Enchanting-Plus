package eplus.network;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import eplus.network.packets.ReConfigPacket;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class PlayerTracker implements IPlayerTracker {

    @Override
    public void onPlayerLogin(EntityPlayer player)
    {

    }

    @Override
    public void onPlayerLogout(EntityPlayer player)
    {

    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player)
    {

    }

    @Override
    public void onPlayerRespawn(EntityPlayer player)
    {
        PacketDispatcher.sendPacketToPlayer(new ReConfigPacket().makePacket(),
                (Player) player);
    }
}
