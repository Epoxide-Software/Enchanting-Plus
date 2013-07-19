package eplus.network.packets;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.Side;
import eplus.lib.ConfigurationSettings;

public class EnchantmentAllowedPacket extends BasePacket
{

    private HashMap<String, Boolean> enchantments;

    public EnchantmentAllowedPacket()
    {

    }

    public EnchantmentAllowedPacket(HashMap<String, Boolean> enchantments)
    {
        this.enchantments = enchantments;
    }

    @Override
    public void execute(EntityPlayer player, Side side)
    {
        if (side == Side.CLIENT)
        {
            ConfigurationSettings.enchantments = enchantments;
        }

    }

    @Override
    public void read(ByteArrayDataInput input)
    {
        HashMap<String, Boolean> temp = new HashMap<String, Boolean>();
        int size = input.readInt();

        for (int i = 0; i < size; i++)
        {
            temp.put(input.readUTF(), input.readBoolean());
        }
        enchantments = temp;
    }

    @Override
    protected void write(ByteArrayDataOutput output)
    {
        int size = enchantments.keySet().size();

        output.writeInt(size);

        for (String enchant : enchantments.keySet())
        {
            output.writeUTF(enchant);
            output.writeBoolean(enchantments.get(enchant));
        }

    }

}
