package net.darkhax.eplus.libs;

import com.google.common.base.Throwables;

import net.darkhax.eplus.EnchantingPlus;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class EPlusUtils {

    public static String getEnchId (Enchantment enchant) {

        if (enchant == null) {

            EnchantingPlus.LOG.warn(String.format("Attempted to get ID for invalid enchantment. Enchantment was null."), Throwables.getStackTraceAsString(new Throwable()));
            return "N/A";
        }

        else if (enchant.getRegistryName() == null || enchant.getRegistryName().toString().isEmpty()) {

            EnchantingPlus.LOG.warn(String.format("Attempted to get ID for invalid enchantment. Enchantment lacks registry info. Class: %s Name: %s", enchant.getClass(), enchant.getName()), Throwables.getStackTraceAsString(new Throwable()));
            return "N/A";
        }

        else if (!ForgeRegistries.ENCHANTMENTS.containsValue(enchant) || !ForgeRegistries.ENCHANTMENTS.containsKey(enchant.getRegistryName())) {

            EnchantingPlus.LOG.warn(String.format("Attempted to get ID for invalid enchantment. Enchantment was not registered. Class: %s Name: %s RegID: %S", enchant.getClass(), enchant.getName(), enchant.getRegistryName().toString()), Throwables.getStackTraceAsString(new Throwable()));
            return "N/A";
        }

        return enchant.getRegistryName().toString();
    }

    public static Enchantment getEnchantmentFromId (String id) {

        if (id == null) {

            EnchantingPlus.LOG.warn(String.format("Attempted to get Enchantment for invalid ID. ID was null."), Throwables.getStackTraceAsString(new Throwable()));
            return null;
        }

        if (id.isEmpty()) {

            EnchantingPlus.LOG.warn(String.format("Attempted to get Enchantment for invalid ID. ID was empty."), Throwables.getStackTraceAsString(new Throwable()));
            return null;
        }

        final ResourceLocation idRL = new ResourceLocation(id);

        if (!ForgeRegistries.ENCHANTMENTS.containsKey(idRL)) {

            EnchantingPlus.LOG.warn(String.format("Attempted to get Enchantment for invalid ID. No enchantment registered with id %s", id), Throwables.getStackTraceAsString(new Throwable()));
            return null;
        }

        return ForgeRegistries.ENCHANTMENTS.getValue(idRL);
    }
}