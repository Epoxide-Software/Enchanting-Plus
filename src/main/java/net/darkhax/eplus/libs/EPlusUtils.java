package net.darkhax.eplus.libs;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class EPlusUtils {
    
    public static String getEnchId(Enchantment enchant) {
        
        if (enchant == null) {
            
            Constants.LOG.warn("Attempted to get ID for invalid enchantment. Enchantment was null.");
            return "N/A";
        }
        
        else if (enchant.getRegistryName() == null || enchant.getRegistryName().toString().isEmpty()) {
            
            Constants.LOG.warn("Attempted to get ID for invalid enchantment. Enchantment lacks registry info. Class: %s Name: %s", enchant.getClass(), enchant.getName());
            return "N/A";
        }
        
        else if (!ForgeRegistries.ENCHANTMENTS.containsValue(enchant) || !ForgeRegistries.ENCHANTMENTS.containsKey(enchant.getRegistryName())) {
            
            Constants.LOG.warn("Attempted to get ID for invalid enchantment. Enchantment was not registered. Class: %s Name: %s RegID: %S", enchant.getClass(), enchant.getName(), enchant.getRegistryName().toString());
            return "N/A";
        }
        
        return enchant.getRegistryName().toString();
    }
    
    public static Enchantment getEnchantmentFromId(String id) {
        
        if (id == null || id.isEmpty()) {
            
            Constants.LOG.warn("Attempted to get Enchantment for invalid ID. ID was empty or null.");
            return null;
        }
        
        final ResourceLocation idRL = new ResourceLocation(id);
        
        if (!ForgeRegistries.ENCHANTMENTS.containsKey(idRL)) {
            
            Constants.LOG.warn("Attempted to get Enchantment for invalid ID. No enchantment registered with id %s", id);
            return null;
        }
        
        return ForgeRegistries.ENCHANTMENTS.getValue(idRL);
    }
}