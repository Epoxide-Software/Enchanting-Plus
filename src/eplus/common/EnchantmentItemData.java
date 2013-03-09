package eplus.common;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;

public class EnchantmentItemData extends EnchantmentData
{
    public final int shelves;

    public EnchantmentItemData(Enchantment var1, int var2, int var3)
    {
        super(var1, var2);
        shelves = var3;
    }

    public EnchantmentItemData(EnchantmentData var1, int var2)
    {
        this(var1.enchantmentobj, var1.enchantmentLevel, var2);
    }
}