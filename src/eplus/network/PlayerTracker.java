package eplus.network;

import cpw.mods.fml.common.IPlayerTracker;
import eplus.EnchantingPlus;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Enchanting Plus
 *
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class PlayerTracker implements IPlayerTracker {
    {
        EnchantingPlus.log.info("Initializing Player Tracker.");
    }

    @Override
    public void onPlayerLogin(EntityPlayer player) {

    }

    @Override
    public void onPlayerLogout(EntityPlayer player) {

    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player) {

    }

    @Override
    public void onPlayerRespawn(EntityPlayer player) {

    }
}
