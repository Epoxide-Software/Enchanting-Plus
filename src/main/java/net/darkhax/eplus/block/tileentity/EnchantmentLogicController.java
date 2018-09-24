package net.darkhax.eplus.block.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.darkhax.bookshelf.util.EnchantmentUtils;
import net.darkhax.eplus.EnchLogic;
import net.darkhax.eplus.inventory.ItemStackHandlerEnchant;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnchantmentLogicController {

    private final EntityPlayer player;
    private final World world;
    private final BlockPos pos;
    private final ItemStackHandlerEnchant inventory;

    private ItemStack inputStack;

    // All valid enchantments for the current item.
    private List<Enchantment> validEnchantments;

    // The original enchantment map.
    private Map<Enchantment, Integer> initialEnchantments;

    // The new enchantments that will go on the item.
    private Map<Enchantment, Integer> itemEnchantments;

    private float enchantmentPower;
    private int cost;

    public EnchantmentLogicController (EntityPlayer player, World world, BlockPos pos, ItemStackHandlerEnchant inventory) {

        this.player = player;
        this.world = world;
        this.pos = pos;
        this.inventory = inventory;

        this.validEnchantments = new ArrayList<>();
        this.initialEnchantments = new HashMap<>();
        this.itemEnchantments = new HashMap<>();

        this.onItemUpdated();
    }

    public void onItemUpdated () {

        this.inputStack = this.inventory.getEnchantingStack();

        this.initialEnchantments = EnchantmentHelper.getEnchantments(this.inputStack);
        this.itemEnchantments = new HashMap<>(this.initialEnchantments);
        this.validEnchantments = EnchLogic.getValidEnchantments(this.inputStack, this.world);
        this.calculateState();
    }

    public void calculateState () {

        this.enchantmentPower = EnchantmentUtils.getEnchantingPower(this.world, this.pos);
        this.cost = 0;

        // Calculate cost of new enchantments
        for (final Entry<Enchantment, Integer> newEntry : this.itemEnchantments.entrySet()) {

            final int original = this.initialEnchantments.getOrDefault(newEntry.getKey(), 0);
            final int newLevels = newEntry.getValue() - original;
            
            // If you want to enable giving exp back to the player, this check can be modified.
            if (newLevels > 0) {
                
                this.cost += EnchLogic.calculateNewEnchCost(newEntry.getKey(), newLevels);
            }
        }

        // Calculate cost of removed curses
        for (final Entry<Enchantment, Integer> existingEnch : this.initialEnchantments.entrySet()) {

            if (existingEnch.getKey().isCurse() && existingEnch.getValue() > 0) {

                final int currentCurseLevel = this.itemEnchantments.getOrDefault(existingEnch.getKey(), 0);

                if (currentCurseLevel < existingEnch.getValue()) {

                    this.cost += EnchLogic.calculateNewEnchCost(existingEnch.getKey(), existingEnch.getValue() - currentCurseLevel);
                }
            }
        }

        // Apply bookshelf discount

        if (this.enchantmentPower > 0) {

            this.cost -= this.getCost() * this.enchantmentPower / 100f;
        }
    }

    public int getCurrentLevel (Enchantment enchant) {

        return this.itemEnchantments.getOrDefault(enchant, 0);
    }

    public void updateEnchantment (Enchantment enchantment, int level) {

        // If the level is set to below 0, remove it from the item.
        if (level < 1) {

            this.itemEnchantments.remove(enchantment);
        }

        // If the enchantment is valid for item, update it.
        else if (this.validEnchantments.contains(enchantment)) {

            this.itemEnchantments.put(enchantment, level);
        }

        this.calculateState();
    }

    public boolean isValidEnchantment (Enchantment enchantment) {

        return this.validEnchantments.contains(enchantment);
    }

    public List<Enchantment> getValidEnchantments () {

        return this.validEnchantments;
    }

    public Map<Enchantment, Integer> getInitialEnchantments () {

        return this.initialEnchantments;
    }

    public Map<Enchantment, Integer> getCurrentEnchantments () {

        return this.itemEnchantments;
    }

    public void enchantItem () {

        // If player doesn't have enough exp, ignore them.
        if (!this.player.isCreative() && EnchLogic.getExperience(player) < this.getCost()) {

            return;
        }

        // Only creative players get charged
        if (!this.player.isCreative()) {

            EnchLogic.removeExperience(player, this.getCost());
        }

        // Clear all existing enchantments
        EnchantmentHelper.setEnchantments(new HashMap<>(), this.inputStack);

        // Apply new enchantments
        for (final Entry<Enchantment, Integer> entry : this.itemEnchantments.entrySet()) {

            if (entry.getValue() > 0) {

                this.inputStack.addEnchantment(entry.getKey(), entry.getValue());
            }
        }

        // Update the logic.
        this.onItemUpdated();
    }

    public int getCost () {

        return this.cost;
    }

    public float getEnchantmentPower () {

        return Math.min(this.enchantmentPower, 30f);
    }

    public ItemStackHandlerEnchant getInventory () {

        return this.inventory;
    }

    public BlockPos getPos () {

        return this.pos;
    }

    public World getWorld () {

        return this.world;
    }

    public EntityPlayer getPlayer () {

        return this.player;
    }
}