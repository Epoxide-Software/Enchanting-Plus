package eplus.plugins;

import java.util.ArrayList;
import java.util.List;

import eplus.EnchantingPlus;
import eplus.api.EplusPlugin;
import eplus.lib.EnchantmentHelp;

/**
 * @author Freyja Lesser GNU Public License v3
 *         (http://www.gnu.org/licenses/lgpl.html)
 */
@EplusPlugin
public class TConPlugin
{

    @EplusPlugin.PostInit
    public void postInit()
    {
        final String modId = "TConstruct";

        final List<Integer> itemsBlackList = new ArrayList<Integer>();

        for (final Integer itemId : EnchantingPlus.itemMap.keySet())
        {
            if (EnchantingPlus.itemMap.get(itemId).equalsIgnoreCase(modId))
            {
                itemsBlackList.add(itemId);
            }
        }
        
        //EnchantmentHelp.putBlackListItem(itemsBlackList);
    }
}
