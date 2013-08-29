package eplus.plugins;

import java.util.HashMap;
import java.util.Map;

import eplus.api.EplusApi;
import eplus.api.EplusPlugin;

/**
 * @author Freyja Lesser GNU Public License v3
 *         (http://www.gnu.org/licenses/lgpl.html)
 */
@EplusPlugin
public class ThaumcraftPlugin
{

    @EplusPlugin.PreInit
    public void PreInit()
    {
        final Map<String, String> toolTips = new HashMap<String, String>();

        toolTips.put("enchantment.repair", "Consumes vis from the local aura to repair the item with this enchantment");
        toolTips.put("enchantment.charging", "Allows your \"Tool\" and \"Weapon\" wands to recharge from the local aura.");
        toolTips.put("enchantment.frugal", "Wand equivalent of unbreaking.");
        toolTips.put("enchantment.potency", "Increases damage or range that wands have.");
        toolTips.put("enchantment.haste",
                "Usable on boots and the Thaumostatic Harness only. Makes you move faster. Very effective on Boots of the Traveller, or the Thaumostatic Harness.");

        EplusApi.addCustomEnchantmentToolTip(toolTips);
    }
}
