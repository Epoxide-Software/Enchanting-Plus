package eplus.network.packets;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import eplus.EnchantingPlus;
import eplus.inventory.ContainerEnchantTable;

/**
 * Enchanting Plus
 * 
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class RepairPacket extends BasePacket
{

    private int cost;
    private int amount;

    public RepairPacket()
    {
    }

    public RepairPacket(int cost, int amount)
    {
        this.cost = cost;
        this.amount = amount;
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
                    ((ContainerEnchantTable) player.openContainer).repair(player, cost, amount);
                } catch (final Exception e)
                {
                    EnchantingPlus.log.info("Repair failed because: " + e.getMessage());
                    PacketDispatcher.sendPacketToPlayer(new DisenchantErrorPacket(e.getMessage()).makePacket(), (Player) player);

                }
                player.openContainer.detectAndSendChanges();
            }
        }
    }

    @Override
    public void read(ByteArrayDataInput input)
    {
        cost = input.readInt();
    }

    @Override
    protected void write(ByteArrayDataOutput output)
    {
        output.writeInt(cost);
    }
}
