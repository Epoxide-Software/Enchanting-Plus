package eplus.network.packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.relauncher.Side;
import eplus.EnchantingPlus;
import eplus.inventory.ContainerEnchantTable;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class RepairPacket extends BasePacket {

    private int cost;

    public RepairPacket()
    {
    }

    public RepairPacket(int cost)
    {
        this.cost = cost;
    }

    @Override
    protected void write(ByteArrayDataOutput output)
    {
        output.writeInt(cost);
    }

    @Override
    public void read(ByteArrayDataInput input)
    {
        cost = input.readInt();
    }

    @Override
    public void execute(EntityPlayer player, Side side)
    {
        if (side.isServer()) {
            if (player.openContainer instanceof ContainerEnchantTable) {
                try {
                    ((ContainerEnchantTable) player.openContainer).repair(
                            player, cost);
                } catch (Exception e) {
                    EnchantingPlus.log.info("Enchant failed because: "
                            + e.getMessage());
                }
                player.openContainer.detectAndSendChanges();
            }
        }
    }
}
