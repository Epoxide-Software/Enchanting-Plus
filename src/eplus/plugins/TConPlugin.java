package eplus.plugins;

import eplus.EnchantingPlus;
import eplus.api.EplusApi;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Freyja Lesser GNU Public License v3
 *         (http://www.gnu.org/licenses/lgpl.html)
 */
@EplusPlugin
public class TConPlugin {

    @EplusPlugin.PreInit
    public void preInit()
    {
        String modId = "TConstruct";

        List<Integer> itemsBlackList = new ArrayList<Integer>();

        for (Integer itemId : EnchantingPlus.itemMap.keySet()) {
            if (EnchantingPlus.itemMap.get(itemId).equalsIgnoreCase(modId)) {
                itemsBlackList.add(itemId);
            }
        }

        EplusApi.addItemToBlackList(itemsBlackList);
    }
}
