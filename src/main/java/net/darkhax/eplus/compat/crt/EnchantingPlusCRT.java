package net.darkhax.eplus.compat.crt;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.enchantments.IEnchantment;
import crafttweaker.api.item.IItemStack;
import net.darkhax.eplus.api.Blacklist;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.eplus.Eplus")
public class EnchantingPlusCRT {

    @ZenMethod
    public static void blacklistItem (IItemStack stack) {

        CraftTweakerAPI.apply(new ActionBlacklistItem(stack));
    }

    @ZenMethod
    public static void blacklistEnchantment (IEnchantment enchantment) {

        CraftTweakerAPI.apply(new ActionBlacklist(enchantment));
    }

    private static class ActionBlacklistItem implements IAction {

        private final IItemStack item;

        public ActionBlacklistItem (IItemStack stack) {

            this.item = stack;
        }

        @Override
        public void apply () {

            Blacklist.blacklist(new ItemStack((Item) this.item.getDefinition().getInternal()));
        }

        @Override
        public String describe () {

            return "Blacklisting " + this.item.getDisplayName() + " from E+";
        }
    }

    private static class ActionBlacklist implements IAction {

        private final IEnchantment enchantment;

        public ActionBlacklist (IEnchantment enchantment) {

            this.enchantment = enchantment;
        }

        @Override
        public void apply () {

            Blacklist.blacklist((ItemStack) this.enchantment.getDefinition().getInternal());
        }

        @Override
        public String describe () {

            return "Blacklisting " + this.enchantment.displayName() + " from E+";
        }
    }
}