package com.aesireanempire.eplus.handlers;

import com.aesireanempire.eplus.lib.References;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

/**
 * @author Freyja Lesser GNU Public License v3
 *         (http://www.gnu.org/licenses/lgpl.html)
 */
public class VersionTickHandler
{
    private boolean messageSent;

    @SubscribeEvent
    public void versionNag(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (messageSent)
        {
            return;
        }

        final EntityPlayer player = event.player;

        if (Version.versionSeen() && Version.isVersionCheckComplete())
        {
            if (Version.hasUpdated())
            {
                sendChatToPlayer(player, String.format("[%s]: %s: %s", References.MODID, "Version update is available", Version.getRecommendedVersion()));
                sendChatToPlayer(player, String.format("[%s]: to view a changelog use: %s", References.MODID, "/eplus changelog"));
            }
        }
        messageSent = true;
    }

    public void sendChatToPlayer(EntityPlayer player, String message)
    {
        player.addChatComponentMessage(new ChatComponentText(message));
    }
}
