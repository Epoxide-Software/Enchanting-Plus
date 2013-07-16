package eplus.handlers;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;
import eplus.lib.References;

/**
 * @author Freyja Lesser GNU Public License v3
 *         (http://www.gnu.org/licenses/lgpl.html)
 */
public class VersionTickHandler implements IScheduledTickHandler
{
    private boolean messageSent;

    @Override
    public String getLabel()
    {
        return "Enchanting Plus update message";
    }

    @Override
    public int nextTickSpacing()
    {
        return 100;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void tickEnd(EnumSet type, Object... tickData)
    {
    }

    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    @Override
    public EnumSet ticks()
    {
        if (messageSent)
        {
            return EnumSet.noneOf(TickType.class);
        }
        return EnumSet.of(TickType.PLAYER);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void tickStart(EnumSet type, Object... tickData)
    {
        if (messageSent)
        {
            return;
        }

        final EntityPlayer player = (EntityPlayer) tickData[0];

        if (Version.versionSeen() && Version.isVersionCheckComplete())
        {
            if (Version.hasUpdated())
            {
                player.sendChatToPlayer(String.format("[%s]: %s: %s", References.MODID, "Version update is available", Version.getRecommendedVersion()));
                player.sendChatToPlayer(String.format("[%s]: to view a changelog use: %s", References.MODID, "/eplus changelog"));
            }
        }
        messageSent = true;
    }
}
