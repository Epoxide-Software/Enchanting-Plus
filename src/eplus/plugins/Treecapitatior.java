package eplus.plugins;

import eplus.api.EplusApi;
import eplus.api.EplusPlugin;

/**
 * @author Freyja Lesser GNU Public License v3
 *         (http://www.gnu.org/licenses/lgpl.html)
 */
@EplusPlugin
public class Treecapitatior
{

    @EplusPlugin.PreInit
    public void preInit()
    {
        EplusApi.addCustomEnchantmentToolTip("Treecapitating", "Allows axes not in the config to chop down trees Treecapitator style.");
    }
}
