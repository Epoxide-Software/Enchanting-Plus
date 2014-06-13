package com.aesireanempire.eplus.helper;

import com.aesireanempire.eplus.lib.ConfigurationSettings;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by freyja
 */
public class PlayerHelper
{
    private static HashMap<String, ArrayList<Enchantment>> unlockedEnchants = new HashMap<String, ArrayList<Enchantment>>();

    public static boolean hasPlayerUnlocked(EntityPlayer player, Enchantment enchantment)
    {
        if (enchantment == null || player == null)
        {
            return false;
        }

        if (player.capabilities.isCreativeMode || ConfigurationSettings.classicMode)
        {
            return true;
        }

        return unlockedEnchants.containsKey(player.getDisplayName()) && unlockedEnchants.get(player.getDisplayName()).contains(enchantment);
    }

    public static void unlockEnchantmentForPlayer(EntityPlayer player, Enchantment enchantment)
    {
        if (enchantment == null || player == null)
        {
            return;
        }

        String playerDisplayName = player.getDisplayName();

        ArrayList<Enchantment> enchantments;

        if (unlockedEnchants.containsKey(playerDisplayName))
        {
            enchantments = unlockedEnchants.get(playerDisplayName);
        }
        else
        {
            enchantments = new ArrayList<Enchantment>();
        }

        if (!enchantments.contains(enchantment))
        {
            enchantments.add(enchantment);
        }

        unlockedEnchants.put(player.getDisplayName(), enchantments);
    }

    public static ArrayList<Enchantment> getPlayerEnchantments(EntityPlayer player)
    {
        if (unlockedEnchants.containsKey(player.getDisplayName()))
        {
            return unlockedEnchants.get(player.getDisplayName());
        }
        return new ArrayList<Enchantment>();
    }

    public static void setPlayerEnchantments(EntityPlayer player, ArrayList<Enchantment> enchantments)
    {
        unlockedEnchants.put(player.getDisplayName(), enchantments);
    }
}
