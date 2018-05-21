package net.darkhax.eplus.block.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.darkhax.eplus.EnchLogic;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class EnchantmentLogicController {
    
    private TileEntityAdvancedTable table;
    
    private ItemStack inputStack;
    
    private List<Enchantment> validEnchantments;
    private Map<Enchantment, Integer> initialEnchantments;
    private Map<Enchantment, Integer> itemEnchantments;
    
    public EnchantmentLogicController (TileEntityAdvancedTable table) {
        
        this.table = table;
        this.validEnchantments = new ArrayList<>();
        this.initialEnchantments = new HashMap<>();
        this.itemEnchantments = new HashMap<>();
    }
    
    public void onItemUpdated() {
        
        inputStack = table.getItem();
        
        if (!this.inputStack.isEmpty() && (inputStack.isItemEnchantable() || this.inputStack.isItemEnchanted())) {
            
            this.initialEnchantments = EnchantmentHelper.getEnchantments(this.inputStack);
            this.itemEnchantments = new HashMap<>(this.initialEnchantments);
            this.validEnchantments = EnchLogic.getValidEnchantments(inputStack);
        }
    }
    
    public int getCurrentLevel(Enchantment enchant) {
        
        return this.itemEnchantments.get(enchant);
    }
    
    public void updateEnchantment(Enchantment enchantment, int level) {
        
        // If the level is set to below 0, remove it from the item.
        if (level < 1) {
            
            this.itemEnchantments.remove(enchantment);
        }
        
        // If the enchantment is valid for item, update it.
        else if (validEnchantments.contains(enchantment)) {
            
            this.itemEnchantments.put(enchantment, level);
        }
        
        this.table.doBlockUpdate();
    }
    
    public boolean isValidEnchantment(Enchantment enchantment) {
        
        return this.validEnchantments.contains(enchantment);
    }
    
    public List<Enchantment> getValidEnchantments() {
        
        return this.validEnchantments;
    }
    
    public Map<Enchantment, Integer> getInitialEnchantments() {
        
        return this.initialEnchantments;
    }
    
    public Map<Enchantment, Integer> getCurrentEnchantments() {
        
        return this.itemEnchantments;
    }
    
    public void enchantItem() {
        
        // Clear all existing enchantments
        EnchantmentHelper.setEnchantments(new HashMap<>(), this.inputStack);
        
        // Apply new enchantments
        for (Entry<Enchantment, Integer> entry : this.itemEnchantments.entrySet()) {
            
            if (entry.getValue() > 0) {
                
                this.inputStack.addEnchantment(entry.getKey(), entry.getValue());
            }
        }
        
        // Update the logic.
        this.onItemUpdated();
        
        // Update the block in world.
        this.table.doBlockUpdate();
    }
}