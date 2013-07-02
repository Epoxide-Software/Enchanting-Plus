package eplus.network.packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.relauncher.Side;
import eplus.EnchantingPlus;
import eplus.inventory.ContainerEnchantTable;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class EnchantPacket extends BasePacket {

    protected HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    protected int cost;

    public EnchantPacket()
    {
    }

    public EnchantPacket(HashMap<Integer, Integer> list, int cost)
    {
        this.map = list;
        this.cost = cost;
    }

    @Override
    public void write(ByteArrayDataOutput output)
    {
        output.writeInt(cost);
        output.writeInt(map.size());

        for (Integer enchantmentId : map.keySet()) {
            output.writeInt(enchantmentId);
            output.writeInt(map.get(enchantmentId));
        }
    }

    @Override
    public void read(ByteArrayDataInput input)
    {
        HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
        this.cost = input.readInt();

        int size = input.readInt();

        for (int i = 0; i < size; i++) {
            temp.put(input.readInt(), input.readInt());
        }
        this.map = temp;
    }

    @Override
    public void execute(EntityPlayer player, Side side)
    {
        if (side.isServer()) {
            if (player.openContainer instanceof ContainerEnchantTable) {
                try {
                    ((ContainerEnchantTable) player.openContainer).enchant(
                            player, map, cost);
                } catch (Exception e) {
                    EnchantingPlus.log.info("Enchant failed because: "
                            + e.getMessage());
                }
                player.openContainer.detectAndSendChanges();
            }
        }
    }
}
