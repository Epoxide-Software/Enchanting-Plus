package eplus.exceptions;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import cpw.mods.fml.client.CustomModLoadingErrorDisplayException;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class FingerprintException extends CustomModLoadingErrorDisplayException
{

    /**
     * 
     */
    private static final long serialVersionUID = -8712607652355978922L;
    private final String[] errorMessage;

    public FingerprintException(String s)
    {
        errorMessage = s.split("\n");
    }

    @Override
    public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime)
    {
        for (int i = 0; i < errorMessage.length; i++)
        {
            errorScreen.drawCenteredString(fontRenderer, errorMessage[i], errorScreen.width / 2, (errorScreen.height - fontRenderer.FONT_HEIGHT * errorMessage.length) / 2
                    + fontRenderer.FONT_HEIGHT + 10 * i, 0xffaabbcc);
        }
    }

    @Override
    public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer)
    {
    }
}
