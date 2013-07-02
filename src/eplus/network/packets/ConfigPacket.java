package eplus.network.packets;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import eplus.handlers.ConfigurationHandler;
import eplus.lib.ConfigurationSettings;
import eplus.lib.References;

/**
 * Enchanting Plus
 * 
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ConfigPacket extends BasePacket
{

    HashMap<String, String> configSettings = new HashMap<String, String>();

    public ConfigPacket()
    {
    }

    public ConfigPacket(HashMap<String, String> configSettings)
    {
        this.configSettings = configSettings;
    }

    @Override
    public void execute(EntityPlayer player, Side side)
    {
        if (side.isServer())
        {
            for (final String key : configSettings.keySet())
            {
                final Field field = ReflectionHelper.findField(ConfigurationSettings.class, key);
                final Class<?> type = field.getType();
                if (type == boolean.class)
                {
                    final NBTTagCompound entityData = player.getEntityData();
                    entityData.setBoolean(References.MODID + ":" + key, Boolean.parseBoolean(configSettings.get(key)));
                }
            }
        } else
        {
            for (final String key : configSettings.keySet())
            {
                ConfigurationHandler.set(key, configSettings.get(key));
            }
        }
    }

    @Override
    public void read(ByteArrayDataInput input)
    {
        final HashMap<String, String> temp = new HashMap<String, String>();
        final int count = input.readInt();

        for (int i = 0; i < count; i++)
        {
            temp.put(input.readUTF(), input.readUTF());
        }

        configSettings = temp;
    }

    @Override
    public void write(ByteArrayDataOutput output)
    {
        output.writeInt(configSettings.size());

        for (final String key : configSettings.keySet())
        {
            output.writeUTF(key);
            output.writeUTF(configSettings.get(key));
        }
    }
}
