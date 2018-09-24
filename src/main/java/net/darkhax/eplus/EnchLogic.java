package net.darkhax.eplus;

import java.util.ArrayList;
import java.util.List;

import net.darkhax.eplus.api.Blacklist;
import net.darkhax.eplus.api.event.EnchantmentCostEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public final class EnchLogic {

    public static int EnchantmentSeed;

    public static int calculateNewEnchCost (Enchantment enchantment, int level) {

        // Base cost is equal to roughly 2.5 levels of EXP.
        int cost = ConfigurationHandler.baseCost;

        // Cost is multiplied up to 10, based on rarity of the enchant.
        // Rarer the enchant, higher the cost.
        cost *= Math.max(11 - enchantment.getRarity().getWeight(), 1);

        // Linear cost increase based on level.
        cost *= level;

        // The cost factor is applied. Default is 1.5.
        cost *= ConfigurationHandler.costFactor;

        // Curses cost even more to apply
        if (enchantment.isCurse()) {

            cost *= ConfigurationHandler.curseFactor;
        }

        // Treasures cost more to apply
        else if (enchantment.isTreasureEnchantment()) {

            cost *= ConfigurationHandler.treasureFactor;
        }

        final EnchantmentCostEvent event = new EnchantmentCostEvent(cost, enchantment, level);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getCost();
    }

    public static List<Enchantment> getValidEnchantments (ItemStack stack, World world) {

        final List<Enchantment> enchList = new ArrayList<>();

        if (!stack.isEmpty() && (stack.isItemEnchantable() || stack.isItemEnchanted())) {

            for (final Enchantment enchantment : Enchantment.REGISTRY) {

                if (Blacklist.isEnchantmentBlacklisted(enchantment) || !enchantment.canApply(stack)) {

                    continue;
                }

                if (enchantment.isCurse() && EnchLogic.isWikedNight(world) || !enchantment.isTreasureEnchantment()) {

                    enchList.add(enchantment);
                }
            }
        }

        return enchList;
    }

    public static boolean isWikedNight (World world) {

        final float skyAngle = world.getCelestialAngle(1f);
        final boolean isNightRange = skyAngle > 0.40 && skyAngle < 0.60;

        return world.provider.getMoonPhase(world.getWorldTime()) == 0 && isNightRange;
    }

    // The following code was adapted from OpenModsLib https://github.com/OpenMods/OpenModsLib
    public static int getExperience (EntityPlayer player) {

        return (int) (getExperienceForLevels(player.experienceLevel) + player.experience * player.xpBarCap());
    }

    public static void removeExperience (EntityPlayer player, int amount) {

        addExperience(player, -amount);
    }

    public static void addExperience (EntityPlayer player, int amount) {

        if (amount < 1) {

            return;
        }

        final int experience = getExperience(player) + amount;
        player.experienceTotal = experience;
        player.experienceLevel = getLevelForExperience(experience);
        final int expForLevel = getExperienceForLevels(player.experienceLevel);
        player.experience = (float) (experience - expForLevel) / (float) player.xpBarCap();
    }

    public static int getExperienceForLevels (int level) {

        if (level == 0) {

            return 0;
        }

        if (level > 0 && level < 17) {

            return level * level + 6 * level;
        }

        else if (level > 16 && level < 32) {

            return (int) (2.5 * level * level - 40.5 * level + 360);
        }

        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    public static int getLevelForExperience (int experience) {

        int level = 0;

        while (getExperienceForLevels(level) <= experience) {

            level++;
        }

        return level - 1;
    }
}