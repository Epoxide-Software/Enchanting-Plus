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
        if (!this.messageSent && Version.isVersionCheckComplete()) {
            this.messageSent = true;
            if (Version.hasUpdated()) {
                EntityPlayer player = (EntityPlayer) tickData[0];
                player.sendChatToPlayer("An updated version of Enchanting Plus is available: " + Version.getRecommendedVersion());
            } else if(Version.currentVersion == Version.EnumUpdateState.BETA) {
                EntityPlayer player = (EntityPlayer) tickData[0];
                player.sendChatToPlayer(String.format("§6[EPLUS]§f: Using the beta build: %s, please report all issues on the forms.", Version.getCurrentModVersion()));
            }
        }
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
        return 300;
    }
}