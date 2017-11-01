package net.darkhax.eplus.libs;

import net.minecraft.enchantment.*;

public class EnchantData extends EnchantmentData {
    
    public EnchantData(Enchantment enchantmentObj, int enchLevel) {
        super(enchantmentObj, enchLevel);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof EnchantmentData)) {
            return false;
        }
        EnchantmentData data = (EnchantmentData)obj;
        boolean equal = false;
        if(enchantment.getRegistryName().equals(data.enchantment.getRegistryName())) {
            equal = true;
        }
        if(this.enchantmentLevel == data.enchantmentLevel){
            equal = true;
        }
        return equal || super.equals(obj);
    }
    
    @Override
    public String toString() {
        return "EnchantData{" + "enchantment=" + enchantment + ", enchantmentLevel=" + enchantmentLevel + '}';
    }
}
