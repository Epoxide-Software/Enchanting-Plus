package eplus.handlers;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;
import eplus.lib.References;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;

import java.util.EnumSet;

/**
 * @author Freyja Lesser GNU Public License v3
 *         (http://www.gnu.org/licenses/lgpl.html)
 */
public class VersionTickHandler implements IScheduledTickHandler {
    private boolean messageSent;

    @Override
    public void tickStart(EnumSet type, Object... tickData)
    {
        if (messageSent)
            return;

        EntityPlayer player = (EntityPlayer) tickData[0];

        if (Version.versionSeen() && Version.isVersionCheckComplete()) {
            if (Version.hasUpdated()) {
                player.func_110122_a(ChatMessageComponent.func_111066_d(String.format("[%s]: %s: %s",
                        References.MODID, "Version update is available",
                        Version.getRecommendedVersion())));
                player.func_110122_a(ChatMessageComponent.func_111066_d(String.format(
                        "[%s]: to view a changelog use: %s", References.MODID,
                        "/eplus changelog")));
            }
        }
        messageSent = true;
    }

    @Override
    public void tickEnd(EnumSet type, Object... tickData)
    {
    }

    @Override
    public EnumSet ticks()
    {
        if (this.messageSent) {
            return EnumSet.noneOf(TickType.class);
        }
        return EnumSet.of(TickType.PLAYER);
    }

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
}
