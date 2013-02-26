package eplus.client;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;
import eplus.common.Version;

public class LatestVersionMessage implements IScheduledTickHandler
{
    private boolean messageSent;

    @Override
    public void tickStart(EnumSet type, Object... tickData) {
        if(messageSent)
            return;

        EntityPlayer player = (EntityPlayer) tickData[0];

        if(Version.versionSeen() && Version.isVersionCheckComplete()) {
            if(Version.hasUpdated()){
                player.sendChatToPlayer(String.format("[EPLUS]: An updated version is available: %s", Version.getRecommendedVersion()));
            } else if(Version.currentVersion == Version.EnumUpdateState.BETA) {
                player.sendChatToPlayer(String.format("[EPLUS]: Using the beta build: %s, please report all issues on the forms.", Version.getCurrentModVersion()));
                player.sendChatToPlayer("Please use /eplus changlelog to see changes");
            }
        }

        messageSent = true;

    }

    @Override
    public void tickEnd(EnumSet type, Object... tickData) {
    }

    @Override
    public EnumSet ticks() {
        if (this.messageSent) {
            return EnumSet.noneOf(TickType.class);
        }
        return EnumSet.of(TickType.PLAYER);
    }

    @Override
    public String getLabel() {
        return "Enchanting Plus update message";
    }

    @Override
    public int nextTickSpacing() {
        return 100;
    }
}