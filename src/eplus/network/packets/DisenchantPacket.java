package eplus.network.packets;

import cpw.mods.fml.relauncher.Side;
import eplus.inventory.ContainerEnchantTable;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class DisenchantPacket extends EnchantPacket {

    public DisenchantPacket() {
    }

    public DisenchantPacket(HashMap<Integer, Integer> list, int cost) {
        super(list, cost);
    }

    @Override
    public void execute(EntityPlayer player, Side side) {
        if (side.isServer()) {
            if (player.openContainer instanceof ContainerEnchantTable) {
                ((ContainerEnchantTable) player.openContainer).disenchant(player, map, cost);
                player.openContainer.detectAndSendChanges();
            }
        }
    }
}
