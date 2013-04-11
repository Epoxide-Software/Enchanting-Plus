package eplus.network;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.PacketDispatcher;
import eplus.lib.ConfigurationSettings;
import eplus.network.packets.ConfigPacket;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

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
        HashMap<String,String> configs = new HashMap<String, String>();
        configs.put("useMod", String.valueOf(ConfigurationSettings.useMod));
        PacketDispatcher.sendPacketToServer(new ConfigPacket(configs).makePacket());
    }
}
