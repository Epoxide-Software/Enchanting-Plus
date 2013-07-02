package eplus.network.packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import eplus.lib.ConfigurationSettings;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class ReConfigPacket extends BasePacket {

    @Override
    public void write(ByteArrayDataOutput output)
    {

    }

    @Override
    public void read(ByteArrayDataInput input)
    {

    }

    @Override
    public void execute(EntityPlayer player, Side side)
    {
        if (side == Side.CLIENT) {
            HashMap<String, String> configs = new HashMap<String, String>();
            configs.put("useMod", String.valueOf(ConfigurationSettings.useMod));
            PacketDispatcher.sendPacketToServer(new ConfigPacket(configs)
                    .makePacket());
        } else {
            HashMap<String, String> configs = new HashMap<String, String>();
            configs.put("needsBookShelves",
                    String.valueOf(ConfigurationSettings.needsBookShelves));
            configs.put("hasLight",
                    String.valueOf(ConfigurationSettings.hasLight));
            configs.put("AllowDisenchanting",
                    String.valueOf(ConfigurationSettings.AllowDisenchanting));
            configs.put("AllowRepair",
                    String.valueOf(ConfigurationSettings.AllowRepair));
            PacketDispatcher.sendPacketToPlayer(
                    new ConfigPacket(configs).makePacket(), (Player) player);
        }
    }
}
