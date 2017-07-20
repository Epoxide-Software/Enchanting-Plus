package net.darkhax.eplus.loot;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.darkhax.eplus.item.ItemScroll;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class EnchantScroll extends LootFunction {

    public static final String FUNC_NAME = "eplus:enchant_scroll";

    public EnchantScroll (LootCondition[] conditions) {

        super(conditions);
    }

    @Override
    public ItemStack apply (ItemStack stack, Random rand, LootContext context) {

        final Enchantment enchant = Enchantment.REGISTRY.getRandomObject(rand);

        if (!stack.isEmpty() && enchant != null) {
            stack = ItemScroll.enchantScroll(stack, enchant);
        }

        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<EnchantScroll> {

        public Serializer () {

            super(new ResourceLocation(FUNC_NAME), EnchantScroll.class);
        }

        @Override
        public void serialize (JsonObject object, EnchantScroll functionClazz, JsonSerializationContext serializationContext) {

        }

        @Override
        public EnchantScroll deserialize (JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {

            return new EnchantScroll(conditionsIn);
        }
    }
}