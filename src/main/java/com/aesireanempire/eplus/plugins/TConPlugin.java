package com.aesireanempire.eplus.plugins;

import com.aesireanempire.eplus.EnchantingPlus;
import com.aesireanempire.eplus.api.EplusPlugin;

import java.util.ArrayList;
import java.util.List;

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
