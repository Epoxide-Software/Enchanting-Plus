package eplus.network.packets;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.Side;
import eplus.EnchantingPlus;
import eplus.inventory.ContainerEnchantTable;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class EnchantPacket extends BasePacket
{

    protected HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
    protected HashMap<Integer, Integer> levels = new HashMap<Integer, Integer>();
    protected int cost;

    public EnchantPacket()
    {
    }

    public EnchantPacket(HashMap<Integer, Integer> list, HashMap<Integer,Integer> levels, int cost)
    
    {
        this.list = list;
        this.levels = levels;
        this.cost = cost;
    }

    @Override
    public void execute(EntityPlayer player, Side side)
    {
        if (side.isServer())
        {
            if (player.openContainer instanceof ContainerEnchantTable)
            {
                try
                {
                    ((ContainerEnchantTable) player.openContainer).enchant(player, list, levels, cost);
                } catch (final Exception e)
                {
                    EnchantingPlus.log.info("Enchant failed because: " + e.getMessage());
                }
                player.openContainer.detectAndSendChanges();
            }
        }
    }

    @Override
    public void read(ByteArrayDataInput input)
    {
        final HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
        final HashMap<Integer, Integer> temp2 = new HashMap<Integer, Integer>();
        cost = input.readInt();

        final int size = input.readInt();

        for (int i = 0; i < size; i++)
        {
            temp.put(input.readInt(), input.readInt());
        }
        
        final int size2 = input.readInt();
        
        for (int i = 0; i < size2; i++)
        {
            temp2.put(input.readInt(), input.readInt());
        }
        list = temp;
        levels = temp2;
    }

    @Override
    public void write(ByteArrayDataOutput output)
    {
        output.writeInt(cost);
        output.writeInt(list.size());

        for (final Integer enchantmentId : list.keySet())
        {
            output.writeInt(enchantmentId);
            output.writeInt(list.get(enchantmentId));
        }
        
        output.writeInt(levels.size());

        for (final Integer enchantmentId : levels.keySet())
        {
            output.writeInt(enchantmentId);
            output.writeInt(levels.get(enchantmentId));
        }
    }
}
