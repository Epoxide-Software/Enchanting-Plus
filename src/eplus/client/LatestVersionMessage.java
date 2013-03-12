package eplus.client;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;
import eplus.common.Version;
import eplus.common.localization.LocalizationHelper;
import net.minecraft.entity.player.EntityPlayer;

import java.util.EnumSet;

@SuppressWarnings("ALL")
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
                player.sendChatToPlayer(String.format("[EPLUS]: %s: %s", LocalizationHelper.getLocalString("version.update.available"), Version.getRecommendedVersion()));
            } else if(Version.currentVersion == Version.EnumUpdateState.BETA) {
                player.sendChatToPlayer(String.format("[EPLUS]: %s", LocalizationHelper.getLocalString("version.beta.build").replace("@MOD_VERSION@",Version.getCurrentModVersion())));
                player.sendChatToPlayer(String.format("[EPLUS]: %s: %s", LocalizationHelper.getLocalString("version.changelog.command"), "/eplus changelog"));
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