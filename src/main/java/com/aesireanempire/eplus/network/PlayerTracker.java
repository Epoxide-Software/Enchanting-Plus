package com.aesireanempire.eplus.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class PlayerTracker
{
    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        //TODO
        //PacketDispatcher.sendPacketToPlayer(new ReConfigPacket().makePacket(), (Player) event.player);
    }

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        //TODO
        //PacketDispatcher.sendPacketToPlayer(new ReConfigPacket().makePacket(), player);
        //PacketDispatcher.sendPacketToPlayer(new EnchantmentAllowedPacket(ConfigurationSettings.enchantments).makePacket(), player);
    }
}
